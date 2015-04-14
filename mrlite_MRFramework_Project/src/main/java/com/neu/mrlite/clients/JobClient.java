package com.neu.mrlite.clients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import com.neu.mrlite.common.TaskConf;
import com.neu.mrlite.common.datastructures.Pair;

//TODO Make use of java.util.Logger instead of sysouts

public class JobClient extends Thread {
    static List<Pair> outVal = null;
    private Socket socket;
    private BufferedReader inputFromMaster;
    private PrintWriter outputToMaster;
    private int nodeId;
    private String masterIp;

    public JobClient(final String masterIp) {
        this.masterIp = masterIp;
        start();
    }

    @SuppressWarnings("resource")
    @Override
    public void run() {
        try {
            this.socket = new Socket(this.masterIp, 2120);
            this.outputToMaster = new PrintWriter(
                    this.socket.getOutputStream(), true);
            this.inputFromMaster = new BufferedReader(new InputStreamReader(
                    this.socket.getInputStream()));
            receiveNodeIdAndExecuteTask();
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Shutting down the client");
            try {
                if (inputFromMaster != null) {
                    inputFromMaster.close();
                    inputFromMaster = null;
                }
                if (outputToMaster != null) {
                    outputToMaster.close();
                    outputToMaster = null;
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
        String line = this.inputFromMaster.readLine().trim();
        // First receive node_id i.e. "CLIENT_NUMBER X"

        if (!line.startsWith("CLIENT_NUMBER")) {
            System.out
                    .println("Unable to establish CLIENT_NUMBER communication, closing socket connection");
        } else {
            try {
                this.nodeId = Integer.parseInt(line.split(" ")[1]);
                this.outputToMaster.println("CLIENT_NUMBER_ACCEPTED");
                System.out
                        .println("Client Number communication sequence complete: "
                                + line);
                // Start the MapOutputServer which serves the mapper output from In-Memory-Store
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

        while ((line = this.inputFromMaster.readLine().trim()) != null) {
            try {
                // this is serialized TaskConf object, deserialize it
                TaskConf task = TaskConf.deserializeFromJson(line);

                // Decide if its a Mapper task or reducer task
                if (task.isMapperTask()) {
                    // Start Map Task thread
                    mapClientTask = new MapperClientTask(task,
                            this.outputToMaster);
                    mapClientTask.join();
                } else {
                    // Start Reduce Task Thread
                    reduceClientTask = new ReducerClientTask(task,
                            this.outputToMaster);
                    reduceClientTask.join();
                }
                System.out.println(task.isMapperTask() ? "JobClient: Mapper "
                        : "Reducer " + "Task is complete");
            } catch (final Exception e) {
                e.printStackTrace();
                this.outputToMaster
                        .println("Invalid Task configuration provided");
            }
        }
    }
}
