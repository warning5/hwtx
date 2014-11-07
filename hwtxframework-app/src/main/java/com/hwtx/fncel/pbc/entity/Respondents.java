package com.hwtx.fncel.pbc.entity;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.Date;

@TableBind(tableName = "respondents", pkName = "respondId")
public class Respondents extends DataEntity<Respondents> {

    public static Respondents dao = new Respondents();

    
    public java.lang.String getRespondId() {
        return get("respondId");
    }

    public void setRespondId(java.lang.String respondId) {
        set("respondId",respondId);
    }
        
    public Date getSubmitTime() {
        return get("submitTime");
    }

    public void setSubmitTime(Date submitTime) {
        set("submitTime",submitTime);
    }
        
    public java.lang.String getSurveyId() {
        return get("surveyId");
    }

    public void setSurveyId(java.lang.String surveyId) {
        set("surveyId",surveyId);
    }
        
    public java.lang.Integer getSurveyValidity() {
        return get("surveyValidity");
    }

    public void setSurveyValidity(java.lang.Integer surveyValidity) {
        set("surveyValidity",surveyValidity);
    }
        
    public java.lang.Integer getSubmitQuestions() {
        return get("submitQuestions");
    }

    public void setSubmitQuestions(java.lang.Integer submitQuestions) {
        set("submitQuestions",submitQuestions);
    }
        
    public java.lang.String getSurveyRegion() {
        return get("surveyRegion");
    }

    public void setSurveyRegion(java.lang.String surveyRegion) {
        set("surveyRegion",surveyRegion);
    }
        
    public java.lang.String getRespondOrgType() {
        return get("respondOrgType");
    }

    public void setRespondOrgType(java.lang.String respondOrgType) {
        set("respondOrgType",respondOrgType);
    }
    
    public String actions = "actions";

    public String getActions() {
        return actions;
    }
}
