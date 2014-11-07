package com.hwtx.fncel.pbc.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hwtx.fncel.pbc.dao.RuleDao;
import com.hwtx.fncel.pbc.entity.*;
import com.hwtx.fncel.pbc.exception.ExistException;
import com.hwtx.fncel.pbc.util.AppConstants;
import com.hwtx.fncel.pbc.vo.DefClassVo;
import com.hwtxframework.ioc.annotation.Component;
import com.hwtxframework.ioc.annotation.Dependon;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;
import com.thinkgem.jeesite.common.Constants;
import com.thinkgem.jeesite.common.utils.IdGen;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by panye on 2014/9/10.
 */
@Component
@Dependon("jFinalCache")
public class RuleService {

    public static final String TOPRULEID = "-1";
    public static final String TOPRULENAME = "指标体系";
    public static final String DEFCAT_TYPE = "defCat";
    public static final String DEFCLASS_TYPE = "defClass";
    public static final String DEFKPI_TYPE = "defKpi";

    private Map<String, DefCat> defCatMapping = Maps.newLinkedHashMap();
    private Map<String, List<DefClass>> catClassMapping = Maps.newLinkedHashMap();
    private Map<String, List<DefKpi>> classKpiMapping = Maps.newLinkedHashMap();
    private Map<String, DefKpi> defKpiMapping = Maps.newHashMap();
    private ReadWriteLock defCatReadWriteLock = new ReentrantReadWriteLock();
    private Lock defCatReadLock = defCatReadWriteLock.readLock();
    private Lock defCatWriteLook = defCatReadWriteLock.writeLock();

    private ReadWriteLock defClassReadWriteLock = new ReentrantReadWriteLock();
    private Lock defClassReadLock = defClassReadWriteLock.readLock();
    private Lock defClassWriteLook = defClassReadWriteLock.writeLock();

    private ReadWriteLock defKpiReadWriteLock = new ReentrantReadWriteLock();
    private Lock defKpiReadLock = defKpiReadWriteLock.readLock();
    private Lock defKpiWriteLook = defKpiReadWriteLock.writeLock();


    public Map<String, DefCat> getDefCats() {
        return defCatMapping;
    }

    public List<DefClass> getDefClasses(String defCatId) {
        return catClassMapping.get(defCatId);
    }

    public List<DefClassVo> getDefClasses() {
        List<DefClassVo> result = Lists.newArrayList();
        for (DefCat defCat : getDefCats().values()) {
            for (DefClass defClass : getDefClasses(defCat.getDcatId())) {
                result.add(new DefClassVo(defClass, getDefKpis(defClass.getDclassId())));
            }
        }
        return result;
    }

    public List<DefKpi> getDefKpis(String defClassId) {
        return classKpiMapping.get(defClassId);
    }

    public int getDefKpiCount() {
        return defKpiMapping.size();
    }

    @PostConstruct
    public void init() {
        handleDefCat();
        handleDefClass();
        handleDefKpi();
    }

    private void handleDefCat() {
        List<DefCat> defCats = ruleDao.getDefCats();
        try {
            defCatWriteLook.lock();
            defCatMapping.clear();
            for (DefCat defCat : defCats) {
                defCatMapping.put(defCat.getDcatId(), defCat);
            }
        } finally {
            defCatWriteLook.unlock();
        }
    }

    private void handleDefClass() {
        List<DefClass> defClasses = ruleDao.getDefClasses();
        try {
            defClassWriteLook.lock();
            catClassMapping.clear();
            for (DefClass defClass : defClasses) {
                List<DefClass> list = catClassMapping.get(defClass.getDcatId());
                if (list == null) {
                    list = Lists.newArrayList();
                }
                list.add(defClass);
                catClassMapping.put(defClass.getDcatId(), list);
            }
        } finally {
            defClassWriteLook.unlock();
        }
    }

