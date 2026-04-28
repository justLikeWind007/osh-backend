package com.backstage.system.exception;

public class UpLoadException extends RuntimeException {
    public UpLoadException(String message) {
        super(message);
    }

    public UpLoadException(String message, Throwable cause) {
        super(message, cause);
    }

}
