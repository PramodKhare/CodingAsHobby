package com.neu.mrlite.clients;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import com.neu.mrlite.common.TaskConf;
import com.neu.mrlite.common.datastructures.Writable;

public class JobClient extends Thread {
    static List<Writable> outVal = null;
    private PrintWriter out;
    private BufferedReader in;
    private String masterIp;

    public JobClient(String masterIp) {
        this.masterIp = masterIp;
        start();
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(masterIp, 2120);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                try {
                    // this is serialized TaskConf object, deserialize it
                    TaskConf task = TaskConf.deserializeFromJson(line);

                    // Decide if its a Mapper task or reducer task
                    if (task.isMapperTask()) {
                        // Start Map Task thread
                        new MapperClientTask(task, out).join();
                    } else {
                        // Start Reduce Task Thread
                        new ReducerClientTask(task, out).join();
                    }

                } catch (final Exception e) {
                    e.printStackTrace();
                    out.println("Invalid Task configuration provided");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
