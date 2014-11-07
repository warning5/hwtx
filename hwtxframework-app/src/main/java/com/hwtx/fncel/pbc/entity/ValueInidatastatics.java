package com.hwtx.fncel.pbc.entity;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.Date;

@TableBind(tableName = "value_inidatastatics", pkName = "siniDataId")
public class ValueInidatastatics extends DataEntity<ValueInidatastatics> {

    public static ValueInidatastatics dao = new ValueInidatastatics();

    public java.lang.String getViniDataId() {
        return get("viniDataId");
    }

    public void setViniDataId(java.lang.String viniDataId) {
        set("viniDataId", viniDataId);
    }

    public java.lang.String getSiniDataId() {
        return get("siniDataId");
    }

    public void setSiniDataId(java.lang.String siniDataId) {
        set("siniDataId", siniDataId);
    }

    public java.lang.String getSubmitOrg() {
        return get("submitOrg");
    }

    public void setSubmitOrg(java.lang.String submitOrg) {
        set("submitOrg", submitOrg);
    }

    public java.lang.Float getSiniDataValue() {
        return get("siniDataValue");
    }

    public void setSiniDataValue(java.lang.Float siniDataValue) {
        set("siniDataValue", siniDataValue);
    }

    public java.lang.Integer getSiniDataRegion() {
        return get("siniDataRegion");
    }

    public void setSiniDataRegion(java.lang.Integer siniDataRegion) {
        set("siniDataRegion", siniDataRegion);
    }

    public java.lang.Integer getSiniDataCheckMark() {
        return get("siniDataCheckMark");
    }

    public void setSiniDataCheckMark(java.lang.Integer siniDataCheckMark) {
        set("siniDataCheckMark", siniDataCheckMark);
    }

    public Date getSiniDate() {
        return get("siniDate");
    }

    public void setSiniDate(Date siniDate) {
        set("siniDate", siniDate);
    }

    public java.lang.Integer getSiniStatus() {
        return get("siniStatus");
    }

    public void setSiniStatus(java.lang.Integer siniStatus) {
        set("siniStatus", siniStatus);
    }

    public String actions = "actions";

    public String getActions() {
        return actions;
    }
}
