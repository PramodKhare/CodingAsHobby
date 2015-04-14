package com.neu.mrlite.master;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.neu.io.FileSplitServer;
import com.neu.mrlite.common.ClientNode;
import com.neu.mrlite.common.ClientNodesMap;
import com.neu.mrlite.common.Constants;
import com.neu.mrlite.common.JobConf;
import com.neu.mrlite.common.JobQueue;
import com.neu.mrlite.common.NodeIdGenerator;
import com.neu.mrlite.common.TaskConf;

/**
 * This basically polls the Jobs from the JobQueue, If JobQueue is empty then
 * blocks until it gets atleast one Job, once it has at least one job -
 * JobScheduler will remove this job and schedule it onto the Client Nodes
 * network.
 * 
 * @author Pramod Khare
 */

//TODO Make use of Logger instead of sysouts
public class JobScheduler implements Runnable {
    private boolean interrupt = false;
    private static JobScheduler jobSchedulerSingleton;
    private JobConf runningJob;

    private JobScheduler() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        // Continuous Schedule the jobs onto the Client nodes network until its forcefully interrupted  
        while (!isInterrupted()) {
            try {
                System.out.println("Waiting for new MR job ...");

                // Get the JobConf from the JobQueue
                runningJob = JobQueue.get().dequeueJob();

                // TODO Decide number of map nodes - based on the inputFileSize, 
                // Currently taking total reduce nodes == total client nodes in slave nodes network

                // Decide Total number of reduce task nodes
                estimateTotalReduceTasks();

                /*********************************************************
                 * Execute Mapper Task
                 *********************************************************/

                List<JobServlet> mapNodesTasksList = scheduleMapTasksToSlaveNodes();
                runningJob.setMapperTasks(mapNodesTasksList);

                // At this point all Map Tasks have been finished, lets check if
                // there is any reduce task to execute

                /*********************************************************
                 * Execute Reducer Task
                 *********************************************************/
                List<JobServlet> reduceNodesTasksList = null;
                if (runningJob.getReducerClass() != null
                        && !runningJob.getReducerClass().trim().equals("")) {
                    reduceNodesTasksList = scheduleReduceTasksToSlaveNodes(mapNodesTasksList);
                }
                runningJob.setReducerTasks(reduceNodesTasksList);

                System.out.println("Job Id: " + runningJob.getJobId()
                        + " is finished");
            } catch (final InterruptedException e) {
                e.printStackTrace();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Execute any user-defined reduce tasks
     * 
     * @throws IOException
     */
    private List<JobServlet> scheduleReduceTasksToSlaveNodes(
            List<JobServlet> mapNodesTasksList) throws IOException {
        List<JobServlet> reduceNodesTasksList = new ArrayList<JobServlet>();
        ClientNodesMap slaveNodes = ClientNodesMap.get();

        // Precondition: This is already estimated and updated using estimateTotalReduceTasks() method
        int totalReduceNodes = runningJob.getNumberOfReduceTasks();

        // Iterate over current clients to send them a reducer Task
        synchronized (slaveNodes) {
            // TODO - Make ClientNodesMap iterable, rather than looping over it
            int maxNumberOfNodes = NodeIdGenerator.currentNodeId();
            for (int i = 0, j = 0; (i < maxNumberOfNodes)
                    && (j < totalReduceNodes); i++) {
                ClientNode node = slaveNodes.getClientNode(i);
                if (node == null) {
                    continue;
                } else {
                    // create a JobServlet thread for this reduce job task on this slave node
                    TaskConf reduceTask = TaskConf.createReducerTaskConf(
                            mapNodesTasksList, runningJob, j++);
                    // Add this thread to waitlist - list of threads to wait on - till they complete
                    reduceNodesTasksList.add(new JobServlet(node, reduceTask));
                }
            }
        }
        // Wait till all Reduce Nodes finish their tasks, and stream their output to outputDir

        for (JobServlet reduceTask : reduceNodesTasksList) {
            try {
                if (reduceTask.isAlive()) {
                    reduceTask.join();
                }
            } catch (InterruptedException e) {
                System.out.println("Unable to wait for Reduce Task - "
                        + reduceTask.getTaskConf().getTaskId());
                e.printStackTrace();
            }
        }
        runningJob.setReducerTasks(reduceNodesTasksList);
        return reduceNodesTasksList;
    }

    /**
     * Schedule Map task - to individual slave nodes in the network, this method
     * will wait until all Map-Tasks Finish
     * 
     * @return
     * @throws IOException
     */
    private List<JobServlet> scheduleMapTasksToSlaveNodes() throws IOException {
        ClientNodesMap slaveNodes = ClientNodesMap.get();
        List<JobServlet> mapNodesTasksList = new ArrayList<JobServlet>();

        // Precondition: This is already estimated and updated using estimateTotalReduceTasks() method
        int totalReduceNodes = runningJob.getNumberOfReduceTasks();

        // Iterate over Current Clients to send them a Mapper Task
        synchronized (slaveNodes) {
            // Start the FileSplit Server - with total number of nodes
            FileSplitServer.startFileSplitServer(runningJob.getInputFilePath(),
                    slaveNodes.size());

            // TODO - Make ClientNodesMap iterable, rather than looping over it
            int maxNumberOfNodes = NodeIdGenerator.currentNodeId();
            for (int i = 0; i < maxNumberOfNodes; i++) {
                ClientNode node = slaveNodes.getClientNode(i);
                if (node == null) {
                    continue;
                } else {
                    // Create a JobServlet Thread for this Job-Task on this slave node
                    TaskConf mapTask = TaskConf.createMapperTaskConf(node,
                            runningJob, totalReduceNodes);
                    // Maintain list of all MapTask jobservlet config objects
                    mapNodesTasksList.add(new JobServlet(node, mapTask));
                }
            }
        }

        /* Now wait till all the map tasks complete */
        for (JobServlet mapTask : mapNodesTasksList) {
            try {
                if (mapTask.isAlive()) {
                    mapTask.join();
                }
            } catch (InterruptedException e) {
                System.out.println("Unable to wait for Map Task - "
                        + mapTask.getTaskConf().getTaskId());
                e.printStackTrace();
            }
        }
        return mapNodesTasksList;
    }

    /**
     * Total Reduce nodes cannot be more than total mapper nodes, And also
     * cannot be more than total available number of (client nodes-1)
     * 
     * @return
     */
    private int estimateTotalReduceTasks() {
        ClientNodesMap slaveNodes = ClientNodesMap.get();
        int totalReduceNodes = Constants.REDUCE_NODES;
        if (runningJob.getNumberOfReduceTasks() > slaveNodes.size()) {
            totalReduceNodes = (slaveNodes.size() < 2) ? 1
                    : (slaveNodes.size() - 1);
        } else if (runningJob.getNumberOfReduceTasks() > 0) {
            totalReduceNodes = runningJob.getNumberOfReduceTasks();
        }
        runningJob.setNumberOfReduceTasks(totalReduceNodes);
        return totalReduceNodes;
    }

    public void interrupt() {
        interrupt = true;
    }

    public boolean isInterrupted() {
        return interrupt == true;
    }

    public static synchronized void startJobScheduler() throws Exception {
        if (jobSchedulerSingleton == null) {
            jobSchedulerSingleton = new JobScheduler();
        }
    }

    public static synchronized void stopJobScheduler() throws Exception {
        if (jobSchedulerSingleton != null) {
            jobSchedulerSingleton.interrupt();
        }
        jobSchedulerSingleton = null;
    }
}
