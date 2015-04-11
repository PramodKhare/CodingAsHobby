package com.neu.mrlite.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.neu.mrlite.common.exception.InvalidJobConfException;
import com.neu.mrlite.master.JobServlet;

/**
 * Holds Job Configuration details like executable jar name, input file path,
 * output directory path and list of lib jars and main-class
 * 
 * @author Pramod Khare
 */
public class JobConf implements Serializable {
    private static final long serialVersionUID = -3551935659518505726L;

    private String jobId;
    private String executableJar;
    private List<String> libJars;
    private String mapperClass;
    private String reducerClass;
    private int numberOfMapTasks;
    private int numberOfReduceTasks;
    private String inputFilePath;
    private String outDirPath;
    private boolean isCompleted = false;
    private long jobStartTime;
    private long jobEndTime;
    private long jobQueuedTime;
    private long timeToCompleteMapPhase;
    private long timeToCompleteReducePhase;
    private List<TaskConf> mapperTasks;
    private List<TaskConf> reducerTasks;

    public JobConf() {
    }

    /**
     * @param executableJar
     * @param libJars
     * @param mainClass
     * @param inputFilePath
     * @param outDirPath
     */
    public JobConf(final String executableJar, final List<String> libJars,
            final String mapperClass, final String inputFilePath,
            final String outDirPath) {
        super();
        this.executableJar = executableJar;
        this.libJars = libJars;
        this.mapperClass = mapperClass;
        this.inputFilePath = inputFilePath;
        this.outDirPath = outDirPath;
    }

    /**
     * Validate if all required attributes for a job configuration are given, so
     * that a job can be successfully started
     * 
     * @return boolean true value when configurations are correct
     * @throws InvalidJobConfException
     *             otherwise
     */
    public synchronized boolean isValidJobConfiguration()
            throws InvalidJobConfException {
        boolean isValid = true;
        isValid = isValid && !isEmpty(this.executableJar);
        isValid = isValid && !isEmpty(this.mapperClass);
        isValid = isValid && !isEmpty(this.inputFilePath);
        isValid = isValid && !isEmpty(this.outDirPath);
        if (!isValid) {
            throw new InvalidJobConfException(
                    "Please provide executable-jar, mapper-class, reducer-class, input-file-path and output directory-path atleast.");
        }
        // Generate a unique timestamp for jobId
        this.setJobId("Job_" + System.currentTimeMillis());
        return isValid;
    }

    // Checks if input string is empty
    private boolean isEmpty(final String input) {
        return (input == null || input.trim().length() == 0);
    }

    /***************************************************************************
     * Getters And Setters
     ***************************************************************************/

    public String getExecutableJar() {
        return executableJar;
    }

    public void setExecutableJar(final String executableJar) {
        this.executableJar = executableJar;
    }

    public List<String> getLibJars() {
        return libJars;
    }

    public void setLibJars(final List<String> libJars) {
        this.libJars = libJars;
    }

    public int getNumberOfMapTasks() {
        return numberOfMapTasks;
    }

    public void setNumberOfMapTasks(final int numberOfMapTasks) {
        this.numberOfMapTasks = numberOfMapTasks;
    }

    public int getNumberOfReduceTasks() {
        return numberOfReduceTasks;
    }

    public void setNumberOfReduceTasks(final int numberOfReduceTasks) {
        this.numberOfReduceTasks = numberOfReduceTasks;
    }

    public String getInputFilePath() {
        return inputFilePath;
    }

    public void setInputFilePath(final String inputFilePath) {
        this.inputFilePath = inputFilePath;
    }

    public String getOutDirPath() {
        return outDirPath;
    }

    public void setOutDirPath(final String outDirPath) {
        this.outDirPath = outDirPath;
    }

    public String getMapperClass() {
        return mapperClass;
    }

    public void setMapperClass(final String mapperClass) {
        this.mapperClass = mapperClass;
    }

    public String getReducerClass() {
        return reducerClass;
    }

    public void setReducerClass(final String reducerClass) {
        this.reducerClass = reducerClass;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(final boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(final String jobId) {
        this.jobId = jobId;
    }

    public long getJobStartTime() {
        return jobStartTime;
    }

    public void setJobStartTime(final long jobStartTime) {
        this.jobStartTime = jobStartTime;
    }

    public long getJobEndTime() {
        return jobEndTime;
    }

    public void setJobEndTime(final long jobEndTime) {
        this.jobEndTime = jobEndTime;
    }

    public long getJobQueuedTime() {
        return jobQueuedTime;
    }

    public void setJobQueuedTime(final long jobQueuedTime) {
        this.jobQueuedTime = jobQueuedTime;
    }

    public long getTimeToCompleteMapPhase() {
        return timeToCompleteMapPhase;
    }

    public void setTimeToCompleteMapPhase(final long timeToCompleteMapPhase) {
        this.timeToCompleteMapPhase = timeToCompleteMapPhase;
    }

    public long getTimeToCompleteReducePhase() {
        return timeToCompleteReducePhase;
    }

    public void setTimeToCompleteReducePhase(
            final long timeToCompleteReducePhase) {
        this.timeToCompleteReducePhase = timeToCompleteReducePhase;
    }

    public List<TaskConf> getMapperTasks() {
        return mapperTasks;
    }

    /**
     * Replaces any previous value with new list's TaskConfs list
     * 
     * @param mapTasks
     */
    public void setMapperTasks(final List<JobServlet> mapTasks) {
        if (mapTasks != null && !mapTasks.isEmpty()) {
            this.mapperTasks = new ArrayList<TaskConf>();
            for (JobServlet mapTask : mapTasks) {
                this.mapperTasks.add(mapTask.getTaskConf());
            }
        }
    }

    public List<TaskConf> getReducerTasks() {
        return reducerTasks;
    }

    /**
     * Replaces any previous value with new list's TaskConfs list
     * 
     * @param mapTasks
     */
    public void setReducerTasks(final List<JobServlet> reduceTasks) {
        if (reduceTasks != null && !reduceTasks.isEmpty()) {
            this.reducerTasks = new ArrayList<TaskConf>();
            for (JobServlet reduceTask : reduceTasks) {
                this.reducerTasks.add(reduceTask.getTaskConf());
            }
        }
    }

    @Override
    public String toString() {
        return this.serializeToJson();
    }

    public String serializeToJson() {
        final Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static JobConf deserializeFromJson(final String jsonStr)
            throws JsonSyntaxException {
        final Gson gson = new Gson();
        return gson.fromJson(jsonStr, JobConf.class);
    }
}
