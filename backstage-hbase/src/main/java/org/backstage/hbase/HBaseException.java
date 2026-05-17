package org.backstage.hbase;

public class HBaseException extends RuntimeException {

    public HBaseException(String message) {
        super(message);
    }

    public HBaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
