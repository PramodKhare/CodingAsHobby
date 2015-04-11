package com.neu.mrlite.common;

import java.io.Serializable;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.neu.mrlite.master.JobServer;

public class TaskConf implements Serializable {
    private static final long serialVersionUID = 4610481245402892811L;
    private static int taskCounter = 0;

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
    private String ioHandleServerUrl;

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

    public String getIoHandleServerUrl() {
        return ioHandleServerUrl;
    }

    public void setIoHandleServerUrl(String ioHandleServerUrl) {
        this.ioHandleServerUrl = ioHandleServerUrl;
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
            final ClientNode node, final JobConf jobConf) {
        TaskConf taskConf = new TaskConf();

        /* Common Task Properties */
        taskConf.setExecutableJar(jobConf.getExecutableJar());
        taskConf.setInputFilePath(jobConf.getInputFilePath());
        taskConf.setLibJars(jobConf.getLibJars());
        taskConf.setTaskId(jobConf.getJobId() + "_task_map" + taskCounter++);

        /* Mapper Properties */
        taskConf.setMapperTask(true);
        taskConf.setMapperClass(jobConf.getMapperClass());
        taskConf.setIoHandleServerUrl(node.getClientSocket().getLocalAddress()
                .toString());
        taskConf.setNumberOfReduceTasks(jobConf.getNumberOfReduceTasks() < 1 ? 1
                : jobConf.getNumberOfReduceTasks());
        return taskConf;
    }

    public static synchronized TaskConf createReducerTaskConf(
            final List<JobServer> nodes, final JobConf jobConf) {
        TaskConf taskConf = new TaskConf();
        /* Common Task Properties */
        taskConf.setExecutableJar(jobConf.getExecutableJar());
        taskConf.setInputFilePath(jobConf.getInputFilePath());
        taskConf.setLibJars(jobConf.getLibJars());
        taskConf.setTaskId(jobConf.getJobId() + "_task_reduce" + taskCounter++);

        /* Reducer Properties */
        taskConf.setMapperTask(false);
        taskConf.setReducerClass(jobConf.getReducerClass());
        // taskConf.setIoHandleServerUrl(node.getClientSocket().getLocalAddress().toString());
        taskConf.setOutDirPath(jobConf.getOutDirPath());

        /* Create MapperNodeInfo for each mapper jobservlet */ 

        return taskConf;
    }
}
