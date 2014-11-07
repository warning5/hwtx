package com.hwtx.fncel.pbc.entity;

import com.hwtx.fncel.pbc.util.AppConstants;
import com.hwtx.modules.sys.utils.DictUtils;
import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.kit.StrKit;
import com.thinkgem.jeesite.common.persistence.DataEntity;

@TableBind(tableName = "def_inidata", pkName = "diniDataId")
public class DefInidata extends DataEntity<DefInidata> {

    public static DefInidata dao = new DefInidata();
    public String actions = "actions";

    public java.lang.String getDkpiId() {
        return get("dkpiId");
    }

    public void setDkpiId(java.lang.String dkpiId) {
        set("dkpiId", dkpiId);
    }

    public java.lang.String getDiniDataCalRuleId() {
        return get("diniDataCalRuleId");
    }

    public void setDiniDataCalRuleId(java.lang.String diniDataCalRuleId) {
        set("diniDataCalRuleId", diniDataCalRuleId);
    }

    public java.lang.String getDiniDataId() {
        return get("diniDataId");
    }

    public void setDiniDataId(java.lang.String diniDataId) {
        set("diniDataId", diniDataId);
    }

    public java.lang.String getDiniDataAuthRuleId() {
        return get("diniDataAuthRuleId");
    }

    public void setDiniDataAuthRuleId(java.lang.String diniDataAuthRuleId) {
        set("diniDataAuthRuleId", diniDataAuthRuleId);
    }

    public java.lang.String getDiniDataName() {
        return get("diniDataName");
    }

    public void setDiniDataName(java.lang.String diniDataName) {
        set("diniDataName", diniDataName);
    }

    public java.lang.String getSubmitOrgRole() {
        return get("submitOrgRole");
    }

    public void setSubmitOrgRole(java.lang.String submitOrgRole) {
        set("submitOrgRole", submitOrgRole);
    }

    public java.lang.Integer getDiniDataExpireMark() {
        return get("diniDataExpireMark");
    }

    public void setDiniDataExpireMark(java.lang.Integer diniDataExpireMark) {
        set("diniDataExpireMark", diniDataExpireMark);
    }

    public java.lang.Integer getIsStaticMark() {
        return get("isStaticMark");
    }

    public void setIsStaticMark(java.lang.Integer isStaticMark) {
        set("isStaticMark", isStaticMark);
    }

    public java.lang.Integer getDiniDataDelMark() {
        return get("diniDataDelMark");
    }

    public void setDiniDataDelMark(java.lang.Integer diniDataDelMark) {
        set("diniDataDelMark", diniDataDelMark);
    }

    public java.lang.String getDiniDataType() {
        return get("diniDataType");
    }

    public void setDiniDataType(java.lang.String diniDataType) {
        set("diniDataType", diniDataType);
    }

    public String getShowDiniDataType() {
        return DictUtils.getDictLabel(getDiniDataType(), AppConstants.DICT_DATA_TYPE, "");
    }

    public void setDiniDataUnit(java.lang.String diniDataUnit) {
        set("diniDataUnit", diniDataUnit);
    }

    public String getDiniDataUnit() {
        return get("diniDataUnit");
    }

    public java.lang.String getDiniDataRemark() {
        return get("diniDataRemark");
    }

    public void setDiniDataRemark(java.lang.String diniDataRemark) {
        set("diniDataRemark", diniDataRemark);
    }

    public java.lang.String getDiniDataLabel() {
        return get("diniDataLabel");
    }

    public void setDiniDataLabel(java.lang.String diniDataLabel) {
        set("diniDataLabel", diniDataLabel);
    }

    public java.lang.Integer getDiniDataRequired() {
        return get("diniDataRequired");
    }

    public void setDiniDataRequired(java.lang.Integer diniDataRequired) {
        set("diniDataRequired", diniDataRequired);
    }

    public java.lang.Integer getDiniDataMaxLength() {
        return get("diniDataMaxLength");
    }

    public void setDiniDataMaxLength(java.lang.Integer diniDataMaxLength) {
        set("diniDataMaxLength", diniDataMaxLength);
    }

    public java.lang.Integer getDiniDataMinLength() {
        return get("diniDataMinLength");
    }

    public void setDiniDataMinLength(java.lang.Integer diniDataMinLength) {
        set("diniDataMinLength", diniDataMinLength);
    }

    public Integer getDiniFractionDigits() {
        return get("diniFractionDigits");
    }

    public void setDiniFractionDigits(Integer diniFractionDigits) {
        set("diniFractionDigits", diniFractionDigits);
    }

    public Double getDiniDataMaxValue() {
        return get("diniDataMaxValue");
    }

    public void setDiniDataMaxValue(Double diniDataMaxValue) {
        set("diniDataMaxValue", diniDataMaxValue);
    }

    public Double getDiniDataMinValue() {
        return get("diniDataMinValue");
    }

    public void setDiniDataMinValue(Double diniDataMinValue) {
        set("diniDataMinValue", diniDataMinValue);
    }

    public String getActions() {
        return actions;
    }

    public boolean isSeachItemNull() {
        return StrKit.isBlank(getDiniDataLabel()) && StrKit.isBlank(getSubmitOrgRole());
    }

    public String getOrgRole() {
        return get("orgRole");
    }

    public void setOrgRole(String orgRole) {
        set("orgRole", orgRole);
    }
}
