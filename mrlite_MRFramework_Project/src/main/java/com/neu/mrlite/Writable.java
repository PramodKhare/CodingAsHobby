package com.neu.mrlite;

public class Writable {
	private Object obj;
	public Writable(Object obj) {
		this.obj = obj;
	}
	public <P> P cast(Class<? extends P> to) {
		return to.cast(obj);
	}
	
	public String toString() {
		return obj.toString();
	}
}
