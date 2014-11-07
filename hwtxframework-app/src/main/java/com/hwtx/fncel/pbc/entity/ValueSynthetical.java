package com.hwtx.fncel.pbc.entity;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.Date;

@TableBind(tableName = "value_synthetical", pkName = "id")
public class ValueSynthetical extends DataEntity<ValueSynthetical> {

    public static ValueSynthetical dao = new ValueSynthetical();


    public java.lang.String getId() {
        return get("id");
    }

    public void setId(java.lang.String id) {
        set("id", id);
    }

    public java.lang.Integer getScore() {
        return get("score");
    }

    public void setScore(java.lang.Integer score) {
        set("score", score);
    }

    public java.lang.Float getValue() {
        return get("value");
    }

    public void setValue(java.lang.Float value) {
        set("value", value);
    }

    public Date getDate() {
        return get("date");
    }

    public void setDate(Date date) {
        set("date", date);
    }

    public java.lang.Integer getRegion() {
        return get("region");
    }

    public void setRegion(java.lang.Integer region) {
        set("region", region);
    }

    public String actions = "actions";

    public String getActions() {
        return actions;
    }
}
