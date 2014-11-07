package com.hwtx.fncel.pbc.service;

import com.google.common.collect.Lists;
import com.hwtx.fncel.pbc.dao.SurveyDao;
import com.hwtx.fncel.pbc.entity.*;
import com.hwtx.fncel.pbc.util.AppConstants;
import com.hwtx.fncel.pbc.vo.ComputeValue;
import com.hwtx.fncel.pbc.vo.SurveiesCount;
import com.hwtxframework.ioc.annotation.Component;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.thinkgem.jeesite.common.utils.IdGen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by panye on 2014/10/10.
 */
@Component
public class SurveyService {

    @Resource
    private SurveyDao surveyDao;
    @Resource
    private DataComputeService dataComputeService;
    @Resource
    private RuleService ruleService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public List<DefSurvey> getSurveies() {
        return surveyDao.getSurveies();
    }

    public List<DefQuestions> getQuestions(String type) {
        List<Record> records = surveyDao.getQuestions(type);
        List<DefQuestions> questionses = Lists.newArrayList();
        for (int i = 0; i < records.size(); i++) {
            Record record = records.get(i);
            DefQuestions question = new DefQuestions();
            for (String columnName : record.getColumnNames()) {
                if (columnName.startsWith(AppConstants.OPTION)) {
                    String prefix = columnName.substring(AppConstants.OPTION.length());
                    String value = record.getStr(columnName);
                    if (!StrKit.isBlank(value)) {
                        question.addOption(columnName, prefix + "、" + value);
                    }
                } else {
                    question.set(columnName, record.get(columnName));
                }
            }
            questionses.add(question);
        }
        return questionses;
    }

    @Before(Tx.class)
    public void save(String surveyId, Integer region, Map<String, String> answers) {
        Respondents respondents = new Respondents();
        String respondentId = IdGen.uuid();
        respondents.setRespondId(respondentId);
        respondents.setSubmitTime(new Date());
        respondents.setSurveyId(surveyId);
        respondents.setSurveyRegion(region.toString());
        surveyDao.saveRespondents(respondents);
        List<AnswerScore> params = Lists.newArrayList();
        for (Map.Entry<String, String> entry : answers.entrySet()) {
            AnswerScore answerScore = new AnswerScore();
            answerScore.setSurveyId(surveyId);
            answerScore.setRespondId(respondentId);
            answerScore.setId(IdGen.uuid());
            answerScore.setQuestionId(Integer.valueOf(entry.getKey()));
            answerScore.set(entry.getValue(), 1);
            params.add(answerScore);
        }
        surveyDao.saveAnswers(params);
    }

    public List<SurveiesCount> getSurveiesCount(Integer region, Date date) {
        List<Record> records = surveyDao.getSurveiesCount(region, date);
        List<SurveiesCount> result = Lists.newArrayList();
        for (Record record : records) {
            SurveiesCount surveiesCount = new SurveiesCount();
            surveiesCount.setSurveyName(record.getStr("surveyName"));
            surveiesCount.setSurveyId(record.getStr("surveyId"));
            surveiesCount.setCount(record.getLong("count"));
            result.add(surveiesCount);
        }
        return result;
    }

