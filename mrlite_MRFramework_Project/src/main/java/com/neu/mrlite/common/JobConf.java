package com.neu.mrlite.common;

import java.util.List;

import com.neu.mrlite.common.exception.InvalidJobConfException;

/**
 * Holds Job Configuration details like executable jar name, input file path,
 * output directory path and list of lib jars and main-class
 * 
 * @author Pramod Khare
 */
public class JobConf {

    private String executableJar;
    private List<String> libJars;
    private String mapperClass;
    private String reducerClass;
    private int numberOfMapTasks;
    private int numberOfReduceTasks;
    private String inputFilePath;
    private String outDirPath;

    public JobConf() {
    }

    /**
     * @param executableJar
     * @param libJars
     * @param mainClass
     * @param inputFilePath
     * @param outDirPath
     */
    public JobConf(String executableJar, List<String> libJars,
            String mapperClass, String inputFilePath, String outDirPath) {
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
    public boolean isValidJobConfiguration() throws InvalidJobConfException {
        boolean isValid = true;
        isValid = isEmpty(this.executableJar);
        isValid = isEmpty(this.mapperClass);
        isValid = isEmpty(this.inputFilePath);
        isValid = isEmpty(this.outDirPath);
        if (!isValid) {
            throw new InvalidJobConfException(
                    "Please provide executable-jar, main-class, input-file-path and output directory-path atleast.");
        }
        return true;
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

    public void setExecutableJar(String executableJar) {
        this.executableJar = executableJar;
    }

    public List<String> getLibJars() {
        return libJars;
    }

    public void setLibJars(List<String> libJars) {
        this.libJars = libJars;
    }

    public int getNumberOfMapTasks() {
        return numberOfMapTasks;
    }

    public void setNumberOfMapTasks(int numberOfMapTasks) {
        this.numberOfMapTasks = numberOfMapTasks;
    }

    public int getNumberOfReduceTasks() {
        return numberOfReduceTasks;
    }

    public void setNumberOfReduceTasks(int numberOfReduceTasks) {
        this.numberOfReduceTasks = numberOfReduceTasks;
    }

    public String getInputFilePath() {
        return inputFilePath;
    }

    public void setInputFilePath(String inputFilePath) {
        this.inputFilePath = inputFilePath;
    }

    public String getOutDirPath() {
        return outDirPath;
    }

    public void setOutDirPath(String outDirPath) {
        this.outDirPath = outDirPath;
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
}
