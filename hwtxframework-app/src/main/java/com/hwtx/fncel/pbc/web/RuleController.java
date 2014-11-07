package com.hwtx.fncel.pbc.web;

import com.hwtx.fncel.pbc.entity.DefCat;
import com.hwtx.fncel.pbc.entity.DefClass;
import com.hwtx.fncel.pbc.entity.DefFormula;
import com.hwtx.fncel.pbc.entity.DefKpi;
import com.hwtx.fncel.pbc.exception.ExistException;
import com.hwtx.fncel.pbc.service.IniDataService;
import com.hwtx.fncel.pbc.service.RuleService;
import com.hwtx.fncel.pbc.util.AppConstants;
import com.hwtx.framework.interceptor.ModuleIocInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.thinkgem.jeesite.common.Constants;
import com.thinkgem.jeesite.common.web.BaseController;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;

@Before(ModuleIocInterceptor.class)
@ControllerBind(controllerKey = "${adminPath}/app/rule")
public class RuleController extends BaseController {

    @Resource
    private RuleService ruleService;
    @Resource
    private IniDataService iniDataService;

    @ActionKey(value = {"list"})
    public void list() {
        render("/app/rule/ruleList.jsp");
    }

    @ActionKey(value = {"showAddCat"})
    public void showAddCat() {
        render("/app/rule/catForm.jsp");
    }

    @ActionKey(value = {"showAddClass"})
    public void showAddClass() {
        DefClass defClass = new DefClass();
        defClass.setDcatId(getPara("id"));
        setAttr("defClass", defClass);
        render("/app/rule/classForm.jsp");
    }

    @ActionKey(value = {"showAddKpi"})
    public void showAddKpi() {
        DefKpi defKpi = new DefKpi();
        String complex = getPara("complex");
        String id = getPara("id");
        setAttr("id", id);
        if ("1".equals(complex)) {
            defKpi.setPid(id);
            DefKpi findIt = ruleService.getDefKpi(id);
            id = findIt.getDclassId();
        }
        defKpi.setDclassId(id);
        setAttr("defKpi", defKpi);
        render("/app/rule/kpiForm.jsp");
    }

    @ActionKey(value = {"treeData"})
    public void treeData() {
        String id = getPara("id");
        String nodeType = getPara("nodeType");
        String jsonTree = ruleService.getRuleTree(id, nodeType);
        renderJson(jsonTree);
    }

    @ActionKey(value = {"saveCat"})
    public void saveCat() throws ExistException {
        DefCat defCat = getModel(DefCat.class);
        try {
            ruleService.updateDefCat(defCat);
        } catch (ExistException e) {
            renderJson(getRenderJson(Constants.RENDER_JSON_ERROR, "分类已经存在"));
            throw e;
        } catch (Exception e) {
            renderJson(getRenderJson(Constants.RENDER_JSON_ERROR, e.getMessage()));
            throw e;
        }
        renderJson(getRenderJson(Constants.RENDER_JSON_SUCCESS, "保存成功"));
    }

    @ActionKey(value = {"showEditCat"})
    public void showEditCat() {
        String id = getPara("id");
        setAttr("defCat", ruleService.getDefCat(id));
        render("/app/rule/catForm.jsp");
    }

    @ActionKey(value = {"saveClass"})
    public void saveClass() throws ExistException {
        DefClass defClass = getModel(DefClass.class);
        try {
            ruleService.updateDefClass(defClass);
        } catch (ExistException e) {
            renderJson(getRenderJson(Constants.RENDER_JSON_ERROR, "项目已经存在"));
            throw e;
        } catch (Exception e) {
            renderJson(getRenderJson(Constants.RENDER_JSON_ERROR, e.getMessage()));
            throw e;
        }
        renderJson(getRenderJson(Constants.RENDER_JSON_SUCCESS, "保存成功"));
    }

    @ActionKey(value = {"showEditClass"})
    public void showEditClass() {
        String id = getPara("id");
        setAttr("defClass", ruleService.getDefClass(id));
        render("/app/rule/classForm.jsp");
    }

    @ActionKey(value = {"saveKpi"})
    public void saveKpi() throws ExistException {
        DefKpi defKpi = getModel(DefKpi.class);
        try {
            String comparisonOp = getPara("comparisonOp");
            String comparisonText = getPara("comparisonText");
            if (StrKit.notBlank(comparisonOp, comparisonText)) {
                if (!StringUtils.isNumeric(comparisonText)) {
                    comparisonText = "'" + comparisonText + "'";
                }
                defKpi.setDkpiExtendExp(comparisonOp + " " + comparisonText);
            }
            ruleService.updateDefKpi(defKpi);
        } catch (ExistException e) {
            renderJson(getRenderJson(Constants.RENDER_JSON_ERROR, "指标已经存在"));
            throw e;
        } catch (Exception e) {
            renderJson(getRenderJson(Constants.RENDER_JSON_ERROR, e.getMessage()));
            throw e;
        }
        renderJson(getRenderJson(Constants.RENDER_JSON_SUCCESS, "保存成功"));
    }

    @ActionKey(value = {"showEditKpi"})
    public void showEditKpi() {
        String id = getPara("id");
        setAttr("id", "#");
        DefKpi defKpi = ruleService.getDefKpi(id);
        String expr = defKpi.geDkpiExtendExp();
        if (StrKit.notBlank(expr)) {
            String[] exp = expr.split(" ");
            defKpi.setComparisonOp(exp[0]);
            String text = exp[1];
            if (text.contains("'")) {
                text = text.substring(1, text.length() - 1);
            }
            defKpi.setComparisonText(text);
        }
        setAttr("defKpi", defKpi);
        render("/app/rule/kpiForm.jsp");
    }

    @ActionKey(value = {"formula"})
    public void formula() {
        setAttr("inidata", iniDataService.getIniDataNameAndLabels());
        String kpiId = getPara("id");
        setAttr("defFormula", ruleService.getFormula(kpiId));
        setAttr("kpiId", kpiId);
        setAttr("defClasses", ruleService.getDefClasses());
        setAttr("inidata_prefix", AppConstants.inidata_prefix);
        setAttr("kpi_prefix", AppConstants.kpi_prefix);
        render("/app/rule/formula.jsp");
    }

    @ActionKey(value = {"saveFormula"})
    public void saveFormula() {
        DefFormula defFormula = getModel(DefFormula.class);
        String kpiId = getPara("kpiId");
        ruleService.saveFormula(kpiId, defFormula);
        renderJson(getRenderJson(Constants.RENDER_JSON_SUCCESS, "保存成功"));
    }
}