package com.hwtx.fncel.pbc.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hwtx.fncel.pbc.entity.DefQuestions;
import com.hwtx.fncel.pbc.entity.DefSurvey;
import com.hwtx.fncel.pbc.service.AppOrgService;
import com.hwtx.fncel.pbc.service.SurveyService;
import com.hwtx.fncel.pbc.vo.SurveiesCount;
import com.hwtx.framework.interceptor.ModuleIocInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.thinkgem.jeesite.common.Constants;
import com.thinkgem.jeesite.common.web.BaseController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Before(ModuleIocInterceptor.class)
@ControllerBind(controllerKey = "${adminPath}/app/survey")
public class SurveyController extends BaseController {

    @Resource
    private SurveyService surveyService;
    @Resource
    private AppOrgService appOrgService;

    @ActionKey(value = {"/"})
    public void survey() {
        List<DefSurvey> surveys = surveyService.getSurveies();
        setAttr("surveies", surveys);
        if (surveys.size() != 0) {
            List<SurveiesCount> surveiesCounts = surveyService.getSurveiesCount(appOrgService.getRegion(),
                    surveys.get(0).getSurveyDate());
            if (surveiesCounts.size() == 0) {
                surveiesCounts = Lists.newArrayList();
                for (DefSurvey defSurvey : surveys) {
                    SurveiesCount surveiesCount =new SurveiesCount();
                    surveiesCount.setSurveyId(defSurvey.getSurveyId());
                    surveiesCount.setSurveyName(defSurvey.getSurveyName());
                    surveiesCount.setCount(0);
                    surveiesCounts.add(surveiesCount);
                }
            }
            setAttr("surveiesCount", surveiesCounts);
        }
        render("/app/data/survey.jsp");
    }

    @ActionKey(value = {"questions"})
    public void questions() {
        String surveyId = getPara("type");
        List<DefQuestions> defQuestionses = surveyService.getQuestions(surveyId);
        setAttr("questions", defQuestionses);
        int size = defQuestionses.size();
        setAttr("steps", size / 2 + size % 2);
        setAttr("surveyId", surveyId);
        render("/app/data/questions.jsp");
    }

    @ActionKey(value = {"submit"})
    public void submit() {
        String surveyId = getPara("surveyId");
        Map<String, String> answers = Maps.newHashMap();
        for (Map.Entry<String, String[]> entry : getParaMap().entrySet()) {
            if (entry.getKey().equals("surveyId")) {
                continue;
            }
            answers.put(entry.getKey(), entry.getValue()[0]);
        }
        try {
            surveyService.save(surveyId, appOrgService.getRegion(), answers);
            renderJson(getRenderJson(Constants.RENDER_JSON_SUCCESS, "提交成功!"));
        } catch (Exception e) {
            renderJson(getErrorRenderJson("提交失败", HttpServletResponse.SC_BAD_REQUEST));
            return;
        }


    }
}