    public List<ComputeValue> handleSurveyStandardize(Integer region, Date date, String defCatId) {
        List<Record> records = surveyDao.getQuestionCountAndType(region, date);
        List<QuestionScore> questionScores = Lists.newArrayList();
        float sum_score = 0;
        int question_count = 0;
        List<Record> options = surveyDao.countOptions();
        for (Record record : records) {
            long count = record.getLong("count");
            Integer questionId = record.getInt("questionId");
            if (count == 0) {
                logger.warn("nobody select question " + questionId);
                continue;
            }
            Integer optionAValue = record.getInt("optionAValue");
            Integer optionBValue = record.getInt("optionBValue");
            Integer optionCValue = record.getInt("optionCValue");
            Integer optionDValue = record.getInt("optionDValue");
            String questionTypeId = record.getStr("questionTypeId");
            long dValue = 0;
            if (optionDValue != null) {
                dValue = optionDValue.longValue();
            }
            Integer optionEValue = record.getInt("optionEValue");
            long eValue = 0;
            if (optionEValue != null) {
                eValue = optionEValue.longValue();
            }
            if (options.size() == 0) {
                logger.warn("can't get question " + questionId + " options info");
                continue;
            }
            QuestionScore questionScore = new QuestionScore();
            questionScore.setQuestionId(questionId);
            questionScore.setDate(date);
            questionScore.setQuestionTypeId(questionTypeId);
            questionScore.setStandardizeId(IdGen.uuid());
            questionScore.setRegion(region);
            questionScore.setSurveyId(record.getStr("surveyId"));
            Record option = getOptionCountByQuestionId(questionId, options);
            long countA = option.getLong("countA");
            long countB = option.getLong("countB");
            long countC = option.getLong("countC");
            if (dValue == 0) {
                questionScore.setStandardizeScore(handleThree(countA, countB, countC, optionAValue, optionBValue, optionCValue));
            } else {
                long countD = option.getLong("countD");
                if (eValue == 0) {
                    questionScore.setStandardizeScore(handleFour(countA, countB, countC, countD, optionAValue,
                            optionBValue, optionCValue, dValue));
                } else {
                    questionScore.setStandardizeScore(handleFive(countA, countB, countC, countD, option.getLong("countE"),
                            optionAValue, optionBValue, optionCValue, dValue, eValue));
                }
            }
            sum_score += questionScore.getStandardizeScore();
            question_count++;
            questionScores.add(questionScore);
        }

        surveyDao.saveQuestionScore(questionScores);
        List<ComputeValue> result = Lists.newArrayList();
        if (question_count != 0) {
            handleCat(defCatId, sum_score, question_count, region, date, result);
        }
        return result;
    }

    private Record getOptionCountByQuestionId(Integer questionId, List<Record> options) {
        for (Record record : options) {
            if (record.getInt("questionId") == questionId) {
                return record;
            }
        }
        return null;
    }

    private void handleCat(String defCatId, float sum_score, int count, Integer region, Date date,
                           List<ComputeValue> result) {
        float value = (sum_score / count) * 100 / 100;
        String vcatId = dataComputeService.saveCatValue(defCatId, value, region, date);
        DefCat defCat = ruleService.getDefCat(defCatId);
        result.add(new ComputeValue(defCatId, defCat.getDcatName(), "0", value));
        for (DefClass defClass : ruleService.getDefClasses(defCatId)) {
            handleClass(defClass, vcatId, value, region, date, result);
        }
    }

    private void handleClass(DefClass defClass, String vcatId, float value, Integer region, Date date,
                             List<ComputeValue> result) {
        String vClassId = dataComputeService.saveClassValue(defClass.getDclassId(), vcatId, value, region, date);
        result.add(new ComputeValue(defClass.getDclassId(), defClass.getDclassName(), defClass.getDcatId(), value));
        for (DefKpi defKpi : ruleService.getDefKpis(defClass.getDclassId())) {
            handleKpi(defKpi, vClassId, value, region, date, result);
        }
    }

    private void handleKpi(DefKpi defKpi, String vClassId, float value, Integer region, Date date,
                           List<ComputeValue> result) {
        dataComputeService.saveKpiValue(defKpi.getDkpiId(), vClassId, region, date, value);
        result.add(new ComputeValue(defKpi.getDkpiId(), defKpi.getDkpiName(), defKpi.getDclassId(), value));
    }

    private float handleThree(long countA, long countB, long countC, long optionAValue, long optionBValue, long optionCValue) {
        double value = (countA * optionAValue + countB * optionBValue + countC * optionCValue) / (countA + countB +
                countC);
        return ((int) (value * 100)) / 100;
    }

    private float handleFour(long countA, long countB, long countC, long countD, long optionAValue, long optionBValue,
                             long optionCValue, long optionDValue) {
        double value = (countA * optionAValue + countB * optionBValue + countC * optionCValue + countD * optionDValue)
                / (countA + countB + countC + countD);
        return ((int) (value * 100)) / 100;
    }

    private float handleFive(long countA, long countB, long countC, long countD, long countE, long optionAValue, long optionBValue,
                             long optionCValue, long optionDValue, long optionEValue) {
        double value = (countA * optionAValue + countB * optionBValue + countC * optionCValue + countD * optionDValue
                + countE * optionEValue) / (countA + countB + countC + countD + countE);
        return ((int) (value * 100)) / 100;
    }
}
