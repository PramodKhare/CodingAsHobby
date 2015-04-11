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
    private int nodeId;
    private InetAddress clientIp;
    private PrintWriter outputToClient;
    private BufferedReader inputFromClient;

    public ClientNode(final Socket clientSocket, final int nodeId)
            throws IOException {
        this.clientSocket = clientSocket;
        this.nodeId = nodeId;
        this.clientIp = clientSocket.getInetAddress();
        outputToClient = new PrintWriter(clientSocket.getOutputStream(), true);
        inputFromClient = new BufferedReader(new InputStreamReader(
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
