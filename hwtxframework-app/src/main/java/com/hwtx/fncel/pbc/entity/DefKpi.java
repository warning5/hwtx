package com.hwtx.fncel.pbc.entity;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.thinkgem.jeesite.common.persistence.DataEntity;
import lombok.Getter;
import lombok.Setter;

@TableBind(tableName = "def_kpi", pkName = "dkpiId")
public class DefKpi extends DataEntity<DefKpi> {

    public static DefKpi dao = new DefKpi();


    public java.lang.String getDkpiId() {
        return get("dkpiId");
    }

    public DefKpi setDkpiId(java.lang.String dkpiId) {
        set("dkpiId", dkpiId);
        return this;
    }

    public java.lang.String getDkpiName() {
        return get("dkpiName");
    }

    public void setDkpiName(java.lang.String dkpiName) {
        set("dkpiName", dkpiName);
    }

    public java.lang.String getDclassId() {
        return get("dclassId");
    }

    public void setDclassId(java.lang.String dclassId) {
        set("dclassId", dclassId);
    }

    public java.lang.String getDkpiCalRuleId() {
        return get("dkpiCalRuleId");
    }

    public DefKpi setDkpiCalRuleId(java.lang.String dkpiCalRuleId) {
        set("dkpiCalRuleId", dkpiCalRuleId);
        return this;
    }

    public java.lang.String getDkpiAuthRuleId() {
        return get("dkpiAuthRuleId");
    }

    public void setDkpiAuthRuleId(java.lang.String dkpiAuthRuleId) {
        set("dkpiAuthRuleId", dkpiAuthRuleId);
    }

    public java.lang.String geDkpiExtendExp() {
        return get("dkpiExtendExp");
    }

    public void setDkpiExtendExp(java.lang.String dkpiExtendExp) {
        set("dkpiExtendExp", dkpiExtendExp);
    }

    public java.sql.Timestamp getDkpiDefDate() {
        return get("dkpiDefDate");
    }

    public void setDkpiDefDate(java.sql.Timestamp dkpiDefDate) {
        set("dkpiDefDate", dkpiDefDate);
    }

    public java.sql.Timestamp getDkpiModDate() {
        return get("dkpiModDate");
    }

    public void setDkpiModDate(java.sql.Timestamp dkpiModDate) {
        set("dkpiModDate", dkpiModDate);
    }

    public java.lang.Integer getDkpiExpireMark() {
        return get("dkpiExpireMark");
    }

    public void setDkpiExpireMark(java.lang.Integer dkpiExpireMark) {
        set("dkpiExpireMark", dkpiExpireMark);
    }

    public java.lang.String getDkpiWeight() {
        return get("dkpiWeight");
    }

    public void setDkpiWeight(java.lang.String dkpiWeight) {
        set("dkpiWeight", dkpiWeight);
    }

    public Float getDkpiscore() {
        return get("dkpiscore");
    }

    public void setDkpiscore(Float dkpiscore) {
        set("dkpiscore", dkpiscore);
    }

    public java.lang.Integer getDkpiDelMark() {
        return get("dkpiDelMark");
    }

    public void setDkpiDelMark(java.lang.Integer dkpiDelMark) {
        set("dkpiDelMark", dkpiDelMark);
    }

    public java.lang.String getDkpiRemark() {
        return get("dkpiRemark");
    }

    public void setDkpiRemark(java.lang.String dkpiRemark) {
        set("dkpiRemark", dkpiRemark);
    }

    public Integer getDkpiSquence() {
        return get("dkpiSquence");
    }

    public void setDkpiSquence(Integer dkpiSquence) {
        set("dkpiSquence", dkpiSquence);
    }

    public java.lang.Integer getComplex() {
        return get("complex");
    }

    public void setComplex(java.lang.Integer complex) {
        set("complex", complex);
    }

    public Integer getDkpiStandardizeType() {
        return get("dkpiStandardizeType");
    }

    public void setDkpiStandardizeType(Integer dkpiStandardizeType) {
        set("dkpiStandardizeType", dkpiStandardizeType);
    }

    public String getPid() {
        return get("pid");
    }

    public void setPid(String pid) {
        set("pid", pid);
    }

    @Getter
    @Setter
    private String comparisonOp;
    @Getter
    @Setter
    private String comparisonText;

    public String actions = "actions";

    public String getActions() {
        return actions;
    }
}
