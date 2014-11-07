package com.hwtx.fncel.pbc.dao;

import com.hwtx.fncel.pbc.entity.AnswerScore;
import com.hwtx.fncel.pbc.entity.DefSurvey;
import com.hwtx.fncel.pbc.entity.QuestionScore;
import com.hwtx.fncel.pbc.entity.Respondents;
import com.hwtx.fncel.pbc.util.AppConstants;
import com.hwtxframework.ioc.annotation.Component;
import com.jfinal.ext.plugin.sqlinxml.SqlKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.Date;
import java.util.List;

/**
 * Created by panye on 2014/10/10.
 */
@Component
public class SurveyDao {

    public List<DefSurvey> getSurveies() {
        return DefSurvey.dao.find(SqlKit.sql(AppConstants.survey_getSurveies));
    }

    public List<Record> getQuestions(String type) {
        return Db.find(SqlKit.sql(AppConstants.survey_getQuestions), type);
    }

    public void saveRespondents(Respondents respondents) {
        respondents.save();
    }

    public void saveAnswers(List<AnswerScore> answerScores) {
        Object[][] params = new Object[answerScores.size()][11];
        for (int i = 0; i < answerScores.size(); i++) {
            AnswerScore answerScore = answerScores.get(i);
            params[i] = new Object[]{answerScore.getId(), answerScore.getRespondId(), answerScore.getQuestionId()
                    , answerScore.getSurveyId(), answerScore.getOptionA(), answerScore.getOptionB(),
                    answerScore.getOptionC(), answerScore.getOptionD(), answerScore.getOptionE(),
                    answerScore.getOptionF(), answerScore.getAnswerDesc()};
        }
        Db.batch(SqlKit.sql(AppConstants.survey_saveAnswers), params, params.length);
    }

    public List<Record> getSurveiesCount(Integer region, Date date) {
        return Db.find(SqlKit.sql(AppConstants.survey_getSurveiesCount), region, date);
    }

    public List<Record> getQuestionCountAndType(Integer region, Date date) {
        return Db.find(SqlKit.sql(AppConstants.survey_getQuestionCountAndType), date, region);
    }

    public List<Record> countOptions() {
        return Db.find(SqlKit.sql(AppConstants.survey_countOptions));
    }

    public void saveQuestionScore(List<QuestionScore> questionScores) {
        QuestionScore.dao.batchSave(questionScores);
    }
}
