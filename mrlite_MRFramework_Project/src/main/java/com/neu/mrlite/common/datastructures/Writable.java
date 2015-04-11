package com.neu.mrlite.common.datastructures;

import java.io.Serializable;

public abstract class Writable implements Serializable, Comparable<Writable> {
    private static final long serialVersionUID = 7198028059909547957L;

    private Object obj;

    public Writable(Object obj) {
        this.obj = obj;
    }

    public <P> P cast(Class<? extends P> to) {
        return to.cast(obj);
    }

    @Override
    public String toString() {
        return this.serializeToJson();
    }

    public abstract String serializeToJson();

    public abstract Writable deserializeFromJson(final String jsonStr)
            throws Exception;

    public abstract int compareTo(Writable o);
}
