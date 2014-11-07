package com.hwtx.fncel.pbc.entity;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.Date;

@TableBind(tableName = "value_kpi", pkName = "vkpiId")
public class ValueKpi extends DataEntity<ValueKpi> {

    public static ValueKpi dao = new ValueKpi();

    public java.lang.String getVkpiId() {
        return get("vkpiId");
    }

    public void setVkpiId(java.lang.String vkpiId) {
        set("vkpiId", vkpiId);
    }

    public java.lang.String getDkpiId() {
        return get("dkpiId");
    }

    public ValueKpi setDkpiId(java.lang.String dkpiId) {
        set("dkpiId", dkpiId);
        return this;
    }

    public java.lang.String getVclassId() {
        return get("vclassId");
    }

    public void setVclassId(java.lang.String vclassId) {
        set("vclassId", vclassId);
    }

    public java.lang.String getVkpiCalRuleId() {
        return get("vkpiCalRuleId");
    }

    public void setVkpiCalRuleId(java.lang.String vkpiCalRuleId) {
        set("vkpiCalRuleId", vkpiCalRuleId);
    }

    public java.lang.String getVkpiAuthRuleId() {
        return get("vkpiAuthRuleId");
    }

    public void setVkpiAuthRuleId(java.lang.String vkpiAuthRuleId) {
        set("vkpiAuthRuleId", vkpiAuthRuleId);
    }

    public Date getVkpiCalDate() {
        return get("vkpiCalDate");
    }

    public void setVkpiCalDate(Date vkpiCalDate) {
        set("vkpiCalDate", vkpiCalDate);
    }

    public java.lang.Integer getVkpiRegion() {
        return get("vkpiRegion");
    }

    public ValueKpi setVkpiRegion(java.lang.Integer vkpiRegion) {
        set("vkpiRegion", vkpiRegion);
        return this;
    }

    public java.lang.Float getVkpiScore() {
        return get("vkpiScore");
    }

    public void setVkpiScore(java.lang.Float vkpiScore) {
        set("vkpiScore", vkpiScore);
    }

    public java.lang.Float getVkpiValue() {
        return get("vkpiValue");
    }

    public void setVkpiValue(java.lang.Float vkpiValue) {
        set("vkpiValue", vkpiValue);
    }

    public java.lang.Integer getVkpiCheckMark() {
        return get("vkpiCheckMark");
    }

    public void setVkpiCheckMark(java.lang.Integer vkpiCheckMark) {
        set("vkpiCheckMark", vkpiCheckMark);
    }

    public Date getVkpiDate() {
        return get("vkpiDate");
    }

    public ValueKpi setVkpiDate(Date vkpiDate) {
        set("vkpiDate", vkpiDate);
        return this;
    }

    public Integer getVkpiStatus() {
        return get("vkpiStatus");
    }

    public ValueKpi setVkpiStatus(Integer vkpiStatus) {
        set("vkpiStatus", vkpiStatus);
        return this;
    }

    public String actions = "actions";

    public String getActions() {
        return actions;
    }
}
