package com.hwtx.fncel.pbc.dao;

import com.google.common.collect.Lists;
import com.hwtx.fncel.pbc.entity.AppDataCheck;
import com.hwtx.fncel.pbc.entity.ValueInidata;
import com.hwtx.fncel.pbc.util.AppConstants;
import com.hwtx.fncel.pbc.vo.InputDataVo;
import com.hwtxframework.ioc.annotation.Component;
import com.jfinal.ext.plugin.sqlinxml.SqlKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page1;
import com.jfinal.plugin.activerecord.Record;
import com.thinkgem.jeesite.common.persistence.CollectBuilder;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by panye on 2014/9/30.
 */
@Component
public class DataInputDao {

    public Long countValueInidata(String type, Integer region, Date date) {
        return Db.queryLong(SqlKit.sql(AppConstants.value_countValueInidata), date, region, type);
    }

    public void saveValueInidata(Object[][] params) {
        Db.batch(SqlKit.sql(AppConstants.value_saveValueInidata), params, params.length);
    }

    public void saveValueInidatastatics(Object[][] params) {
        Db.batch(SqlKit.sql(AppConstants.value_saveValueInidatastatics), params, params.length);
    }

    public int[] deleteValueInidatastatics(Date date, Integer region, String orgId) {
        Object[][] param = new Object[][]{{date, region, orgId}};
        return Db.batch(SqlKit.sql(AppConstants.value_deleteValueInidatastatics), param, param.length);
    }

    public int[] deleteValueInidata(Date date, String submitRole, Integer region) {
        Object[][] param = new Object[][]{{date, submitRole, region}};
        return Db.batch(SqlKit.sql(AppConstants.value_deleteValueInidata), param, param.length);
    }

    public List<InputDataVo> getTableData(String type, Integer region, String submitOrg, boolean statistic) {
        List<Record> records = Lists.newArrayList();
        if (statistic) {
            records = Db.find(SqlKit.sql(AppConstants.value_getStatisticTableData), type, region, submitOrg);
        } else {
            records = Db.find(SqlKit.sql(AppConstants.value_getTableData), type, region);
        }
        List<InputDataVo> result = Lists.newArrayList();
        for (int i = 0; i < records.size(); i++) {
            InputDataVo inputDataVo = new InputDataVo();
            Integer status = records.get(i).getInt("status");
            if (status == null) {
                status = AppConstants.Value_Inidata_Save_Status;
            }
            inputDataVo.setStatus(status);
            inputDataVo.setYear(records.get(i).getDate("date"));
            inputDataVo.setNum(i + 1);
            result.add(inputDataVo);
        }
        return result;
    }

    public ValueInidata getValue(String diniDataId, Date date, Integer region, String submitRole, Integer... status) {
        CollectBuilder collectBuilder = new CollectBuilder();
        collectBuilder.in("viniStatus", Arrays.asList(status));
        return ValueInidata.dao.findFirst(SqlKit.sql(AppConstants.value_getValue) + collectBuilder.build(true),
                diniDataId, date, region, submitRole);
    }

    public Page1<Record> getFinancialInputDatas(String type, Integer region, Integer status, int offset, int count) {
        return Db.paginateOverOffset(offset, count, SqlKit.sqlSelect(AppConstants
                .value_getFinancialInputDatas).getExpress(), SqlKit.sqlExceptSelect(AppConstants
                .value_getFinancialInputDatas).getExpress(), type, region, status);
    }

    public void saveAppDataCheck(AppDataCheck appDataCheck) {
        appDataCheck.save();
    }

    public List<ValueInidata> getValues(Date date, Integer region, String type) {
        return ValueInidata.dao.find(SqlKit.sql(AppConstants.value_getValues), date, region, type);
    }

    public Long countValueInidatastatics(Date date, Integer region, String submitRole) {
        return Db.queryLong(SqlKit.sql(AppConstants.value_countValueInidatastatics), date, region, submitRole);
    }

