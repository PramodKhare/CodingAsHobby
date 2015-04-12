package com.neu.mrlite.common.datastructures;

/**
 * A generic callback method for which the Assortment has a complete knowledge
 * of which methods to execute to perform the required operation (process)
 * 
 * @author nikit T determines what type of data does the callback processes
 * @param <T>
 */
public abstract class IOCallback extends POCallback {
    /**
     * Implementation of specific operations will go here. User will define
     * their custom methods to execute on API an operation call.
     * 
     * @param data
     */
    public abstract void process(IOHandle io);
}