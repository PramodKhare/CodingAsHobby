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
        try {
            System.out.println("MapOutputServer listening on:"
                    + socket.getInetAddress() + ":" + socket.getLocalPort());
            while (!isInterrupted()) {
                // Accepted the Reducer Client Socket connection
                Socket cilentSocket = socket.accept();
                serveMapOutput(cilentSocket);
            }
            System.out.println("Stopped MapOutputServer:"
                    + socket.getInetAddress() + ":" + socket.getLocalPort());
            socket.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Communication protocol - Reducer client will send the mapoutkey for which
     * it needs the shuffle data for. If no such data exists in the
     * In-Memory-Store of Client then it will send ERROR message and close the
     * Socket Connection
     * 
     * @param cilentSocket
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public static void serveMapOutput(final Socket cilentSocket)
            throws IOException {
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            in = new BufferedReader(new InputStreamReader(
                    cilentSocket.getInputStream()));
            out = new PrintWriter(cilentSocket.getOutputStream(), true);
            // One request will serve only one key - but all its list of pairs will be streamed
            String line = in.readLine().trim();
            // Get the key i.e. map output partition key
            if (line != null && !line.equals("")) {
                try {
                    // this is serialized JobConf object, deserialize it
                    List<Pair> mapOutputPartition = (List<Pair>) InMemStore
                            .getValueForKey(line);
                    if (mapOutputPartition == null
                            || mapOutputPartition.isEmpty()) {
                        out.println("NO RECORDS FOR PARTITION_KEY");
                    } else {
                        for (Pair p : mapOutputPartition) {
                            out.println(p.toString());
                        }
                        out.println("SHUFFLE_COMPLETE");
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                    out.println("ERROR: UNABLE_TO_SEND_MAP_PARTITION");
                }
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
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
