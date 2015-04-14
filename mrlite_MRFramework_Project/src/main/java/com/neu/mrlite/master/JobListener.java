package com.neu.mrlite.master;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.neu.mrlite.common.JobConf;
import com.neu.mrlite.common.JobQueue;

public class JobListener implements Runnable {
    private boolean intr = false;
    private static JobListener jobListenerSingleton;
    private ServerSocket listenerSocket;

    private JobListener() throws IOException {
        intr = false;
        listenerSocket = new ServerSocket(2121);
    }

    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;
        Socket client = null;
        try {
            System.out.println("Listening for jobs on port :"
                    + listenerSocket.getLocalPort());
            while (!isInterrupted()) {
                client = listenerSocket.accept();
                System.out.println("request connected:" + client.getPort());
                in = new BufferedReader(new InputStreamReader(
                        client.getInputStream()));
                out = new PrintWriter(client.getOutputStream(), true);
                String line;
                while ((line = in.readLine()) != null) {
                    try {
                        // this is serialized JobConf object, deserialize it
                        JobConf job = JobConf.deserializeFromJson(line);
                        if (job.isValidJobConfiguration()) {
                            JobQueue.get().queueJob(job);
                            out.println("job is put onto Job Queue : "
                                    + job.getExecutableJar());
                        } else {
                            out.println("Invalid Job configuration");
                        }
                    } catch (final IllegalStateException e) {
                        e.printStackTrace();
                        out.println("JobQueue is full, can't schedule the job now, try again");
                    } catch (final Exception e) {
                        e.printStackTrace();
                        out.println(e.getMessage());
                        System.out
                                .println("Invalid job configuration provided, please provide valid JobConf object");
                    }
                }
                out.close();
                in.close();
                client.close();
            }
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                if (client != null) {
                    client.close();
                }
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void interrupt() {
        intr = true;
    }

    public boolean isInterrupted() {
        return intr == true;
    }

    public static void startJobListener() throws IOException {
        if (jobListenerSingleton == null) {
            jobListenerSingleton = new JobListener();
            new Thread(jobListenerSingleton).start();
        }
    }

    public static void stopJobListener() throws IOException {
        if (jobListenerSingleton != null) {
            jobListenerSingleton.interrupt();
            if (jobListenerSingleton.listenerSocket != null
                    && !jobListenerSingleton.listenerSocket.isClosed()) {
                jobListenerSingleton.listenerSocket.close();
            }
            jobListenerSingleton = null;
        }
    }
}
