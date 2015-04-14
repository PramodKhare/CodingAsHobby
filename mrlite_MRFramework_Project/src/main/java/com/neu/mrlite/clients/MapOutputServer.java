package com.neu.mrlite.clients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import com.neu.mrlite.common.Constants;
import com.neu.mrlite.common.InMemStore;
import com.neu.mrlite.common.datastructures.Pair;

public class MapOutputServer extends Thread {
    private static MapOutputServer mapOutputServerSingleton;
    private ServerSocket socket;
    private boolean intr = false;

    private MapOutputServer(final int nodeId) throws IOException {
        // e.g. Every Nodes's MapOutputServer will start at port = 8000 + nodeId
        socket = new ServerSocket(Constants.BASE_MAP_SERVER_PORT + nodeId);
        start();
    }

    public static void startMapOutputServer(final int nodeId)
            throws IOException {
        if (mapOutputServerSingleton == null) {
            mapOutputServerSingleton = new MapOutputServer(nodeId);
        }
    }

    public boolean isInterrupted() {
        return (intr == true);
    }

    public void interrupt() {
        intr = false;
    }

    public static void stopMapOutputServer() throws IOException {
        if (mapOutputServerSingleton != null) {
            mapOutputServerSingleton.interrupt();
            if (mapOutputServerSingleton.socket != null
                    && !mapOutputServerSingleton.socket.isClosed()) {
                mapOutputServerSingleton.socket.close();
            }
            mapOutputServerSingleton = null;
        }
    }

    @Override
    public void run() {
        System.out.println("MapOutputServer listening on:"
                + socket.getInetAddress().getHostAddress() + ":"
                + socket.getLocalPort());
        try {
            while (!isInterrupted()) {
                try {
                    // Accept the Reducer Client Socket connection, for copying map-output partitions
                    Socket cilentSocket = socket.accept();
                    serveMapOutputPartition(cilentSocket);
                } catch (final IOException e) {
                    System.out
                            .println("Unable to serve map-output-partition request!");
                    e.printStackTrace();
                }
            }
        } finally {
            // Finally shutdown the Map Output Server
            System.out.println("Stopped MapOutputServer:"
                    + socket.getInetAddress() + ":" + socket.getLocalPort());
            try {
                socket.close();
            } catch (final IOException io) {
                io.printStackTrace();
            }
        }
    }

    /**
     * Communication Protocol - Reducer client will send the mapoutputkey e.g.
     * TaksId_<partition_number> for which it needs the shuffle data for. If no
     * such data exists in the In-Memory-Store of this client node then it will
     * send ERROR message and close the Socket Connection
     * 
     * @param cilentSocket
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public static void serveMapOutputPartition(final Socket cilentSocket)
            throws IOException {
        BufferedReader inputFromReduceNode = null;
        PrintWriter outputToReduceNode = null;
        try {
            inputFromReduceNode = new BufferedReader(new InputStreamReader(
                    cilentSocket.getInputStream()));
            outputToReduceNode = new PrintWriter(
                    cilentSocket.getOutputStream(), true);

            // One request will serve only one key - but all its list of pairs will be streamed
            String line = inputFromReduceNode.readLine().trim();
            // Get the key i.e. map output partition key
            if (line != null && !line.equals("")) {
                try {
                    // this is serialized JobConf object, deserialize it
                    List<Pair> mapOutputPartition = (List<Pair>) InMemStore
                            .getValueForKey(line);
                    if (mapOutputPartition == null
                            || mapOutputPartition.isEmpty()) {
                        outputToReduceNode
                                .println("NO RECORDS FOR PARTITION_KEY");
                    } else {
                        for (Pair p : mapOutputPartition) {
                            outputToReduceNode.println(p.toString());
                        }
                        outputToReduceNode.println("SHUFFLE_COMPLETE");
                        System.out
                                .println("Sent all the map output pairs for key "
                                        + line);
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                    outputToReduceNode
                            .println("ERROR: UNABLE_TO_SEND_MAP_PARTITION");
                }
            } else {
                outputToReduceNode.println("NO RECORDS FOR PARTITION_KEY");
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (outputToReduceNode != null) {
                    outputToReduceNode.close();
                }
                if (inputFromReduceNode != null) {
                    inputFromReduceNode.close();
                }
                if (cilentSocket != null) {
                    cilentSocket.close();
                }
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
