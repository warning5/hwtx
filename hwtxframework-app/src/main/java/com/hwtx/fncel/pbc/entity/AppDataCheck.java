package com.hwtx.fncel.pbc.entity;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.Date;

@TableBind(tableName = "app_data_check", pkName = "checkId")
public class AppDataCheck extends DataEntity<AppDataCheck> {

    public static AppDataCheck dao = new AppDataCheck();

    public java.lang.String getCheckId() {
        return get("checkId");
    }

    public void setCheckId(java.lang.String checkId) {
        set("checkId", checkId);
    }

    public java.lang.String getCheckUserId() {
        return get("checkUserId");
    }

    public void setCheckUserId(java.lang.String checkUserId) {
        set("checkUserId", checkUserId);
    }

    public Integer getCheckdRegion() {
        return get("checkdRegion");
    }

    public void setCheckdRegion(Integer checkdRegion) {
        set("checkdRegion", checkdRegion);
    }

    public Date getCheckedDate() {
        return get("checkedDate");
    }

    public void setCheckedDate(Date checkedDate) {
        set("checkedDate", checkedDate);
    }

    public Date getCheckDate() {
        return get("checkDate");
    }

    public void setCheckDate(Date checkDate) {
        set("checkDate", checkDate);
    }

    public java.lang.String getCheckNotes() {
        return get("checkNotes");
    }

    public void setCheckNotes(java.lang.String checkNotes) {
        set("checkNotes", checkNotes);
    }

    public java.lang.String getCheckOrgId() {
        return get("checkOrgId");
    }

    public void setCheckOrgId(java.lang.String checkOrgId) {
        set("checkOrgId", checkOrgId);
    }

    public java.lang.String getCheckedOrgId() {
        return get("checkedOrgId");
    }

    public void setCheckedOrgId(java.lang.String checkedOrgId) {
        set("checkedOrgId", checkedOrgId);
    }

    public int getCheckType() {
        return get("checkType");
    }

    public void setCheckType(int checkType) {
        set("checkType", checkType);
    }

    public String actions = "actions";

    public String getActions() {
        return actions;
    }
}
