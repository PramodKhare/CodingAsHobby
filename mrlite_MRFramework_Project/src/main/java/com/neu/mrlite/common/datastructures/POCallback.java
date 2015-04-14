package com.neu.mrlite.common.datastructures;

/**
 * A generic callback method for which the Assortment has a complete knowledge
 * of which methods to execute to perform the required operation (process)
 * 
 * @author nikit T determines what type of data does the callback processes
 * @param <T>
 */
public abstract class POCallback<X extends Comparable<X>, Y, K extends Comparable<K>, V> {
    /**
     * naive emit result collectors
     */
    private K key;
    private V value;

    /**
     * Implementation of specific operations will go here. User will define
     * their custom methods to execute on API an operation call.
     * 
     * @param data
     */
    public abstract void process(Pair<X, Y> data);

    /**
     * output of the process execution needs to be collected through this method
     * in order. if output is a key value pair
     * 
     * @param value
     */
    public void emit(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * collector handle for Assortment to get the emitted key
     * 
     * @return key
     */
    public K getKey() {
        return key;
    }

    /**
     * collector handle for Assortment to get the emitted value
     * 
     * @return value
     */
    public V getValue() {
        return value;
    }
}