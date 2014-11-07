package com.hwtx.fncel.pbc.entity;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.Date;

@TableBind(tableName = "value_class", pkName = "vclassId")
public class ValueClass extends DataEntity<ValueClass> {

    public static ValueClass dao = new ValueClass();

    public java.lang.String getVclassId() {
        return get("vclassId");
    }

    public void setVclassId(java.lang.String vclassId) {
        set("vclassId", vclassId);
    }

    public java.lang.String getDclassId() {
        return get("dclassId");
    }

    public ValueClass setDclassId(java.lang.String dclassId) {
        set("dclassId", dclassId);
        return this;
    }

    public java.lang.String getVcatId() {
        return get("vcatId");
    }

    public void setVcatId(java.lang.String vcatId) {
        set("vcatId", vcatId);
    }

    public Date getVclassCalDate() {
        return get("vclassCalDate");
    }

    public ValueClass setVclassCalDate(Date vclassCalDate) {
        set("vclassCalDate", vclassCalDate);
        return this;
    }

    public java.lang.String getVclassCalRuleId() {
        return get("vclassCalRuleId");
    }

    public void setVclassCalRuleId(java.lang.String vclassCalRuleId) {
        set("vclassCalRuleId", vclassCalRuleId);
    }

    public java.lang.Integer getVclassRegion() {
        return get("vclassRegion");
    }

    public ValueClass setVclassRegion(java.lang.Integer vclassRegion) {
        set("vclassRegion", vclassRegion);
        return this;
    }

    public java.lang.String getVclassAuthRuleId() {
        return get("vclassAuthRuleId");
    }

    public void setVclassAuthRuleId(java.lang.String vclassAuthRuleId) {
        set("vclassAuthRuleId", vclassAuthRuleId);
    }

    public java.lang.Float getVclassValue() {
        return get("vclassValue");
    }

    public ValueClass setVclassValue(java.lang.Float vclassValue) {
        set("vclassValue", vclassValue);
        return this;
    }

    public Date getVclassDate() {
        return get("vclassDate");
    }

    public ValueClass setVclassDate(Date vclassDate) {
        set("vclassDate", vclassDate);
        return this;
    }

    public Integer getVclassStatus() {
        return get("vclassStatus");
    }

    public ValueClass setVclassStatus(Integer vclassStatus) {
        set("vclassStatus", vclassStatus);
        return this;
    }

    public String actions = "actions";

    public String getActions() {
        return actions;
    }
}
