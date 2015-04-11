package com.neu.mrlite.master;

public class JobServer {
    private static boolean isStarted = false;

    private JobServer() {
    }

    public static synchronized void stopJobServer() throws Exception {
        // First Stop the JobListener
        JobListener.stopJobListener();
        // Stop the ClientNodeListener
        ClientNodeListener.stopClientNodeListener();
        // Stop the JobScheduler
        JobScheduler.stopJobScheduler();
        isStarted = false;
    }

    public static synchronized void restartJobServer() throws Exception {
        System.out.println("Restarting the job Server...");
        stopJobServer();
        startJobServer();
    }

    public static synchronized void startJobServer() throws Exception {
        if (isStarted) {
            System.out
                    .println("Job Server already running, please restart the job server if you need");
            return;
        }
        isStarted = true;
        // Start the ClientNodeListener
        ClientNodeListener.startClientNodeListener();
        // Start the JobScheduler
        JobScheduler.startJobScheduler();
        // First Start the JobListener
        JobListener.startJobListener();
    }
}
