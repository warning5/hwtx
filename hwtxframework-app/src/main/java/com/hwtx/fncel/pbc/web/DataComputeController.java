package com.hwtx.fncel.pbc.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hwtx.fncel.pbc.entity.*;
import com.hwtx.fncel.pbc.exception.DataComputeException;
import com.hwtx.fncel.pbc.exception.ValueNotExistException;
import com.hwtx.fncel.pbc.service.*;
import com.hwtx.fncel.pbc.util.AppConstants;
import com.hwtx.fncel.pbc.vo.ComputeValue;
import com.hwtx.fncel.pbc.vo.SaveKpiValue;
import com.hwtx.fncel.pbc.vo.SubmitKpiVo;
import com.hwtx.fncel.util.FncelUtils;
import com.hwtx.framework.interceptor.ModuleIocInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page1;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.thinkgem.jeesite.common.utils.IdGen;
import com.thinkgem.jeesite.common.web.BaseController;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Before(ModuleIocInterceptor.class)
@ControllerBind(controllerKey = "${adminPath}/app/compute")
public class DataComputeController extends BaseController {

    @Resource
    private AppOrgService appOrgService;
    @Resource
    private RuleService ruleService;
    @Resource
    private IniDataService iniDataService;
    @Resource
    private DataInputService dataInputService;
    @Resource
    private DataComputeService dataComputeService;
    @Resource
    private SurveyService surveyService;

    private ExpressRunner runner = new ExpressRunner();

