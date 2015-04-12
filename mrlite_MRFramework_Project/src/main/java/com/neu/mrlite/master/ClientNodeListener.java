package com.neu.mrlite.master;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.neu.mrlite.common.ClientNode;
import com.neu.mrlite.common.NodeIdGenerator;

public class ClientNodeListener implements Runnable {
    private static ClientNodeListener clientNodeListenerSingleton;
    private ServerSocket socket;
    private boolean intr = false;

    private ClientNodeListener() throws IOException {
        socket = new ServerSocket(2120);
        new Thread(clientNodeListenerSingleton).start();
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
            System.out.println("Opened server connection:"
                    + socket.getLocalPort());
            while (!isInterrupted()) {
                // Accepted the Client Socket
                Socket cilentSocket = socket.accept();
                saveClientNodeDetails(cilentSocket);
            }
            System.out.println("Closed ClientNodeListener:"
                    + socket.getLocalPort());
            socket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
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
        // First send the node its NodeId Number
        clientNode.getOutputToClient().println(
                "CLIENT_NUMBER " + clientNode.getNodeId());
        // Confirm it received and accepted it, then only add it
        // into the nodes list
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
