package com.hwtx.fncel.pbc.entity;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.Date;

@TableBind(tableName = "value_cat", pkName = "vcatId")
public class ValueCat extends DataEntity<ValueCat> {

    public static ValueCat dao = new ValueCat();


    public java.lang.String getVcatId() {
        return get("vcatId");
    }

    public void setVcatId(java.lang.String vcatId) {
        set("vcatId", vcatId);
    }

    public java.lang.Float getVcatValue() {
        return get("vcatValue");
    }

    public ValueCat setVcatValue(java.lang.Float vcatValue) {
        set("vcatValue", vcatValue);
        return this;
    }

    public java.lang.String getVcatCalRuleId() {
        return get("vcatCalRuleId");
    }

    public void setVcatCalRuleId(java.lang.String vcatCalRuleId) {
        set("vcatCalRuleId", vcatCalRuleId);
    }

    public java.lang.Integer getVcatRegion() {
        return get("vcatRegion");
    }

    public ValueCat setVcatRegion(java.lang.Integer vcatRegion) {
        set("vcatRegion", vcatRegion);
        return this;
    }

    public Date getVcatCalDate() {
        return get("vcatCalDate");
    }

    public ValueCat setVcatCalDate(Date vcatCalDate) {
        set("vcatCalDate", vcatCalDate);
        return this;
    }

    public java.lang.String getDcatId() {
        return get("dcatId");
    }

    public ValueCat setDcatId(java.lang.String dcatId) {
        set("dcatId", dcatId);
        return this;
    }

    public Date getVcatDate() {
        return get("vcatDate");
    }

    public ValueCat setVcatStatus(Integer vcatStatus) {
        set("vcatStatus", vcatStatus);
        return this;
    }

    public Integer getVcatStatus() {
        return get("vcatStatus");
    }

    public ValueCat setVcatDate(Date vcatDate) {
        set("vcatDate", vcatDate);
        return this;
    }

    public String actions = "actions";

    public String getActions() {
        return actions;
    }
}
