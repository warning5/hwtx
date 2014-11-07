package com.hwtx.fncel.pbc.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by panye on 2014/9/30.
 */
@AllArgsConstructor
@Getter
@Setter
public class ValueNotExistException extends Throwable {

    private String name;
    private String type;

}
