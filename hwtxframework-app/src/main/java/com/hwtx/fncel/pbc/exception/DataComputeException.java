package com.hwtx.fncel.pbc.exception;

/**
 * Created by panye on 2014/10/6.
 */
public class DataComputeException extends Throwable {
    public DataComputeException() {
    }

    public DataComputeException(String message) {
        super(message);
    }

    public DataComputeException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataComputeException(Throwable cause) {
        super(cause);
    }

    public DataComputeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
