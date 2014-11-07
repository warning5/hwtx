package com.hwtx.fncel.pbc.exception;

import lombok.Getter;

/**
 * Created by panye on 2014/9/30.
 */
public class FieldNullException extends Throwable {

    @Getter
    private String field;

    public FieldNullException(String field) {
        this.field = field;
    }
}
