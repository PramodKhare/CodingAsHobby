package com.neu.mrlite.master;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.neu.io.FileSplitServer;
import com.neu.mrlite.common.ClientNode;
import com.neu.mrlite.common.Constants;
import com.neu.mrlite.common.JobConf;
import com.neu.mrlite.common.TaskConf;

public class JobScheduler implements Runnable {
    private boolean interrupt = false;
    private static JobScheduler jobSchedulerSingleton;
    private JobConf runningJob;

    private JobScheduler() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                runningJob = JobQueue.get().dequeueJob();
                ClientNodesMap slaveNodes = ClientNodesMap.get();

                // Total Reduce nodes cannot be more than total mapper nodes
                // And also cannot be more than total number of (client nodes-1)
                int totalReduceNodes = Constants.REDUCE_NODES;
                if (runningJob.getNumberOfReduceTasks() > slaveNodes.size()) {
                    totalReduceNodes = slaveNodes.size() - 1;
                } else if (runningJob.getNumberOfReduceTasks() > 0) {
                    totalReduceNodes = runningJob.getNumberOfReduceTasks();
                }

                /*********************************************************
                 * Execute Mapper Task
                 *********************************************************/
                // TODO Decide number of map and reduce nodes - based on the
                // inputFileSize

                List<JobServlet> mapNodesTasksList = new ArrayList<JobServlet>();
                // Iterate over Current Clients to send them a Mapper Task
                synchronized (slaveNodes) {
                    // Start the FileSplit Server - with total number of nodes
                    FileSplitServer.startFileSplitServer(
                            runningJob.getInputFilePath(), slaveNodes.size());
                    // TODO - Make ClientNodesMap iterable
                    for (int i = 0; i < slaveNodes.size(); i++) {
                        ClientNode node = slaveNodes.getClientNode(i);
                        if (node == null) {
                            continue;
                        } else {
                            // Create a JobServlet Thread for this Job-Task on this slave node
                            TaskConf mapTask = TaskConf.createMapperTaskConf(
                                    node, runningJob, totalReduceNodes);
                            // Maintain list of all MapTask jobservlet config objects
                            mapNodesTasksList
                                    .add(new JobServlet(node, mapTask));
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

                runningJob.setMapperTasks(mapNodesTasksList);

                // At this point all Map Tasks have been finished, lets check if
                // there is any reduce task to execute

                /*********************************************************
                 * Execute Reducer Task
                 *********************************************************/
                if (runningJob.getReducerClass() != null
                        && !runningJob.getReducerClass().trim().equals("")) {
                    List<JobServlet> reduceNodesTasksList = new ArrayList<JobServlet>();

                    // Iterate over current clients to send them a reducer Task
                    synchronized (slaveNodes) {
                        // TODO - Make ClientNodesMap iterable
                        for (int i = 0, j = 0; i < slaveNodes.size()
                                && j < totalReduceNodes; i++) {
                            ClientNode node = slaveNodes.getClientNode(i);
                            if (node == null) {
                                continue;
                            } else {
                                // create a JobServlet thread for this reduce job task on this slave node
                                TaskConf reduceTask = TaskConf
                                        .createReducerTaskConf(
                                                mapNodesTasksList, runningJob,
                                                j++);
                                // Add this thread to waitlist - list of threads to wait on - till they complete
                                reduceNodesTasksList.add(new JobServlet(node,
                                        reduceTask));
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
                            System.out
                                    .println("Unable to wait for Reduce Task - "
                                            + reduceTask.getTaskConf()
                                                    .getTaskId());
                            e.printStackTrace();
                        }
                    }
                    runningJob.setReducerTasks(reduceNodesTasksList);
                }

                System.out.println("Job Id: " + runningJob.getJobId()
                        + " is finished");
            } catch (final InterruptedException e) {
                e.printStackTrace();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
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
