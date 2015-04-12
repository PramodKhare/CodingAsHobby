package com.neu.mrlite.clients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import com.neu.mrlite.common.TaskConf;
import com.neu.mrlite.common.datastructures.Writable;

//TODO Make use of Logger instead of sysouts

public class JobClient extends Thread {
    static List<Writable> outVal = null;
    private PrintWriter out;
    private Socket socket;
    private BufferedReader in;
    private String masterIp;
    private int nodeId;

    public JobClient(String masterIp) {
        this.masterIp = masterIp;
        start();
    }

    @SuppressWarnings("resource")
    @Override
    public void run() {
        try {
            this.socket = new Socket(this.masterIp, 2120);
            this.out = new PrintWriter(this.socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(
                    this.socket.getInputStream()));
            receiveNodeIdAndExecuteTask();
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Shutting down the client");
            try {
                if (in != null) {
                    in.close();
                    in = null;
                }
                if (out != null) {
                    out.close();
                    out = null;
                }
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * First complete the NodeId communication and then execute the actual job
     * 
     * @throws IOException
     */
    private void receiveNodeIdAndExecuteTask() throws IOException {
        String line = this.in.readLine();
        // First receive node_id i.e. CLIENT_NUMBER
        line = line.trim();
        if (!line.startsWith("CLIENT_NUMBER")) {
            System.out
                    .println("Unable to establish CLIENT_NUMBER communication, closing socket connection");
        } else {
            try {
                this.nodeId = Integer.parseInt(line.split(" ")[1]);
                this.out.println("CLIENT_NUMBER_ACCEPTED");
                System.out
                        .println("Client Number communication sequence complete - "
                                + line);
                // Start the MapOutputServer which serves the mapper output from
                // In-Memory-Store
                MapOutputServer.startMapOutputServer(this.nodeId);
                // And then execute the actual job
                executeTask();
            } catch (final Exception e) {
                System.out
                        .println("Unable to establish CLIENT_NUMBER communication, closing socket connection");
                e.printStackTrace();
            }
        }
    }

    /**
     * Execute the given Map or Reduce Task
     * 
     * @throws IOException
     */
    private void executeTask() throws IOException {
        String line;
        MapperClientTask mapClientTask;
        ReducerClientTask reduceClientTask;

        while ((line = this.in.readLine()) != null) {
            try {
                // this is serialized TaskConf object, deserialize it
                TaskConf task = TaskConf.deserializeFromJson(line);

                // Decide if its a Mapper task or reducer task
                if (task.isMapperTask()) {
                    // Start Map Task thread
                    mapClientTask = new MapperClientTask(task, this.out);
                    mapClientTask.join();
                } else {
                    // Start Reduce Task Thread
                    reduceClientTask = new ReducerClientTask(task, this.out);
                    reduceClientTask.join();
                }
                System.out.println(task.isMapperTask() ? "JobClient: Mapper "
                        : "Reducer " + "Task is complete");
            } catch (final Exception e) {
                e.printStackTrace();
                this.out.println("Invalid Task configuration provided");
            }
        }
    }
}
