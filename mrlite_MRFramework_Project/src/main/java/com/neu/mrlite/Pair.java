package com.neu.mrlite;

public class Pair<T1, T2> {
	public T1 key;
	public T2 value;
	public Pair() {}
	
	public Pair(T1 key, T2 value) {
		this.key = key;
		this.value = value;
	}
	
	public String toString() {
		return "{"+key+": "+value+"}";
	}
	
	@SuppressWarnings("rawtypes")
	public boolean equals(Object o) {
		if(o == null) return false;
		Pair x = (Pair) o;
		if(x == this)
			return true;
		if(x.key.equals(this.key) && x.value.equals(this.value)) {
			return true;
		}
		return false;
	}
}