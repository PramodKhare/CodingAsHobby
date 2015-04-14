package com.neu.mrlite.common;

import java.io.Serializable;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.neu.mrlite.common.datastructures.Mapper;
import com.neu.mrlite.common.datastructures.Reducer;
import com.neu.mrlite.common.exception.InvalidJobConfException;

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
    private int numberOfReduceTasks = 1;
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

    public void setMapperClass(final Class<? extends Mapper> mapperClass) {
        this.mapperClass = mapperClass.getName();
    }

    public String getReducerClass() {
        return reducerClass;
    }

    public void setReducerClass(final Class<? extends Reducer> reducerClass) {
        this.reducerClass = reducerClass.getName();
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

    public List<TaskConf> getReducerTasks() {
        return reducerTasks;
    }

    public void setMapperTasks(List<TaskConf> mapperTasks) {
        this.mapperTasks = mapperTasks;
    }

    public void setReducerTasks(List<TaskConf> reducerTasks) {
        this.reducerTasks = reducerTasks;
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

    /**
     * Adds the itself (JobConf) into JobQueue list
     * 
     * @return
     * @throws InvalidJobConfException
     */
    public boolean scheduleJob() throws InvalidJobConfException {
        if (this.isValidJobConfiguration()) {
            JobQueue.get().queueJob(this);
        }
        return true;
    }
}
