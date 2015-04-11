package com.neu.mrlite.common.datastructures;

import java.io.Serializable;

public class Writable implements Serializable {
    private static final long serialVersionUID = -5256545999380063772L;
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
