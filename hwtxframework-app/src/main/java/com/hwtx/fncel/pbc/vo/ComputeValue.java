package com.hwtx.fncel.pbc.vo;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * Created by panye on 2014/10/5.
 */

public class ComputeValue {
    @Getter
    private String id;
    @Getter
    private String name;
    @Getter
    private String pid;

    private float value;

    @Getter
    private boolean kpi;

    public ComputeValue(String id, String name, String pid, float value) {
        this(id, name, pid, value, false);
    }

    public ComputeValue(String id, String name, String pid, float value, boolean kpi) {
        this.id = id;
        this.name = name;
        this.pid = pid;
        this.value = value;
        this.kpi = kpi;
    }

    public float getValue() {
        return new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
    }
}
