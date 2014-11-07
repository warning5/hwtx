package com.hwtx.fncel.pbc.entity;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.Date;

@TableBind(tableName = "question_score", pkName = "standardizeId")
public class QuestionScore extends DataEntity<QuestionScore> {

    public static QuestionScore dao = new QuestionScore();


    public java.lang.String getStandardizeId() {
        return get("standardizeId");
    }

    public void setStandardizeId(java.lang.String standardizeId) {
        set("standardizeId", standardizeId);
    }

    public Integer getQuestionId() {
        return get("questionId");
    }

    public void setQuestionId(Integer questionId) {
        set("questionId", questionId);
    }

    public java.lang.Float getStandardizeScore() {
        return get("standardizeScore");
    }

    public void setStandardizeScore(java.lang.Float standardizeScore) {
        set("standardizeScore", standardizeScore);
    }

    public java.lang.String getSurveyId() {
        return get("surveyId");
    }

    public void setSurveyId(java.lang.String surveyId) {
        set("surveyId", surveyId);
    }

    public java.lang.String getQuestionTypeId() {
        return get("questionTypeId");
    }

    public void setQuestionTypeId(java.lang.String questionTypeId) {
        set("questionTypeId", questionTypeId);
    }

    public Date getDate() {
        return get("date");
    }

    public void setDate(Date date) {
        set("date", date);
    }

    public java.lang.Integer getRegion() {
        return get("region");
    }

    public void setRegion(java.lang.Integer region) {
        set("region", region);
    }

    public String actions = "actions";

    public String getActions() {
        return actions;
    }
}
