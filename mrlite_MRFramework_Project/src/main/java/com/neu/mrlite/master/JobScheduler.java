package com.neu.mrlite.master;

import com.neu.mrlite.common.JobConf;

public class JobScheduler implements Runnable {
    private boolean interrupt = false;
    private static JobScheduler jobSchedulerSingleton;
    private JobConf runningJob;

    private JobScheduler() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                runningJob = JobQueue.get().dequeueJob();
                
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void interrupt() {
        interrupt = true;
    }

    public boolean isInterrupted() {
        return interrupt == true;
    }

    public static synchronized void startJobScheduler() throws Exception {
        if (jobSchedulerSingleton == null) {
            jobSchedulerSingleton = new JobScheduler();
        }
    }

    public static synchronized void stopJobScheduler() throws Exception {

    }
}
