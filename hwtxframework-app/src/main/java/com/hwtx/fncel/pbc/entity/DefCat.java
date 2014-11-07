package com.hwtx.fncel.pbc.entity;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.thinkgem.jeesite.common.persistence.DataEntity;

@TableBind(tableName = "def_cat", pkName = "dcatId")
public class DefCat extends DataEntity<DefCat> {

    public static DefCat dao = new DefCat();

    public java.lang.String getDcatId() {
        return get("dcatId");
    }

    public void setDcatId(java.lang.String dcatId) {
        set("dcatId", dcatId);
    }

    public java.lang.String getDcatName() {
        return get("dcatName");
    }

    public void setDcatName(java.lang.String dcatName) {
        set("dcatName", dcatName);
    }

    public java.lang.Integer getDcatExpireMark() {
        return get("dcatExpireMark");
    }

    public void setDcatExpireMark(java.lang.Integer dcatExpireMark) {
        set("dcatExpireMark", dcatExpireMark);
    }

    public java.lang.Integer getDcatDelMark() {
        return get("dcatDelMark");
    }

    public void setDcatDelMark(java.lang.Integer dcatDelMark) {
        set("dcatDelMark", dcatDelMark);
    }

    public Double getDcatScore() {
        return get("dcatScore");
    }

    public void setDcatScore(Double dcatScore) {
        set("dcatScore", dcatScore);
    }

    public java.lang.String getDcatWeight() {
        return get("dcatWeight");
    }

    public void setDcatWeight(java.lang.String dcatWeight) {
        set("dcatWeight", dcatWeight);
    }

    public java.lang.String getDcatAuthRuleId() {
        return get("dcatAuthRuleId");
    }

    public void setDcatAuthRuleId(java.lang.String dcatAuthRuleId) {
        set("dcatAuthRuleId", dcatAuthRuleId);
    }

    public java.lang.Integer getDcatCalRuleId() {
        return get("dcatCalRuleId");
    }

    public void setDcatCalRuleId(java.lang.Integer dcatCalRuleId) {
        set("dcatCalRuleId", dcatCalRuleId);
    }

    public java.lang.String getDcatRemark() {
        return get("dcatRemark");
    }

    public void setDcatRemark(java.lang.String dcatRemark) {
        set("dcatRemark", dcatRemark);
    }

    public Integer getDcatSquence() {
        return get("dcatSquence");
    }

    public void setDcatSquence(Integer dcatSquence) {
        set("dcatSquence", dcatSquence);
    }

    public String actions = "actions";

    public String getActions() {
        return actions;
    }
}