    private void handleDefKpi() {
        List<DefKpi> defKpis = ruleDao.getDefKpis();
        try {
            defKpiWriteLook.lock();
            classKpiMapping.clear();
            for (DefKpi defKpi : defKpis) {
                List<DefKpi> list = classKpiMapping.get(defKpi.getDclassId());
                if (list == null) {
                    list = Lists.newArrayList();
                }
                list.add(defKpi);
                classKpiMapping.put(defKpi.getDclassId(), list);
                defKpiMapping.put(defKpi.getDkpiId(), defKpi);
            }
        } finally {
            defKpiWriteLook.unlock();
        }
    }

    @Resource
    private RuleDao ruleDao;

    public String getRuleTree(String id, String nodeType) {

        List<Map<String, Object>> mapList = Lists.newArrayList();
        if (nodeType.equals(Constants.INVALIDPARENT)) {
            renderWholeRuleTree(mapList);
        } else {
            renderRuleTree(id, nodeType, mapList);
        }
        return JSON.toJSONString(mapList);
    }

    private void renderRuleTree(String id, String nodeType, List<Map<String, Object>> mapList) {
        if (DEFCAT_TYPE.equals(nodeType)) {
            DefCat defCat = defCatMapping.get(id);
            if (defCat != null) {
                fillDefCatNode(defCat, mapList);
            }
        } else if (DEFCLASS_TYPE.equals(nodeType)) {
            String catId = null;
            DefClass defClass = null;
            boolean find = false;
            for (Map.Entry<String, List<DefClass>> entry : catClassMapping.entrySet()) {
                if (find) {
                    break;
                }
                for (DefClass cls : entry.getValue()) {
                    if (cls.getDclassId().equals(id)) {
                        catId = entry.getKey();
                        defClass = cls;
                        find = true;
                        break;
                    }
                }
            }
            if (find) {
                fillDefClassNode(catId, defClass, mapList);
            }
        } else if (DEFKPI_TYPE.equals(nodeType)) {
            String classId = null;
            DefKpi defKpi = null;
            boolean find = false;
            for (Map.Entry<String, List<DefKpi>> entry : classKpiMapping.entrySet()) {
                if (find) {
                    break;
                }
                for (DefKpi kpi : entry.getValue()) {
                    if (kpi.getDkpiId().equals(id)) {
                        classId = entry.getKey();
                        defKpi = kpi;
                        find = true;
                        break;
                    }
                }
            }
            if (find) {
                if (defKpi.getComplex() == 1) {
                    for (DefKpi kpi : defKpiMapping.values()) {
                        if (id.equals(kpi.getPid())) {
                            fillDefKpiNode(kpi.getDclassId(), kpi, mapList);
                        }
                    }
                }
            }
        }
    }

    private void renderWholeRuleTree(List<Map<String, Object>> mapList) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("id", TOPRULEID);
        map.put("parent", "#");
        map.put("text", TOPRULENAME);
        map.put("state", getOpenState());
        mapList.add(map);

