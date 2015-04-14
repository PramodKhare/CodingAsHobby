package com.neu.mrlite.common;

/**
 * This basically generates unique Ids for each client node
 * 
 * @author Pramod Khare
 */
public class NodeIdGenerator {
    private static int nodeId = 0;

    private NodeIdGenerator() {
    }

    public static synchronized int generateNodeId() {
        return nodeId++;
    }

    public static synchronized int currentNodeId() {
        return nodeId;
    }
}
