package com.hwtx.fncel.pbc.entity;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.thinkgem.jeesite.common.persistence.DataEntity;
import lombok.Getter;
import lombok.Setter;

@TableBind(tableName = "def_formula", pkName = "calRuleId")
public class DefFormula extends DataEntity<DefFormula> {

    public static DefFormula dao = new DefFormula();


    public java.lang.String getCalRuleId() {
        return get("calRuleId");
    }

    public void setCalRuleId(java.lang.String calRuleId) {
        set("calRuleId", calRuleId);
    }

    public java.lang.String getCalObjectiveId() {
        return get("calObjectiveId");
    }

    public void setCalObjectiveId(java.lang.String calObjectiveId) {
        set("calObjectiveId", calObjectiveId);
    }

    public java.lang.String getCalRule() {
        return get("calRule");
    }

    public void setCalRule(java.lang.String calRule) {
        set("calRule", calRule);
    }

    public java.lang.String getCalRuleType() {
        return get("calRuleType");
    }

    public void setCalRuleType(java.lang.String calRuleType) {
        set("calRuleType", calRuleType);
    }

    public java.lang.Integer getCalExpireMark() {
        return get("calExpireMark");
    }

    public void setCalExpireMark(java.lang.Integer calExpireMark) {
        set("calExpireMark", calExpireMark);
    }

    @Getter
    @Setter
    private String calRuleShow;

    public java.lang.String getInidatas() {
        return get("inidatas");
    }

    public void setInidatas(java.lang.String inidatas) {
        set("inidatas", inidatas);
    }

    public String actions = "actions";

    public String getActions() {
        return actions;
    }
}
