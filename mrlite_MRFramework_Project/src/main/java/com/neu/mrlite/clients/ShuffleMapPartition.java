package com.neu.mrlite.clients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.neu.mrlite.common.MapperNodeInfo;
import com.neu.mrlite.common.datastructures.Pair;

public class ShuffleMapPartition extends Thread {
    private Socket mapSocket;
    private final int partitionNumber;
    private final MapperNodeInfo mapNode;
    private List<Pair> partition;
    private PrintWriter outputToServer;
    private BufferedReader inputFromServer;

    public ShuffleMapPartition(final MapperNodeInfo mapNode,
            final int partitionNumber) {
        this.mapNode = mapNode;
        this.partitionNumber = partitionNumber;
        this.partition = new ArrayList<Pair>();
        start();
    }

    @Override
    public void run() {
        try {
            // Connect to Map partition output server socket port of given map-node
            this.mapSocket = new Socket(this.mapNode.getShuffleSocketIp(),
                    this.mapNode.getShuffleSocketPort());
            this.outputToServer = new PrintWriter(
                    this.mapSocket.getOutputStream(), true);
            this.inputFromServer = new BufferedReader(new InputStreamReader(
                    this.mapSocket.getInputStream()));
            receiveMapPartition();
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Shutting down map-shuffle socket: "
                    + this.mapNode.getMapTaskId());
            try {
                if (inputFromServer != null) {
                    inputFromServer.close();
                    inputFromServer = null;
                }
                if (outputToServer != null) {
                    outputToServer.close();
                    outputToServer = null;
                }
                this.mapSocket.close();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void receiveMapPartition() throws IOException {
        // First Send the partition key
        this.outputToServer.println(this.mapNode.getMapTaskId() + "_"
                + this.partitionNumber);

        // Now receive the List of pairs from that partition
        String line;
        while ((line = this.inputFromServer.readLine().trim()) != null) {
            System.out.println(line);
            // Meaning we have received all the pairs from the Map-Node
            if (line.equals("SHUFFLE_COMPLETE")) {
                break;
            }

            // Meaning unable to start the Shuffle 
            if (line.equals("NO RECORDS FOR PARTITION_KEY")
                    || line.equals("ERROR: UNABLE_TO_SEND_MAP_PARTITION")) {
                throw new IOException(line);
            }

            // this is serialized Pair object, deserialize it, and add it into List
            Pair pair = Pair.deserializeFromJson(line);
            if (pair != null) {
                partition.add(pair);
            } else {
                System.out.println("Unable to deserialize Pair Object");
            }
        }

        System.out.println("Shuffle from mapNode "
                + mapNode.getShuffleSocketIp() + ":"
                + mapNode.getShuffleSocketPort() + " is complete");
    }

    public Socket getClientSocket() {
        return mapSocket;
    }

    public MapperNodeInfo getMapNode() {
        return mapNode;
    }

    public List<Pair> getPartition() {
        return partition;
    }
}
