package com.hwtx.fncel.pbc.entity;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.thinkgem.jeesite.common.persistence.DataEntity;

@TableBind(tableName = "def_class", pkName = "dclassId")
public class DefClass extends DataEntity<DefClass> {

    public static DefClass dao = new DefClass();

    public java.lang.String getDclassId() {
        return get("dclassId");
    }

    public void setDclassId(java.lang.String dclassId) {
        set("dclassId", dclassId);
    }

    public java.lang.String getDclassName() {
        return get("dclassName");
    }

    public void setDclassName(java.lang.String dclassName) {
        set("dclassName", dclassName);
    }

    public java.lang.Integer getDclassAuthRuleId() {
        return get("dclassAuthRuleId");
    }

    public void setDclassAuthRuleId(java.lang.Integer dclassAuthRuleId) {
        set("dclassAuthRuleId", dclassAuthRuleId);
    }

    public java.lang.String getDcatId() {
        return get("dcatId");
    }

    public void setDcatId(java.lang.String dcatId) {
        set("dcatId", dcatId);
    }

    public java.lang.Integer getDclassExpireMark() {
        return get("dclassExpireMark");
    }

    public void setDclassExpireMark(java.lang.Integer dclassExpireMark) {
        set("dclassExpireMark", dclassExpireMark);
    }

    public java.lang.String getDclassWeight() {
        return get("dclassWeight");
    }

    public void setDclassWeight(java.lang.String dclassWeight) {
        set("dclassWeight", dclassWeight);
    }

    public Double getDclassScore() {
        return get("dclassScore");
    }

    public void setDclassScore(Double dclassScore) {
        set("dclassScore", dclassScore);
    }

    public java.lang.Integer getDclassDelMark() {
        return get("dclassDelMark");
    }

    public void setDclassDelMark(java.lang.Integer dclassDelMark) {
        set("dclassDelMark", dclassDelMark);
    }

    public java.lang.String getDclassCalRuleId() {
        return get("dclassCalRuleId");
    }

    public void setDclassCalRuleId(java.lang.String dclassCalRuleId) {
        set("dclassCalRuleId", dclassCalRuleId);
    }

    public java.lang.String getDclassRemark() {
        return get("dclassRemark");
    }

    public void setDclassRemark(java.lang.String dclassRemark) {
        set("dclassRemark", dclassRemark);
    }

    public Integer getDclassSquence() {
        return get("dclassSquence");
    }

    public void setDclassSquence(Integer dclassSquence) {
        set("dclassSquence", dclassSquence);
    }

    public String actions = "actions";

    public String getActions() {
        return actions;
    }
}
