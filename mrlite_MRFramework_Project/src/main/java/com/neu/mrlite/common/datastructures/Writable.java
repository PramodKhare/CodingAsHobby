package com.neu.mrlite.common.datastructures;

import java.io.Serializable;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class Writable<T> implements Serializable {
    // , Comparable<Writable> {
    private static final long serialVersionUID = 7198028059909547957L;

    public T obj;

    public Writable() {
    }

    public Writable(T obj) {
        this.obj = obj;
    }

    public <P> P cast(Class<? extends P> to) {
        return to.cast(obj);
    }

    @Override
    public String toString() {
        return this.serializeToJson();
    }

    public String serializeToJson() {
        final Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static Writable deserializeFromJson(final String jsonStr)
            throws JsonSyntaxException {
        final Gson gson = new Gson();
        return gson.fromJson(jsonStr, Writable.class);
    }

    // public abstract int compareTo(Writable o);
}