    public String getBackData(String orgId, Date date, Integer region, Integer checkType) {
        return Db.queryStr(SqlKit.sql(AppConstants.value_getBackData), orgId, date, region, checkType);
    }

    public long countStatisticValueInidata(String type, Integer region, Date date, String orgId) {
        return Db.queryLong(SqlKit.sql(AppConstants.value_countStatisticValueInidata), date, region, type, orgId);
    }

    public List<Record> getDefValueInidatas(String submitRole, Integer region, Date date, Integer... status) {
        CollectBuilder collectBuilder = new CollectBuilder();
        collectBuilder.in("viniStatus", Arrays.asList(status));
        collectBuilder.orderBy("diniDataLabel");
        return Db.find(SqlKit.sql(AppConstants.value_getDefValueInidatas) + collectBuilder.build(true), date, submitRole, region);
    }

    public List<Record> getDefStatisticValueInidatas(String submitRole, Integer region, Date date,
                                                     String orgId, Integer... status) {
        CollectBuilder collectBuilder = new CollectBuilder();
        collectBuilder.in("siniStatus", Arrays.asList(status));
        collectBuilder.orderBy("diniDataLabel");
        return Db.find(SqlKit.sql(AppConstants.value_getDefStatisticValueInidatas) + collectBuilder.build(true), date, status,
                submitRole, region, orgId);
    }

    public Double mergeStatisticData(Integer region, Date date, String diniDataId, String submitOrgRole,
                                     Integer... status) {
        CollectBuilder collectBuilder = new CollectBuilder();
        collectBuilder.in("siniStatus", Arrays.asList(status));
        return Db.queryDouble(SqlKit.sql(AppConstants.value_mergeStatisticData) + collectBuilder.build(true), region,
                date, diniDataId, submitOrgRole);
    }

    public void updateInidataDataValue(Integer region, Date date, String diniDataId, String submitOrgRole, float value) {
        Db.update(SqlKit.sql(AppConstants.value_updateInidataDataValue), value, region, date, diniDataId, submitOrgRole);
    }

    public ValueInidata getStatisticValue(String diniDataId, Date date, Integer region, String submitOrgRole) {
        return ValueInidata.dao.findFirst(SqlKit.sql(AppConstants.value_getStatisticValue), diniDataId,
                date, region, submitOrgRole);
    }

    public void updateValueIinidataStatus(Integer region, Date date, Integer status) {
        Object[][] params = new Object[][]{{status, date, region}};
        Db.batch(SqlKit.sql(AppConstants.value_updateValueIinidataStatus), params, params.length);
    }

    public void updateValueIinidataStatisticStatus(Integer region, Date date, Integer status) {
        Object[][] params = new Object[][]{{status, date, region}};
        Db.batch(SqlKit.sql(AppConstants.value_updateValueIinidataStatisticStatus), params, params.length);
    }

    public void updateValueStatusBySubmitRole(String type, Date date, Integer region, Integer status) {
        Db.update(SqlKit.sql(AppConstants.value_updateValueStatusBySubmitRole), status, type, region, date);
    }

    public void updateStatisticStatusBySubmitOrg(String type, Date date, Integer region, String orgId, Integer status) {
        Db.update(SqlKit.sql(AppConstants.value_updateStatisticStatusBySubmitOrg), status, region, date, type, region, date, orgId);
    }

    public List<Record> getValueCat(Integer region, Date date, Integer... status) {
        String sql = SqlKit.sql(AppConstants.value_getValueCat);
        CollectBuilder collectBuilder = new CollectBuilder();
        collectBuilder.in("vcatStatus", Arrays.asList(status));
        collectBuilder.orderBy("dcatSquence");
        return Db.find(sql + collectBuilder.build(true), date, region);
    }

