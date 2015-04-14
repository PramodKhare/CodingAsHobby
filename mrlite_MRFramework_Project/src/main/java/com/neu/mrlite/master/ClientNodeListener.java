package com.neu.mrlite.master;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.neu.mrlite.common.ClientNode;
import com.neu.mrlite.common.ClientNodesMap;
import com.neu.mrlite.common.NodeIdGenerator;

public class ClientNodeListener extends Thread {
    private static ClientNodeListener clientNodeListenerSingleton;
    private ServerSocket socket;
    private boolean intr = false;

    private ClientNodeListener() throws IOException {
        socket = new ServerSocket(2120);
        start();
    }

    public static void startClientNodeListener() throws IOException {
        if (clientNodeListenerSingleton == null) {
            clientNodeListenerSingleton = new ClientNodeListener();
        }
    }

    public boolean isInterrupted() {
        return (intr == true);
    }

    public void interrupt() {
        intr = false;
    }

    public static void stopClientNodeListener() throws IOException {
        if (clientNodeListenerSingleton != null) {
            clientNodeListenerSingleton.interrupt();
            if (clientNodeListenerSingleton.socket != null
                    && !clientNodeListenerSingleton.socket.isClosed()) {
                clientNodeListenerSingleton.socket.close();
            }
            clientNodeListenerSingleton = null;
        }
    }

    @Override
    public void run() {
        try {
            // Started Listening for Clients on port : 2120
            System.out.println("Started Listening for Clients on port: "
                    + socket.getLocalPort());
            // Listen continuously until interrupted
            while (!isInterrupted()) {

                try {
                    // Accepted the incoming client socket connections
                    Socket cilentSocket = socket.accept();
                    saveClientNodeDetails(cilentSocket);
                } catch (final Exception e) {

                }
            }
            socket.close();
            System.out.println("ClientNodeListener Stopped.");
        } catch (IOException e) {
            System.out
                    .println("Unable to close ClientNodeListener server socket.");
            e.printStackTrace();
        }
    }

    /**
     * During first communicate nodeId with client, then save it into client
     * nodes list
     * 
     * @param cilentSocket
     * @throws IOException
     */
    public static void saveClientNodeDetails(final Socket cilentSocket)
            throws IOException {
        ClientNode clientNode = new ClientNode(cilentSocket,
                NodeIdGenerator.generateNodeId());

        // First send the Client node its assigned NodeId number
        clientNode.getOutputToClient().println(
                "CLIENT_NUMBER " + clientNode.getNodeId());

        // Confirm it received and accepted it, then only add it into the nodes list
        String accepted = clientNode.getInputFromClient().readLine().trim();
        if (accepted.equals("CLIENT_NUMBER_ACCEPTED")) {
            // Then append it into client nodes list
            ClientNodesMap.get().addNewClientNode(clientNode);
        } else {
            System.out
                    .println("Unable to establish CLIENT_NUMBER communication, closing socket connection");
            clientNode.terminateClientConnection();
        }
    }
}
