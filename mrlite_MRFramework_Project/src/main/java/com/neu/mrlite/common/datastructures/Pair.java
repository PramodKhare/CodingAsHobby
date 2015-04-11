package com.neu.mrlite.common.datastructures;

import java.io.Serializable;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class Pair<K extends Writable, V extends Writable> implements
        Serializable, Comparable<Pair<K, V>> {
    private static final long serialVersionUID = -6164524333667124662L;
    private K key;
    private V value;

    public Pair(final K key, final V value) {
        this.key = key;
        this.value = value;
    }

    @SuppressWarnings("rawtypes")
    public boolean equals(Object o) {
        if (o == null)
            return false;
        Pair x = (Pair) o;
        if (x == this)
            return true;
        if (x.key.equals(this.key) && x.value.equals(this.value)) {
            return true;
        }
        return false;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.serializeToJson();
    }

    public String serializeToJson() {
        final Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static Pair deserializeFromJson(final String jsonStr)
            throws JsonSyntaxException {
        final Gson gson = new Gson();
        return gson.fromJson(jsonStr, Pair.class);
    }

    @Override
    public int compareTo(Pair<K, V> anotherPair) {
        if (this.equals(anotherPair)) {
            return 0;
        } else {
            return (this.getKey().compareTo(anotherPair.getKey()));
        }
    }
}