        try {
            defCatReadLock.lock();
            for (DefCat defCat : defCatMapping.values()) {
                fillDefCatNode(defCat, mapList);
            }
        } finally {
            defCatReadLock.unlock();
        }
    }

    private void fillDefCatNode(DefCat defCat, List<Map<String, Object>> mapList) {
        Map<String, Object> map = Maps.newHashMap();
        map = Maps.newHashMap();
        map.put("id", defCat.getDcatId());
        map.put("parent", TOPRULEID);
        map.put("text", defCat.getDcatName());
        map.put("state", getOpenState());
        map.put("li_attr", getNodeAttr(DEFCAT_TYPE, defCat.getDcatSquence(), 0));
        map.put("icon", "fa fa-lg fa-fw fa-th");
        mapList.add(map);
        fillDefClassNodes(defCat.getDcatId(), mapList);
    }

    private void fillDefClassNodes(String catId, List<Map<String, Object>> mapList) {
        try {
            defClassReadLock.lock();
            List<DefClass> defClasses = catClassMapping.get(catId);
            if (defClasses == null) {
                return;
            }
            for (DefClass defClass : defClasses) {
                fillDefClassNode(catId, defClass, mapList);
            }
        } finally {
            defClassReadLock.unlock();
        }
    }

    private void fillDefClassNode(String catId, DefClass defClass, List<Map<String, Object>> mapList) {
        Map<String, Object> map = Maps.newHashMap();
        map = Maps.newHashMap();
        map.put("id", defClass.getDclassId());
        map.put("parent", catId);
        map.put("text", defClass.getDclassName());
        map.put("state", getOpenState());
        map.put("li_attr", getNodeAttr(DEFCLASS_TYPE, defClass.getDclassSquence(), 0));
        map.put("icon", "fa fa-lg fa-fw fa-hand-o-right");
        mapList.add(map);
        fillDefKpiNodes(defClass.getDclassId(), mapList);
    }

    private void fillDefKpiNodes(String classId, List<Map<String, Object>> mapList) {

        try {
            defKpiReadLock.lock();
            List<DefKpi> defKpis = classKpiMapping.get(classId);
            if (defKpis == null) {
                return;
            }

            for (DefKpi defKpi : defKpis) {
                fillDefKpiNode(classId, defKpi, mapList);
            }
        } finally {
            defKpiReadLock.unlock();
        }
    }

    private void fillDefKpiNode(String classId, DefKpi defKpi, List<Map<String, Object>> mapList) {

        Map<String, Object> map = Maps.newHashMap();
        map = Maps.newHashMap();
        map.put("id", defKpi.getDkpiId());
        if (StrKit.isBlank(defKpi.getPid())) {
            map.put("parent", classId);
        } else {
            map.put("parent", defKpi.getPid());
        }
        map.put("text", defKpi.getDkpiName());
        map.put("state", getOpenState());
        map.put("li_attr", getNodeAttr(DEFKPI_TYPE, defKpi.getDkpiSquence(), defKpi.getComplex()));
        map.put("icon", "fa fa-lg fa-fw fa-star-o");
        mapList.add(map);
    }

    private Map<String, Object> getOpenState() {
        Map<String, Object> state = Maps.newHashMap();
        state.put("opened", true);
        return state;
    }

    private Map<String, Object> getNodeAttr(String value, int squence, Integer complex) {
        Map<String, Object> attrs = Maps.newHashMap();
        attrs.put("type", value);
        attrs.put("squence", squence);
        attrs.put("complex", complex);
        return attrs;
    }

    public void updateDefCat(DefCat defCat) throws ExistException {

        DefCat exist = ruleDao.getDefCatByName(defCat.getDcatName());

        if (defCat.getDcatId() == null) {

            if (exist != null) {
                throw new ExistException();
            }

            defCat.prePersist();
            defCat.setDcatId(IdGen.uuid());
            ruleDao.save(defCat);
        } else {

            if (exist != null && !exist.getDcatId().equals(defCat.getDcatId())) {
                throw new ExistException();
            }
            defCat.preUpdate();
            ruleDao.update(defCat);
        }
        handleDefCat();
    }

    public DefCat getDefCat(String id) {
        return defCatMapping.get(id);
    }

    public void updateDefClass(DefClass defClass) throws ExistException {

        DefClass exist = ruleDao.getDefClassByName(defClass.getDclassName());

        if (defClass.getDclassId() == null) {

            if (exist != null) {
                throw new ExistException();
            }

            defClass.prePersist();
            defClass.setDclassId(IdGen.uuid());
            ruleDao.save(defClass);
        } else {
            if (exist != null && !exist.getDclassId().equals(defClass.getDclassId())) {
                throw new ExistException();
            }
            defClass.preUpdate();
            ruleDao.update(defClass);
        }
        handleDefClass();
    }

    public DefClass getDefClass(String id) {
        return (DefClass) ruleDao.findById(DefClass.dao, id);
    }

    public void updateDefKpi(DefKpi defKpi) throws ExistException {

        DefKpi exist = ruleDao.getDefKpiByName(defKpi.getDkpiName());

        if (defKpi.getDkpiId() == null) {

            if (exist != null) {
                throw new ExistException();
            }

            defKpi.prePersist();
            defKpi.setDkpiId(IdGen.uuid());
            ruleDao.save(defKpi);
        } else {
            if (exist != null && !exist.getDkpiId().equals(defKpi.getDkpiId())) {
                throw new ExistException();
            }
            defKpi.preUpdate();
            ruleDao.update(defKpi);
        }
        handleDefKpi();
    }

    public DefKpi getDefKpi(String id) {
        return defKpiMapping.get(id);
    }

    public void saveFormula(String kpiId, DefFormula defFormula) {

        if (defFormula.getCalRuleId() != null) {
            defFormula.preUpdate();
            ruleDao.update(defFormula);
        } else {
            String formulaId = IdGen.uuid();
            defFormula.prePersist();
            defFormula.setCalRuleId(formulaId);
            ruleDao.saveFormula(defFormula);
            ruleDao.makeKpiAndFormula(kpiId, formulaId);
        }
    }

    @Resource
    private IniDataService iniDataService;

    /**
     * @param kpiId
     * @return left non-statistic:defInidataId,right statistic:defInidataId
     */
    public Pair<List<String>, List<String>> getFormulaInidata(String kpiId) {
        Record record = ruleDao.getFormulaInidata(kpiId);
        String inidatas = record.get("inidatas");
        String[] inidataIds = inidatas.split(",");
        List<String> iniIds = Lists.newArrayList();
        for (String id : inidataIds) {
            if (id.startsWith(AppConstants.inidata_prefix)) {
                iniIds.add(id.substring(AppConstants.inidata_prefix.length()));
            }
        }
        List<String> nonStatistic = Lists.newArrayList();
        List<String> statistic = Lists.newArrayList();
        for (DefInidata defInidata : iniDataService.getDefInidatasByIds(iniIds)) {
            if (defInidata.getIsStaticMark() != 1) {
                nonStatistic.add(defInidata.getDiniDataId());
            } else {
                statistic.add(defInidata.getDiniDataId());
            }
        }
        return new ImmutablePair<>(nonStatistic, statistic);
    }

    public DefFormula getFormula(String kpiId) {
        DefFormula defFormula = ruleDao.getFormulaByKpiId(kpiId);
        if (defFormula != null) {
            defFormula.setCalRuleShow(defFormula.getCalRule());
            String inidatas = defFormula.getInidatas();
            String[] inidataIds = inidatas.split(",");
            List<String> iniIds = Lists.newArrayList();
            List<String> kpiIds = Lists.newArrayList();
            for (String id : inidataIds) {
                if (id.startsWith(AppConstants.inidata_prefix)) {
                    iniIds.add(id.substring(AppConstants.inidata_prefix.length()));
                } else if (id.startsWith(AppConstants.kpi_prefix)) {
                    kpiIds.add(id.substring(AppConstants.kpi_prefix.length()));
                }
            }
            handleIniData(iniIds, defFormula);
            handleKpi(kpiIds, defFormula);
        }
        return defFormula;
    }

    private void handleIniData(List<String> iniIds, DefFormula defFormula) {
        if (iniIds.size() == 0) {
            return;
        }
        List<DefInidata> defInidatas = iniDataService.getDefInidatasByIds(iniIds);
        String show = defFormula.getCalRuleShow().replaceAll(AppConstants.inidata_prefix, "");
        for (String id : iniIds) {
            for (DefInidata defInidata : defInidatas) {
                if (id.equals(defInidata.getDiniDataId())) {
                    show = show.replace(id, defInidata.getDiniDataLabel());
                    break;
                }
            }
        }
        defFormula.setCalRuleShow(show);
    }

    private void handleKpi(List<String> kpiIds, DefFormula defFormula) {
        if (kpiIds.size() == 0) {
            return;
        }
        String show = defFormula.getCalRuleShow().replaceAll(AppConstants.kpi_prefix, "");
        for (String id : kpiIds) {
            DefKpi defKpi = getDefKpi(id);
            show = show.replace(id, defKpi.getDkpiName());
        }
        defFormula.setCalRuleShow(show);
    }

    public DefFormula getFormulaByKpiId(String defKpiId) {
        return ruleDao.getFormulaByKpiId(defKpiId);
    }
}
