package com.neu.mrlite.common.exception;

public class JobQueueFullException extends Exception {
    private static final long serialVersionUID = -8175248471046495309L;

    public JobQueueFullException() {
        this("MR Job Queue full, cannot run the job at this time!");
    }

    public JobQueueFullException(final String message) {
        super(message);
    }
}
