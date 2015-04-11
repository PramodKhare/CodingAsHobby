package com.neu.mrlite.common;

import java.util.List;

public class TaskConf {
    private long jobNumber;
    private String taskName;
    private String ioHandleServerUrl;
    private boolean isMapperTask = true;
    private List<ClientNode> mapperNodes;
    private int reducePartitionNumber = 0;

    private String executableJar;
    private List<String> libJars;
    private String mapperClass;
    private String reducerClass;
    private String outDirPath;
    private String paritionerClass;

    public TaskConf() {
    }

    public long getJobNumber() {
        return jobNumber;
    }

    public void setJobNumber(long jobNumber) {
        this.jobNumber = jobNumber;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getIoHandleServerUrl() {
        return ioHandleServerUrl;
    }

    public void setIoHandleServerUrl(String ioHandleServerUrl) {
        this.ioHandleServerUrl = ioHandleServerUrl;
    }

    public boolean isMapperTask() {
        return isMapperTask;
    }

    public void setMapperTask(boolean isMapperTask) {
        this.isMapperTask = isMapperTask;
    }

    public List<ClientNode> getMapperNodes() {
        return mapperNodes;
    }

    public void setMapperNodes(List<ClientNode> mapperNodes) {
        this.mapperNodes = mapperNodes;
    }

    public int getReducePartitionNumber() {
        return reducePartitionNumber;
    }

    public void setReducePartitionNumber(int reducePartitionNumber) {
        this.reducePartitionNumber = reducePartitionNumber;
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

    public String getMapperClass() {
        return mapperClass;
    }

    public void setMapperClass(String mapperClass) {
        this.mapperClass = mapperClass;
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

    public String getParitionerClass() {
        return paritionerClass;
    }

    public void setParitionerClass(String paritionerClass) {
        this.paritionerClass = paritionerClass;
    }
}
