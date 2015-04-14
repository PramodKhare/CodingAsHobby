package com.neu.mrlite.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.neu.mrlite.master.JobServlet;

public class TaskConf implements Serializable {
    private static final long serialVersionUID = 4610481245402892811L;

    private static int mapTaskCounter = 0;
    private static int reduceTaskCounter = 0;

    /* Common Task Properties */
    private String taskId;
    private boolean isMapperTask = true;
    private String executableJar;
    private List<String> libJars;
    private String inputFilePath;

    /* Mapper Task Properties */
    private String paritionerClass;
    private int numberOfReduceTasks = 1;
    private String mapperClass;
    private String ioHandleServerIp;

    /* Reducer Task Properties */
    private String reducerClass;
    private String outDirPath;
    private List<MapperNodeInfo> mapperNodeInfo;

    // Partition number to stream from map-node for reducer task
    private int partitionToStreamForReducer = 0;

    private TaskConf() {
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public boolean isMapperTask() {
        return isMapperTask;
    }

    public void setMapperTask(boolean isMapperTask) {
        this.isMapperTask = isMapperTask;
    }

    public String getExecutableJar() {
        return executableJar;
    }

    public void setExecutableJar(String executableJar) {
        this.executableJar = executableJar;
    }

    public List<String> getLibJars() {
        return libJars;
    }

    public void setLibJars(List<String> libJars) {
        this.libJars = libJars;
    }

    public String getInputFilePath() {
        return inputFilePath;
    }

    public void setInputFilePath(String inputFilePath) {
        this.inputFilePath = inputFilePath;
    }

    public String getMapperClass() {
        return mapperClass;
    }

    public void setMapperClass(String mapperClass) {
        this.mapperClass = mapperClass;
    }

    public String getIoHandleServerIp() {
        return ioHandleServerIp;
    }

    public void setIoHandleServerIp(String ioHandleServerIp) {
        this.ioHandleServerIp = ioHandleServerIp;
    }

    public String getReducerClass() {
        return reducerClass;
    }

    public void setReducerClass(String reducerClass) {
        this.reducerClass = reducerClass;
    }

    public String getOutDirPath() {
        return outDirPath;
    }

    public void setOutDirPath(String outDirPath) {
        this.outDirPath = outDirPath;
    }

    public List<MapperNodeInfo> getMapperNodeInfo() {
        return mapperNodeInfo;
    }

    public void setMapperNodeInfo(List<MapperNodeInfo> mapperNodeInfo) {
        this.mapperNodeInfo = mapperNodeInfo;
    }

    public int getPartitionToStreamForReducer() {
        return partitionToStreamForReducer;
    }

    public void setPartitionToStreamForReducer(int partitionToStreamForReducer) {
        this.partitionToStreamForReducer = partitionToStreamForReducer;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getParitionerClass() {
        return paritionerClass;
    }

    public void setParitionerClass(final String paritionerClass) {
        this.paritionerClass = paritionerClass;
    }

    public int getNumberOfReduceTasks() {
        return numberOfReduceTasks;
    }

    public void setNumberOfReduceTasks(int numberOfReduceTasks) {
        this.numberOfReduceTasks = numberOfReduceTasks;
    }

    public static int getMapTaskCounter() {
        return mapTaskCounter;
    }

    public static void setMapTaskCounter(int mapTaskCounter) {
        TaskConf.mapTaskCounter = mapTaskCounter;
    }

    public static int getReduceTaskCounter() {
        return reduceTaskCounter;
    }

    public static void setReduceTaskCounter(int reduceTaskCounter) {
        TaskConf.reduceTaskCounter = reduceTaskCounter;
    }

    public String serializeToJson() {
        final Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static TaskConf deserializeFromJson(final String jsonStr)
            throws JsonSyntaxException {
        final Gson gson = new Gson();
        return gson.fromJson(jsonStr, TaskConf.class);
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
