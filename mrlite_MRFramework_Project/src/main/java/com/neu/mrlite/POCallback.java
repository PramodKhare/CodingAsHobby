package com.neu.mrlite;
/**
 * A generic callback method for which the Assortment
 * has a complete knowledge of which methods to execute
 * to perform the required operation (process)
 * @author nikit
 * T determines what type of data does the callback processes
 * @param <T>
 */
public abstract class POCallback{
	/**
	 * naive emit result collectors
	 */
	private Object key;
	private Object value;
	
	/**
	 * Implementation of specific operations will go here.
	 * User will define their custom methods to execute
	 * on API an operation call.
	 * @param data
	 */
	public abstract void process(Writable data);
	
	/**
	 * output of the process method needs to be collected
	 * through this method. if output is only a single value.
	 * @param value
	 */
	public void emit(Object value) {
		this.value = value;
	}
	
	/**
	 * output of the process execution needs to be collected
	 * through this method in order. if output is a key value
	 * pair
	 * @param value
	 */
	public void emit(Object key, Object value) {
		this.key = key;
		this.value = value;
	}
	
	/**
	 * collector handle for Assortment to get the emitted key
	 * @return key
	 */
	public Object getKey() {
		return key;
	}
	
	/**
	 * collector handle for Assortment to get the emitted value
	 * @return value
	 */
	public Object getValue() {
		return value;
	}
}