    @ActionKey(value = {"cp"})
    public void two() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        setAttr("date", simpleDateFormat.format(new Date()));
        setAttr("survey", getPara("survey"));
        render("/app/data/compute.jsp");
    }

    @ActionKey(value = {"list"})
    public void list() {
        AppOrg appOrg = appOrgService.getAppOrg();
        setAttr("region", appOrgService.getRegion(appOrg));
        setAttr("orgId", appOrg.getOrgId());
        render("/app/data/list.jsp");
    }

    @Override
    protected Page1<SubmitKpiVo> getPageData(int iDisplayStart, int fcount) {
        return dataComputeService.getSubmitKpiData(iDisplayStart, fcount, appOrgService.getRegion(), AppConstants.Value_Inidata_Save_Status);
    }

    @ActionKey(value = {"submit"})
    public void submit() {

        Pair<Date, Integer> pair = beforeComputecheck();
        if (pair == null) {
            return;
        }
        Date date = pair.getLeft();
        Integer region = pair.getRight();

        List<ComputeValue> result = null;
        boolean cp = dataComputeService.hasComputeKpi(region, date);

        if (!cp) {
            result = compute();
        }
        if (cp || (!cp && result != null)) {
            try {
                dataComputeService.submitData(region, date);
                renderHtml("上报成功");
            } catch (Exception e) {
                logger.error("{}", e);
                getResponse().setStatus(HttpServletResponse.SC_BAD_REQUEST);
                renderHtml("上报失败");
            }
        }
    }

    @ActionKey(value = {"doIt"})
    public void doIt() {
        List<ComputeValue> result = compute();
        if (result != null) {
            setAttr("result", result);
            render("/app/data/computeTable.jsp");
        }
    }


    private Pair<Date, Integer> beforeComputecheck() {
        String date = getPara("startdate");
        Date startdate = null;
        try {
            startdate = FncelUtils.getDate(date);
        } catch (ParseException e) {
            renderHtml("选择的日期不正确");
            logger.error("{}", e);
            return null;
        }
        Integer region = appOrgService.getRegion();
        if (region == null) {
            renderHtml("用户区域不存在");
            return null;
        }

        if (dataComputeService.checkSubmitDataExist(region, startdate, AppConstants.Value_Inidata_Submit_Status)) {
            renderHtml(date + "指标值已存在，无法再次计算和提交");
            return null;
        }
        return new ImmutablePair(startdate, region);
    }

    private List<ComputeValue> compute() {

        List<ComputeValue> result = Lists.newArrayList();
        Pair<Date, Integer> pair = beforeComputecheck();
        if (pair == null) {
            return null;
        }
        Date date = pair.getLeft();
        Integer region = pair.getRight();
        boolean includeStatistic;
        try {
            includeStatistic = checkInputValues(region, date);
        } catch (ValueNotExistException e) {
            String message = e.getType() + "填报的" + e.getName() + "不存在" + date + "的数据";
            logger.error(message, e);
            renderHtml(message);
            return null;
        }

        String survey = getPara("survey");
        Map<String, Triple<String, List<String>, IExpressContext<String, Object>>> delay = Maps.newHashMap();
        List<Triple<String, String, Float>> catIdAndValues = Lists.newArrayList();
        List<SaveKpiValue> saveKpis = Lists.newArrayList();
        for (DefCat defCat : ruleService.getDefCats().values()) {
            try {
                if (defCat.getDcatId().equals(survey)) {
                    result.addAll(handleSurvey(region, date, defCat.getDcatId()));
                } else {
                    result.add(new ComputeValue(defCat.getDcatId(), defCat.getDcatName(), "0", 0));
                    String vcatId = IdGen.uuid();
                    handleCat(defCat, region, date, vcatId, result, delay, saveKpis);
                    catIdAndValues.add(new ImmutableTriple<>(defCat.getDcatId(), vcatId, 0f));
                }
            } catch (DataComputeException e) {
                renderHtml(e.getMessage());
                return null;
            }
        }
        dataComputeService.saveCatValues(catIdAndValues, date, region);
        if (includeStatistic) {
            dataComputeService.updateValueIinidataStatisticStatus(region, date, AppConstants.Value_Inidata_Save_Status);
        }
        if (delay.size() != 0) {
            for (Map.Entry<String, Triple<String, List<String>, IExpressContext<String, Object>>> entry : delay.entrySet()) {
                Triple<String, List<String>, IExpressContext<String, Object>> triple = entry.getValue();
                IExpressContext<String, Object> expressContext = triple.getRight();
                for (String kpiId : triple.getMiddle()) {
                    Float value = dataComputeService.getKpiValue(kpiId, date);
                    DefKpi defKpi = ruleService.getDefKpi(kpiId);
                    if (value < 0) {
                        renderHtml("无法获取指标[" + defKpi.getDkpiName() + "]的值");
                        return null;
                    }
                    expressContext.put(AppConstants.kpi_prefix + defKpi.getDkpiId(), value);
                }
                DefKpi defKpi = ruleService.getDefKpi(entry.getKey());
                try {
                    SaveKpiValue saveKpiValue = doCompute(defKpi, ruleService.getFormulaByKpiId(entry.getKey()),
                            triple.getLeft(), expressContext);
                    saveKpis.add(saveKpiValue);
                    result.add(new ComputeValue(defKpi.getDkpiId(), defKpi.getDkpiName(), defKpi.getDclassId(),
                            saveKpiValue.getValue()));
                } catch (Throwable e) {
                    logger.error("{}", e);
                    renderHtml("无法计算指标[" + defKpi.getDkpiName() + "]的值");
                    return null;
                }
            }
        }
        dataComputeService.saveKpiValues(saveKpis, region, date);
        return result;
    }

    private boolean checkInputValues(Integer region, Date date) throws ValueNotExistException {
        Map<String, String> submitOrg = iniDataService.getSubmitOrgRole();
        boolean includeStatistic = false;
        for (String key : submitOrg.keySet()) {
            List<DefInidata> defInidatas = iniDataService.getDefInidatasBySubmitRole(key);
            if (defInidatas == null || defInidatas.size() == 0) {
                continue;
            }
            for (DefInidata defInidata : defInidatas) {
                if (!includeStatistic) {
                    includeStatistic = defInidata.getIsStaticMark() != 0;
                }
                boolean exist = dataInputService.checkValueExist(region, date, defInidata.getDiniDataId(),
                        defInidata.getIsStaticMark(), defInidata.getSubmitOrgRole(), AppConstants.Value_Inidata_Save_Status,
                        AppConstants.Value_Inidata_Back_Status);
                if (!exist) {
                    throw new ValueNotExistException(defInidata.getDiniDataLabel(), submitOrg.get(key));
                }
            }
        }
        return includeStatistic;
    }

    private void handleCat(DefCat defCat, Integer region, Date date, String vcatId, List<ComputeValue> result,
                           Map<String, Triple<String, List<String>, IExpressContext<String, Object>>> delay,
                           List<SaveKpiValue> saveKpis) throws DataComputeException {
        List<DefClass> defClasses = ruleService.getDefClasses(defCat.getDcatId());
        if (defClasses == null) {
            throw new RuntimeException("can't find defClass by DefCat " + defCat.getDcatId());
        }
        List<Triple<String, String, Float>> classIdAndValue = Lists.newArrayList();
        for (DefClass defClass : defClasses) {
            result.add(new ComputeValue(defClass.getDclassId(), defClass.getDclassName(), defClass.getDcatId(), 0));
            String vclassId = IdGen.uuid();
            handleClass(defClass, region, date, vclassId, result, delay, saveKpis);
            classIdAndValue.add(new ImmutableTriple<>(defClass.getDclassId(), vclassId, 0f));
        }
        dataComputeService.saveClassValues(classIdAndValue, vcatId, date, region);
    }

    private List<ComputeValue> handleSurvey(Integer region, Date date, String defCatId) {
        return surveyService.handleSurveyStandardize(region, date, defCatId);
    }

    private void handleClass(DefClass defClass, Integer region, Date startdate, String vclassId, List<ComputeValue> result,
                             Map<String, Triple<String, List<String>, IExpressContext<String, Object>>> delay,
                             List<SaveKpiValue> saveKpis) throws DataComputeException {
        List<DefKpi> defKpis = ruleService.getDefKpis(defClass.getDclassId());
        if (defKpis == null) {
            throw new RuntimeException("can't find defKpi by DefClass " + defClass.getDclassId());
        }
        List<ComputeValue> delayKpis = Lists.newArrayList();
        Map<String, Integer> visitedKpis = Maps.newHashMap();
        for (DefKpi defKpi : defKpis) {
            handleKpi(defKpi.getDkpiId(), region, startdate, vclassId, result, delay, delayKpis, visitedKpis, saveKpis);
        }

        if (delayKpis.size() != 0) {
            for (ComputeValue computeValue : delayKpis) {
                Integer site = visitedKpis.get(computeValue.getPid());
                if (site != null) {
                    result.add(site + 1, computeValue);
                    visitedKpis.put(computeValue.getPid(), site + 1);
                }
            }
        }
    }

    private void handleKpi(String defKpiId, Integer region, Date startdate, String vclassId, List<ComputeValue> result,
                           Map<String, Triple<String, List<String>, IExpressContext<String, Object>>> delay,
                           List<ComputeValue> delayKpis, Map<String, Integer> visitedKpis,
                           List<SaveKpiValue> saveKpis) throws DataComputeException {
        DefFormula defFormula = ruleService.getFormulaByKpiId(defKpiId);
        DefKpi defKpi = ruleService.getDefKpi(defKpiId);
        if (defFormula == null) {
            throw new DataComputeException("不能获取指标 " + defKpi.getDkpiName() + " 的计算公式");
        }

        visitedKpis.put(defKpiId, result.size());
        if (defKpi.getComplex() == 1) {
            dataComputeService.saveKpiValue(defKpi.getDkpiId(), vclassId, region, startdate, 0f);
            result.add(new ComputeValue(defKpi.getDkpiId(), defKpi.getDkpiName(), defKpi.getDclassId(), 0));
            return;
        }

        String[] inidataIds = defFormula.getInidatas().split(",");
        List<String> iniIds = Lists.newArrayList();
        List<String> kpiIds = Lists.newArrayList();
        for (String id : inidataIds) {
            if (id.startsWith(AppConstants.inidata_prefix)) {
                iniIds.add(id.substring(AppConstants.inidata_prefix.length()));
            } else if (id.startsWith(AppConstants.kpi_prefix)) {
                kpiIds.add(id.substring(AppConstants.kpi_prefix.length()));
            }
        }
        IExpressContext<String, Object> expressContext = new DefaultContext<>();
        doIniData(iniIds, region, startdate, expressContext);
        List<String> dependency = Lists.newArrayList();
        doKpi(kpiIds, startdate, dependency, expressContext);

        if (dependency.size() != 0) {
            delay.put(defKpiId, new ImmutableTriple<>(vclassId, dependency, expressContext));
            return;
        }
        try {
            SaveKpiValue saveKpiValue = doCompute(defKpi, defFormula, vclassId, expressContext);
            saveKpis.add(saveKpiValue);
            if (StrKit.isBlank(defKpi.getPid())) {
                result.add(new ComputeValue(defKpi.getDkpiId(), defKpi.getDkpiName(), defKpi.getDclassId(),
                        saveKpiValue.getValue()));
            } else {
                ComputeValue computeValue = new ComputeValue(defKpi.getDkpiId(), defKpi.getDkpiName(),
                        defKpi.getPid(), saveKpiValue.getValue());
                if (visitedKpis.containsKey(defKpi.getPid())) {
                    Integer site = visitedKpis.get(defKpi.getPid());
                    if (site != null) {
                        result.add(site + 1, computeValue);
                        visitedKpis.put(defKpi.getPid(), site + 1);
                    }
                } else {
                    delayKpis.add(computeValue);
                }
            }
        } catch (Throwable e) {
            logger.error("{}", e);
            throw new DataComputeException(e);
        }
    }

    private SaveKpiValue doCompute(DefKpi defKpi, DefFormula defFormula, String vclassId,
                                   IExpressContext<String, Object> expressContext) throws Throwable {
        List<String> errorInfo = new ArrayList<>();
        Float result = (Float) runner.execute(handleRule(defFormula), expressContext, errorInfo, true, false);
        if (result.isNaN()) {
            Set<String> inidatas = getFormulaName(defFormula.getInidatas());
            throw new DataComputeException("指标" + defKpi.getDkpiName() + "的值无法计算，" + inidatas + "输入有误");
        }
        StringBuilder resultStr = new StringBuilder();
        for (int i = 0; i < errorInfo.size(); i++) {
            if (i > 0) {
                resultStr.append(",");
            }
            resultStr.append(errorInfo.get(i));
        }
        if (resultStr.length() != 0) {
            throw new Exception(resultStr.toString());
        }


        return new SaveKpiValue(defKpi.getDkpiId(), vclassId, result);
    }

    private Set<String> getFormulaName(String inidatas) {
        String[] ids = inidatas.split(",");
        Set<String> result = Sets.newHashSetWithExpectedSize(ids.length);
        for (String id : ids) {
            int dot = id.indexOf(".");
            String prefix = id.substring(0, dot + 1);
            String realId = id.substring(dot + 1);
            if (AppConstants.inidata_prefix.equals(prefix)) {
                DefInidata defInidata = iniDataService.getDefInidata(realId);
                if (defInidata != null) {
                    result.add(defInidata.getDiniDataLabel());
                }
            } else if (AppConstants.kpi_prefix.equals(prefix)) {
                DefKpi defKpi = ruleService.getDefKpi(realId);
                if (defKpi != null) {
                    result.add(defKpi.getDkpiName());
                }
            }
        }
        return result;
    }

    private String handleRule(DefFormula defFormula) {

        String rule = defFormula.getCalRule();
        String inidatas = defFormula.getInidatas();
        String[] ids = inidatas.split(",");
        for (String id : ids) {
            int dot = id.indexOf(".");
            String prefix = id.substring(0, dot + 1);
            String realId = id.substring(dot + 1);
            if (AppConstants.inidata_prefix.equals(prefix)) {
                DefInidata defInidata = iniDataService.getDefInidata(realId);
                if (defInidata != null) {
                    rule = rule.replaceAll(id, defInidata.getDiniDataName());
                }
            } else if (AppConstants.kpi_prefix.equals(prefix)) {
                DefKpi defKpi = ruleService.getDefKpi(realId);
                if (defKpi != null) {
                    rule = rule.replaceAll(id, formatComputeParam(defKpi.getDkpiName()));
                }
            }
        }
        return rule;
    }

    private void doKpi(List<String> kpiIds, Date startdate,
                       List<String> dependency, IExpressContext<String, Object> expressContext) {
        if (kpiIds.size() == 0) {
            return;
        }
        for (String kpiId : kpiIds) {
            Float value = dataComputeService.getKpiValue(kpiId, startdate);
            if (value < 0) {
                dependency.add(kpiId);
            } else {
                DefKpi defKpi = ruleService.getDefKpi(kpiId);
                expressContext.put(formatComputeParam(defKpi.getDkpiName()), value);
            }
        }
    }

    private void doIniData(List<String> iniIds, Integer region, Date startdate, IExpressContext<String, Object> expressContext) {
        if (iniIds.size() == 0) {
            return;
        }
        List<DefInidata> defInidatas = iniDataService.getDefInidatasByIds(iniIds);
        ValueInidata valueInidata;

        for (DefInidata defInidata : defInidatas) {
            if (defInidata.getIsStaticMark() == 0) {
                valueInidata = dataInputService.getValue(region, startdate, defInidata.getDiniDataId(),
                        defInidata.getSubmitOrgRole(), AppConstants.Value_Inidata_Save_Status, AppConstants.Value_Inidata_Back_Status);
            } else {
                valueInidata = dataInputService.getStatisticValue(region, startdate,
                        defInidata.getDiniDataId(), defInidata.getSubmitOrgRole());
            }
            expressContext.put(formatComputeParam(defInidata.getDiniDataName()), valueInidata.getViniDataValue());
        }
    }

    private String formatComputeParam(String param) {
        int index = param.indexOf("、");
        if (index > 0) {
            param = param.substring(index + 1);
        }
        return param;
    }
}