package com.hwtx.fncel.pbc.dao;

import com.hwtx.fncel.pbc.entity.DefCat;
import com.hwtx.fncel.pbc.entity.DefClass;
import com.hwtx.fncel.pbc.entity.DefFormula;
import com.hwtx.fncel.pbc.entity.DefKpi;
import com.hwtx.fncel.pbc.util.AppConstants;
import com.hwtxframework.ioc.annotation.Component;
import com.jfinal.ext.plugin.sqlinxml.SqlKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.thinkgem.jeesite.common.persistence.AbstractBaseDao;

import java.util.List;

@Component
public class RuleDao extends AbstractBaseDao {

    public List<DefCat> getDefCats() {
        return DefCat.dao.find(SqlKit.sql(AppConstants.rule_getDefCats));
    }

    public List<DefClass> getDefClasses() {
        return DefClass.dao.find(SqlKit.sql(AppConstants.rule_getDefClasses));
    }

    public List<DefKpi> getDefKpis() {
        return DefKpi.dao.find(SqlKit.sql(AppConstants.rule_getDefKpis));
    }

    public DefCat getDefCatByName(String dcatName) {
        return DefCat.dao.findFirst(SqlKit.sql(AppConstants.rule_getDefCatByName), dcatName);
    }

    public DefClass getDefClassByName(String dclassName) {
        return DefClass.dao.findFirst(SqlKit.sql(AppConstants.rule_getDefClassByName), dclassName);
    }

    public DefKpi getDefKpiByName(String dkpiName) {
        return DefKpi.dao.findFirst(SqlKit.sql(AppConstants.rule_getDefKpiByName), dkpiName);
    }

    public void saveFormula(DefFormula defFormula) {
        save(defFormula);
    }

    public void makeKpiAndFormula(String kpiId, String formulaId) {
        DefKpi.dao.setDkpiCalRuleId(formulaId).setDkpiId(kpiId).update();
    }

    public DefFormula getFormulaByKpiId(String kpiId) {
        return DefFormula.dao.findFirst(SqlKit.sql(AppConstants.rule_getFormulaByKpiId), kpiId);
    }

    public Record getFormulaInidata(String kpiId) {
        return Db.findFirst(SqlKit.sql(AppConstants.rule_getFormulaInidata), kpiId);
    }
}
