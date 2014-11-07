package com.hwtx.fncel.pbc.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hwtx.fncel.pbc.util.AppConstants;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by panye on 2014/9/30.
 */
public class InputDataVo implements Serializable {

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    private int num;
    private Date year;
    private int status;
    private String actions;

    public String getStatusShow() {
        return AppConstants.getAppStatusShow(getStatus());
    }

    @JSONField(format = "yyyy", serialzeFeatures = {SerializerFeature.WriteMapNullValue})
    public Date getYear() {
        return year;
    }

    public void setYear(Date year) {
        this.year = year;
    }

    public String getActions() {
        return actions;
    }

    public void setActions(String actions) {
        this.actions = actions;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
