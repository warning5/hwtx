package com.hwtx.fncel.pbc.web;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by panye on 14-11-4.
 */
public class StatisticResultContainer {

    @Getter
    private List<String> over = Lists.newArrayList();
    @Getter
    private List<String> error = Lists.newArrayList();
    @Getter
    @Setter
    private String running;
    private Integer status = 0;
    @Setter@Getter
    private String errorMessage = "";

    public void clear() {
        over.clear();
        running = null;
    }

    public void clearError() {
        error.clear();
    }

    public boolean isRunning() {
        return status == 1;
    }

    public boolean isOver() {
        return status == 0;
    }

    public void start() {
        status = 1;
    }

    public void over() {
        status = 0;
        clear();
    }
}
