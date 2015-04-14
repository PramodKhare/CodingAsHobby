package com.neu.mrlite.common;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Singleton class - maintains the list of Jobs to be waiting for execution on
 * MR framework
 * 
 * @Bill_Pugh_Singleton_Implementation
 * @author Pramod Khare
 *
 */
public class JobQueue {
    public final BlockingQueue<JobConf> jobQueue;

    private JobQueue() {
        jobQueue = new ArrayBlockingQueue<JobConf>(Constants.MAX_RUNNING_JOBS);
    }

    private JobQueue(final Integer queueSize) {
        jobQueue = new ArrayBlockingQueue<JobConf>(
                queueSize == null ? Constants.MAX_RUNNING_JOBS : queueSize);
    }

    private static class SingletonHelper {
        private static final JobQueue INSTANCE = new JobQueue();
    }

    public static JobQueue get() {
        return SingletonHelper.INSTANCE;
    }

    /**
     * Non-blocking method to add the new JobConf object into jobqueue
     * 
     * @param conf
     * @return
     * @throws IllegalStateException
     */
    public boolean queueJob(final JobConf conf) throws IllegalStateException {
        return jobQueue.add(conf);
    }

    /**
     * Removes the new job to be executed from the jobqueue - blocks if queue is
     * empty until any new JobConf object is available
     * 
     * @return
     * @throws InterruptedException
     */
    public JobConf dequeueJob() throws InterruptedException {
        return jobQueue.take();
    }

    /**
     * Puts or queues the given JobConf into the jobqueue - but blocks if queue
     * is already full until there is any room for new element
     * 
     * @param conf
     * @return
     * @throws InterruptedException
     */
    public void putJob(final JobConf conf) throws InterruptedException {
        jobQueue.put(conf);
    }
}
