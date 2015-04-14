package com.neu.mrlite.master;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.neu.mrlite.common.ClientNode;
import com.neu.mrlite.common.Constants;
import com.neu.mrlite.common.JobConf;
import com.neu.mrlite.common.MapperNodeInfo;
import com.neu.mrlite.common.TaskConf;

public class TaskConfFactory implements Serializable {
    private static final long serialVersionUID = 4181046259966279617L;

    private static int mapTaskCounter = 0;
    private static int reduceTaskCounter = 0;

    private TaskConfFactory() {
    }

    public static int getMapTaskCounter() {
        return mapTaskCounter;
    }

    public static void setMapTaskCounter(int mapTaskCounter) {
        TaskConfFactory.mapTaskCounter = mapTaskCounter;
    }

    public static int getReduceTaskCounter() {
        return reduceTaskCounter;
    }

    public static void setReduceTaskCounter(int reduceTaskCounter) {
        TaskConfFactory.reduceTaskCounter = reduceTaskCounter;
    }

    public static synchronized TaskConf createMapperTaskConf(
            final ClientNode node, final JobConf jobConf,
            final int totalReduceNodes) {
        TaskConf taskConf = new TaskConf();

        /* Common Task Properties */
        taskConf.setExecutableJar(jobConf.getExecutableJar());
        taskConf.setInputFilePath(jobConf.getInputFilePath());
        taskConf.setLibJars(jobConf.getLibJars());
        taskConf.setTaskId(jobConf.getJobId() + "_task_map_" + mapTaskCounter++);

        /* Mapper Properties */
        taskConf.setMapperTask(true);
        taskConf.setMapperClass(jobConf.getMapperClass());
        taskConf.setIoHandleServerIp(node.getClientSocket().getLocalAddress()
                .getHostAddress());
        taskConf.setNumberOfReduceTasks(totalReduceNodes);
        return taskConf;
    }

    public static synchronized TaskConf createReducerTaskConf(
            final ClientNode node, final List<JobServlet> mapNodeJobServlets,
            final JobConf jobConf, final int mapPartitionToCopy) {
        TaskConf taskConf = new TaskConf();

        /* Common Task Properties */
        taskConf.setExecutableJar(jobConf.getExecutableJar());
        taskConf.setInputFilePath(jobConf.getInputFilePath());
        taskConf.setLibJars(jobConf.getLibJars());
        taskConf.setTaskId(jobConf.getJobId() + "_task_reduce_"
                + reduceTaskCounter++);

        /* Reducer Properties */
        taskConf.setMapperTask(false);
        taskConf.setReducerClass(jobConf.getReducerClass());
        taskConf.setIoHandleServerIp(node.getClientSocket().getLocalAddress()
                .getHostAddress());
        taskConf.setOutDirPath(jobConf.getOutDirPath());

        /* Create MapperNodeInfo for each mapper jobservlet */
        taskConf.setMapperNodeInfo(createMapperNodesInfoList(mapNodeJobServlets));

        // This Reducer Node will stream the Mapper outputs from each map node - only the given partition
        taskConf.setPartitionToStreamForReducer(mapPartitionToCopy);
        return taskConf;
    }

    private static List<MapperNodeInfo> createMapperNodesInfoList(
            final List<JobServlet> mapNodeJobServlets) {
        if (mapNodeJobServlets == null || mapNodeJobServlets.isEmpty()) {
            return null;
        }
        final List<MapperNodeInfo> mapInfoList = new ArrayList<MapperNodeInfo>();

        for (JobServlet mapTask : mapNodeJobServlets) {
            MapperNodeInfo mapNodeInfo = new MapperNodeInfo();

            // Task Id for map-task is required for getting value from In-Memory-Store
            mapNodeInfo.setMapTaskId(mapTask.getTaskConf().getTaskId());

            // Store the remote client's IP address
            mapNodeInfo.setShuffleSocketIp(mapTask.getClientSocket()
                    .getInetAddress().getHostAddress());

            // Get the Remote Client's NodeId - from which we can calculate -MapoutputServer's port number
            mapNodeInfo.setShuffleSocketPort(Constants.BASE_MAP_SERVER_PORT
                    + mapTask.getClientNodeId());

            mapInfoList.add(mapNodeInfo);
        }

        return mapInfoList;
    }
}
