package com.hwtx.fncel.pbc.entity;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.thinkgem.jeesite.common.persistence.DataEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@TableBind(tableName = "value_inidata", pkName = "viniDataId")
public class ValueInidata extends DataEntity<ValueInidata> {

    public static ValueInidata dao = new ValueInidata();

    @Getter
    @Setter
    private ValueInidatastatics valueInidatastatistic;

    @Setter
    @Getter
    private boolean insert = true;

    public java.lang.String getViniDataId() {
        return get("viniDataId");
    }

    public void setViniDataId(java.lang.String viniDataId) {
        set("viniDataId", viniDataId);
    }

    public java.lang.String getDiniDataId() {
        return get("diniDataId");
    }

    public void setDiniDataId(java.lang.String diniDataId) {
        set("diniDataId", diniDataId);
    }

    public java.lang.String getVkpiId() {
        return get("vkpiId");
    }

    public void setVkpiId(java.lang.String vkpiId) {
        set("vkpiId", vkpiId);
    }

    public java.lang.Float getViniDataValue() {
        return get("viniDataValue");
    }

    public void setViniDataValue(java.lang.Float viniDataValue) {
        set("viniDataValue", viniDataValue);
    }

    public java.lang.String getViniDataCalRuleId() {
        return get("viniDataCalRuleId");
    }

    public void setViniDataCalRuleId(java.lang.String viniDataCalRuleId) {
        set("viniDataCalRuleId", viniDataCalRuleId);
    }

    public java.sql.Timestamp getViniDataSubDate() {
        return get("viniDataSubDate");
    }

    public void setViniDataSubDate(java.sql.Timestamp viniDataSubDate) {
        set("viniDataSubDate", viniDataSubDate);
    }

    public java.lang.String getViniDataAuthRuleId() {
        return get("viniDataAuthRuleId");
    }

    public void setViniDataAuthRuleId(java.lang.String viniDataAuthRuleId) {
        set("viniDataAuthRuleId", viniDataAuthRuleId);
    }

    public java.lang.Integer getViniDataRegion() {
        return get("viniDataRegion");
    }

    public void setViniDataRegion(java.lang.Integer viniDataRegion) {
        set("viniDataRegion", viniDataRegion);
    }

    public java.lang.String getViniDataSubmitOrgRole() {
        return get("viniDataSubmitOrgRole");
    }

    public void setViniDataSubmitOrgRole(java.lang.String viniDataSubmitOrgRole) {
        set("viniDataSubmitOrgRole", viniDataSubmitOrgRole);
    }

    public java.lang.Integer getIsStastic() {
        return get("isStastic");
    }

    public void setIsStastic(java.lang.Integer isStastic) {
        set("isStastic", isStastic);
    }

    public java.lang.Integer getViniDataCheckMark() {
        return get("viniDataCheckMark");
    }

    public void setViniDataCheckMark(java.lang.Integer viniDataCheckMark) {
        set("viniDataCheckMark", viniDataCheckMark);
    }

    public Date getViniDate() {
        return get("viniDate");
    }

    public void setViniDate(Date viniDate) {
        set("viniDate", viniDate);
    }

    public java.lang.Integer getViniStatus() {
        return get("viniStatus");
    }

    public void setViniStatus(java.lang.Integer viniStatus) {
        set("viniStatus", viniStatus);
    }

    public Date getViniDateFillDate() {
        return get("viniDateFillDate");
    }

    public void setViniDateFillDate(Date viniDateFillDate) {
        set("viniDateFillDate", viniDateFillDate);
    }

    public String actions = "actions";

    public String getActions() {
        return actions;
    }
}
