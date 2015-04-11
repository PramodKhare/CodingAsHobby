package com.neu.mrlite.common;

import java.io.Serializable;

public class MapperNodeInfo implements Serializable {
    private static final long serialVersionUID = -1675171346606975675L;
    private String shuffleSocketIp;
    private int shuffleSocketPort;
    private String mapTaskId;

    public String getShuffleSocketIp() {
        return shuffleSocketIp;
    }

    public void setShuffleSocketIp(String shuffleSocketIp) {
        this.shuffleSocketIp = shuffleSocketIp;
    }

    public int getShuffleSocketPort() {
        return shuffleSocketPort;
    }

    public void setShuffleSocketPort(int shuffleSocketPort) {
        this.shuffleSocketPort = shuffleSocketPort;
    }

    public String getMapTaskId() {
        return mapTaskId;
    }

    public void setMapTaskId(String mapTaskId) {
        this.mapTaskId = mapTaskId;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}
