package com.hwtx.fncel.pbc.entity;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.Date;

@TableBind(tableName = "def_survey", pkName = "surveyId")
public class DefSurvey extends DataEntity<DefSurvey> {

    public static DefSurvey dao = new DefSurvey();

    
    public java.lang.String getSurveyId() {
        return get("surveyId");
    }

    public void setSurveyId(java.lang.String surveyId) {
        set("surveyId",surveyId);
    }
        
    public java.lang.String getSurveyName() {
        return get("surveyName");
    }

    public void setSurveyName(java.lang.String surveyName) {
        set("surveyName",surveyName);
    }
        
    public java.lang.Integer getTotalQuestions() {
        return get("totalQuestions");
    }

    public void setTotalQuestions(java.lang.Integer totalQuestions) {
        set("totalQuestions",totalQuestions);
    }
        
    public Date getSurveyDate() {
        return get("surveyDate");
    }

    public void setSurveyDate(Date surveyDate) {
        set("surveyDate",surveyDate);
    }
                        
    public java.lang.Integer getEnable() {
        return get("enable");
    }

    public void setEnable(java.lang.Integer enable) {
        set("enable",enable);
    }
    
    public String actions = "actions";

    public String getActions() {
        return actions;
    }
}
