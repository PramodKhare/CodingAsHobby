package com.neu.mrlite.common.exception;

public class ShuffleFailureException extends Exception {
    private static final long serialVersionUID = -4365699184057385365L;

    public ShuffleFailureException() {
        this("Shuffle Phase failed");
    }

    public ShuffleFailureException(final String message) {
        super("Shuffle Phase failed: " + message);
    }
}
