package com.neu.mrlite.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Keeps all the details of Slave Nodes
 * 
 * @author Pramod Khare
 */
public class ClientNode {
    private final Socket clientSocket;

    /* Node id for this Client */
    private int nodeId;

    /* Remote Clients IP Address */
    private InetAddress clientIp;

    /* Output and Input Streams to this remote client */
    private PrintWriter outputToClient;
    private BufferedReader inputFromClient;

    public ClientNode(final Socket clientSocket, final int nodeId)
            throws IOException {
        this.clientSocket = clientSocket;
        this.nodeId = nodeId;
        this.clientIp = clientSocket.getInetAddress();
        this.outputToClient = new PrintWriter(clientSocket.getOutputStream(),
                true);
        this.inputFromClient = new BufferedReader(new InputStreamReader(
                clientSocket.getInputStream()));
    }

    public int getNodeId() {
        return nodeId;
    }

    public InetAddress getClientIp() {
        return clientIp;
    }

    public PrintWriter getOutputToClient() {
        return outputToClient;
    }

    public BufferedReader getInputFromClient() {
        return inputFromClient;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public boolean terminateClientConnection() throws IOException {
        if (clientSocket != null && !clientSocket.isClosed()) {
            clientSocket.close();
        }
        return true;
    }
}
