package com.neu.mrlite.master;

import java.util.LinkedHashMap;
import java.util.Map;

import com.neu.mrlite.common.ClientNode;

public class ClientNodesMap {
    public final Map<Integer, ClientNode> nodes;

    private ClientNodesMap() {
        nodes = new LinkedHashMap<Integer, ClientNode>();
    }

    /**
     * Create Singleton Nodes Map
     */
    private static class SingletonHelper {
        private static final ClientNodesMap INSTANCE = new ClientNodesMap();
    }

    public static ClientNodesMap get() {
        return SingletonHelper.INSTANCE;
    }

    /**
     * Add new ClientNode in the nodes map
     * 
     * @param node
     * @return
     */
    public ClientNode addNewClientNode(final ClientNode node) {
        return nodes.put(node.getNodeId(), node);
    }

    /**
     * Removes the ClientNode from the map
     * 
     * @param nodeId
     * @return
     */
    public ClientNode removeClientNode(final Integer nodeId) {
        return nodes.remove(nodeId);
    }

    /**
     * Returns boolean result if node is present in nodes map or not
     * 
     * @param node
     * @return
     */
    public Boolean containsClientNode(final ClientNode node) {
        return nodes.containsKey(node.getNodeId());
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     * 
     * @param nodeId
     * @return
     */
    public ClientNode getClientNode(final Integer nodeId) {
        return nodes.get(nodeId);
    }

    /**
     * Returns total number of client nodes
     * 
     * @return integer value
     */
    public int size() {
        return nodes.size();
    }
}