    public List<Record> getValueClass(Integer region, Date date, String vcatId, Integer... status) {
        String sql = SqlKit.sql(AppConstants.value_getValueClass);
        CollectBuilder collectBuilder = new CollectBuilder();
        collectBuilder.in("vclassStatus", Arrays.asList(status));
        collectBuilder.orderBy("dclassSquence");
        return Db.find(sql + collectBuilder.build(true), date, region, vcatId);
    }

    public List<Record> getValueKpi(String vclassId, Integer region, Date date, Integer... status) {
        String sql = SqlKit.sql(AppConstants.value_getValueKpi);
        CollectBuilder collectBuilder = new CollectBuilder();
        collectBuilder.in("vkpiStatus", Arrays.asList(status));
        collectBuilder.orderBy("dkpiSquence");
        return Db.find(sql + collectBuilder.build(true), date, region, vclassId);
    }

    public List<Record> getValueInidataDetail(Integer region, Date date, List<String> iniIds, Integer... status) {
        String sql = SqlKit.sql(AppConstants.valueSubmit_getValueInidataDetail);
        CollectBuilder collectBuilder = new CollectBuilder();
        collectBuilder.in("viniStatus", Arrays.asList(status));
        collectBuilder.in("b.diniDataId", iniIds);
        return Db.find(sql + collectBuilder.build(true), date, region);
    }

    public List<Record> getStatisticValueInidatasDatail(Integer region, Date date, List<String> iniIds, Integer[] status) {
        String sql = SqlKit.sql(AppConstants.valueSubmit_getStatisticValueInidatasDatail);
        CollectBuilder collectBuilder = new CollectBuilder();
        collectBuilder.in("siniStatus", Arrays.asList(status));
        collectBuilder.in("b.diniDataId", iniIds);
        return Db.find(sql + collectBuilder.build(true), date, region);
    }

    public void handleKpiValueStatus(Date date, Integer region, Integer status, Date handleTime) {
        Object[][] params = new Object[][]{{status, handleTime, date, region}};
        Db.batch(SqlKit.sql(AppConstants.value_handleKpiValueStatus), params, params.length);
    }

    public void handleClassValueStatus(Date date, Integer region, Integer status, Date handleTime) {
        Object[][] params = new Object[][]{{status, handleTime, date, region}};
        Db.batch(SqlKit.sql(AppConstants.value_handleClassValueStatus), params, params.length);
    }

    public void handleCatValueStatus(Date date, Integer region, Integer status, Date handleTime) {
        Object[][] params = new Object[][]{{status, handleTime, date, region}};
        Db.batch(SqlKit.sql(AppConstants.value_handleCatValueStatus), params, params.length);
    }

    public float getMaxKpiValueOfRegions(Date date, String dkpiId, List<Integer> regions, Integer status) {
        CollectBuilder collectBuilder = new CollectBuilder();
        collectBuilder.in("vkpiRegion", regions);
        return Db.queryFloat(SqlKit.sql(AppConstants.value_getMaxKpiValueOfRegions) + collectBuilder.build(true), date,
                dkpiId, status);
    }

    public float getMinKpiValueOfRegions(Date date, String dkpiId, List<Integer> regions, Integer status) {
        CollectBuilder collectBuilder = new CollectBuilder();
        collectBuilder.in("vkpiRegion", regions);
        return Db.queryFloat(SqlKit.sql(AppConstants.value_getMinKpiValueOfRegions) + collectBuilder.build(true), date,
                dkpiId, status);
    }

    public void updateKpisScore(Object[][] params) {
        Db.batch(SqlKit.sql(AppConstants.value_updateKpisScore), params, params.length);
    }

    public void updateClassScore(Object[][] params) {
        Db.batch(SqlKit.sql(AppConstants.value_updateClassScore), params, params.length);
    }

    public void updateCatScore(Object[][] params) {
        Db.batch(SqlKit.sql(AppConstants.value_updateCatScore), params, params.length);
    }
}
