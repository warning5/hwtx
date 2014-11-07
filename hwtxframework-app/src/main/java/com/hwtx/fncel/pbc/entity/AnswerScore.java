package com.hwtx.fncel.pbc.entity;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.thinkgem.jeesite.common.persistence.DataEntity;

@TableBind(tableName = "answer_score", pkName = "id")
public class AnswerScore extends DataEntity<AnswerScore> {

    public static AnswerScore dao = new AnswerScore();


    public java.lang.String getId() {
        return get("id");
    }

    public void setId(java.lang.String id) {
        set("id", id);
    }

    public java.lang.String getRespondId() {
        return get("respondId");
    }

    public void setRespondId(java.lang.String respondId) {
        set("respondId", respondId);
    }

    public Integer getQuestionId() {
        return get("questionId");
    }

    public void setQuestionId(Integer questionId) {
        set("questionId", questionId);
    }

    public java.lang.String getSurveyId() {
        return get("surveyId");
    }

    public void setSurveyId(java.lang.String surveyId) {
        set("surveyId", surveyId);
    }

    public java.lang.Integer getOptionA() {
        return get("optionA");
    }

    public void setOptionA(java.lang.Integer optionA) {
        set("optionA", optionA);
    }

    public java.lang.Integer getOptionB() {
        return get("optionB");
    }

    public void setOptionB(java.lang.Integer optionB) {
        set("optionB", optionB);
    }

    public java.lang.Integer getOptionC() {
        return get("optionC");
    }

    public void setOptionC(java.lang.Integer optionC) {
        set("optionC", optionC);
    }

    public java.lang.Integer getOptionD() {
        return get("optionD");
    }

    public void setOptionD(java.lang.Integer optionD) {
        set("optionD", optionD);
    }

    public java.lang.Integer getOptionE() {
        return get("optionE");
    }

    public void setOptionE(java.lang.Integer optionE) {
        set("optionE", optionE);
    }

    public java.lang.Integer getOptionF() {
        return get("optionF");
    }

    public void setOptionF(java.lang.Integer optionF) {
        set("optionF", optionF);
    }

    public java.lang.String getAnswerDesc() {
        return get("answerDesc");
    }

    public void setAnswerDesc(java.lang.String answerDesc) {
        set("answerDesc", answerDesc);
    }

    public String actions = "actions";

    public String getActions() {
        return actions;
    }
}
