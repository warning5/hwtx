package com.hwtx.fncel.pbc.entity;

import com.google.common.collect.Maps;
import com.jfinal.ext.plugin.tablebind.TableBind;
import com.thinkgem.jeesite.common.persistence.DataEntity;
import lombok.Getter;

import java.util.Map;

@TableBind(tableName = "def_questions", pkName = "questionId")
public class DefQuestions extends DataEntity<DefQuestions> {

    public static DefQuestions dao = new DefQuestions();

    public Integer getQuestionId() {
        return get("questionId");
    }

    public void setQuestionId(Integer questionId) {
        set("questionId", questionId);
    }

    public java.lang.String getQuestionType() {
        return get("questionType");
    }

    public void setQuestionType(java.lang.String questionType) {
        set("questionType", questionType);
    }

    public java.lang.String getQuestionDesc() {
        return get("questionDesc");
    }

    public void setQuestionDesc(java.lang.String questionDesc) {
        set("questionDesc", questionDesc);
    }

    public java.lang.Float getQuestionDefScore() {
        return get("questionDefScore");
    }

    public void setQuestionDefScore(java.lang.Float questionDefScore) {
        set("questionDefScore", questionDefScore);
    }

    public java.lang.String getOptionA() {
        return get("optionA");
    }

    public void setOptionA(java.lang.String optionA) {
        set("optionA", optionA);
    }

    public java.lang.String getOptionB() {
        return get("optionB");
    }

    public void setOptionB(java.lang.String optionB) {
        set("optionB", optionB);
    }

    public java.lang.String getOptionC() {
        return get("optionC");
    }

    public void setOptionC(java.lang.String optionC) {
        set("optionC", optionC);
    }

    public java.lang.String getOptionD() {
        return get("optionD");
    }

    public void setOptionD(java.lang.String optionD) {
        set("optionD", optionD);
    }

    public java.lang.String getOptionE() {
        return get("optionE");
    }

    public void setOptionE(java.lang.String optionE) {
        set("optionE", optionE);
    }

    public java.lang.String getOptionF() {
        return get("optionF");
    }

    public void setOptionF(java.lang.String optionF) {
        set("optionF", optionF);
    }

    public java.lang.String getRemark() {
        return get("Remark");
    }

    public void setRemark(java.lang.String Remark) {
        set("Remark", Remark);
    }

    public java.lang.String getQuestionRespoder() {
        return get("questionRespoder");
    }

    public void setQuestionRespoder(java.lang.String questionRespoder) {
        set("questionRespoder", questionRespoder);
    }

    @Getter
    private Map<String, String> options = Maps.newLinkedHashMap();

    public void addOption(String key, String value) {
        options.put(key, value);
    }

    public String actions = "actions";

    public String getActions() {
        return actions;
    }
}
