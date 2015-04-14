package com.neu.mrlite.master;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import com.neu.mrlite.common.ClientNode;
import com.neu.mrlite.common.TaskConf;

public class JobServlet extends Thread {
    private static final String TERMINATE = "Task_Finished";
    private final Socket clientSocket;
    private final int clientNodeId;
    private final PrintWriter out;
    private final BufferedReader in;
    private final TaskConf taskConf;

    public JobServlet(ClientNode slaveNode, TaskConf taskConf)
            throws IOException {
        this.clientNodeId = slaveNode.getNodeId();
        this.clientSocket = slaveNode.getClientSocket();
        this.in = slaveNode.getInputFromClient();
        this.out = slaveNode.getOutputToClient();
        this.taskConf = taskConf;
        start();
    }

    @Override
    public void run() {
        try {
            // send the Task Configuration to client
            out.println(taskConf.serializeToJson());
            String inputLine;
            System.out.println("Waiting for client response...");
            while ((inputLine = in.readLine().trim()) != null) {
                System.out.println(inputLine);
                if (inputLine.equals(TERMINATE)) {
                    break;
                }
            }
            System.out.println(taskConf.isMapperTask() ? "Mapper " : "Reducer "
                    + "Task is complete");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public int getClientNodeId() {
        return clientNodeId;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public TaskConf getTaskConf() {
        return taskConf;
    }
}
