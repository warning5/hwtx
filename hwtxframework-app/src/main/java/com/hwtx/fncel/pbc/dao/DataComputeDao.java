package com.hwtx.fncel.pbc.dao;

import com.google.common.collect.Lists;
import com.hwtx.fncel.pbc.entity.ValueCat;
import com.hwtx.fncel.pbc.entity.ValueClass;
import com.hwtx.fncel.pbc.entity.ValueKpi;
import com.hwtx.fncel.pbc.entity.ValueSynthetical;
import com.hwtx.fncel.pbc.util.AppConstants;
import com.hwtx.fncel.pbc.vo.SubmitKpiVo;
import com.hwtxframework.ioc.annotation.Component;
import com.jfinal.ext.plugin.sqlinxml.SqlKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page1;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.TableMapping;
import com.thinkgem.jeesite.common.persistence.CollectBuilder;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by panye on 2014/10/7.
 */
@Component
public class DataComputeDao {
    public void saveKpiValue(ValueKpi valueKpi) {
        valueKpi.save();
    }

    public Float getKpiValue(String kpiId, Date date) {
        ValueKpi valueKpi = ValueKpi.dao.findFirst("select vkpiValue from value_kpi where dkpiId = ? and vkpiDate = ?"
                , kpiId, date);
        if (valueKpi != null) {
            return valueKpi.getVkpiValue();
        }
        return -1f;
    }

    public void saveCatValue(ValueCat valueCat) {
        valueCat.save();
    }

    public void saveClassValue(ValueClass valueClass) {
        valueClass.save();
    }

    public Page1<SubmitKpiVo> getSubmitKpiData(int offset, int count, Integer region, Integer excludeStatus) {
        Page1<Record> page1 = Db.paginateOverOffset(offset, count, SqlKit.sqlSelect(AppConstants.value_getSubmitKpiData).getExpress(),
                SqlKit.sqlExceptSelect(AppConstants.value_getSubmitKpiData).getExpress(), region, excludeStatus);
        List<SubmitKpiVo> result = Lists.newArrayListWithCapacity(page1.getList().size());
        for (Record record : page1.getList()) {
            result.add(new SubmitKpiVo(record.getDate("date"), record.getDate("vkpiSubmitTime"), record.getDate("vkpiHandleTime"), record.getInt("vkpiStatus")));
        }
        return new Page1<>(result, result.size());
    }

    public long countKpis(Integer region, Date date, Integer status) {
        return Db.queryLong(SqlKit.sql(AppConstants.value_countKpis), date, region, status);
    }

    public void deleteKpiValue(Integer region, Date date, String kpiId, Integer... status) {
        Object[][] params = new Object[][]{{date, region, kpiId}};
        Db.batch(SqlKit.sql(AppConstants.value_deleteKpiValue) + CollectBuilder.builderInClause("vkpiStatus", Arrays.asList(status)).build(true), params,
                params.length);
    }

    public void deleteCatValue(Integer region, Date date, String defCatId, Integer... status) {
        Object[][] params = new Object[][]{{date, region, defCatId}};
        Db.batch(SqlKit.sql(AppConstants.value_deleteCatValue) + CollectBuilder.builderInClause("vcatStatus", Arrays.asList(status)).build(true), params,
                params.length);
    }

    public void deleteCatValues(Integer region, Date date, List<String> dCatIds, Integer... status) {
        Object[][] params = new Object[][]{{date, region}};
        CollectBuilder collectBuilder = new CollectBuilder();
        collectBuilder.in("vcatStatus", Arrays.asList(status));
        collectBuilder.in("dcatId", dCatIds);
        Db.batch(SqlKit.sql(AppConstants.value_deleteCatValues) + collectBuilder.build(true), params, params.length);
    }

    public void deleteClassValue(Integer region, Date date, String dClassId, Integer... status) {
        Object[][] params = new Object[][]{{date, region, dClassId}};
        Db.batch(SqlKit.sql(AppConstants.value_deleteClassValue) + CollectBuilder.builderInClause("vclassStatus", Arrays.asList(status)).build(true),
                params, params.length);
    }

    public void deleteClassValues(Integer region, Date date, List<String> dClassIds, Integer... status) {
        Object[][] params = new Object[][]{{date, region}};
        CollectBuilder collectBuilder = new CollectBuilder();
        collectBuilder.in("vclassStatus", Arrays.asList(status));
        collectBuilder.in("dclassId", dClassIds);
        Db.batch(SqlKit.sql(AppConstants.value_deleteClassValues) + collectBuilder.build(true), params, params.length);
    }

    public Page1<Record> getKpiData(int offset, int count, List<Integer> regions, Integer city, Integer... status) {
        CollectBuilder collectBuilder = new CollectBuilder();
        collectBuilder.in("vkpiStatus", Arrays.asList(status));
        collectBuilder.in("vkpiRegion", regions);
        return Db.paginateOverOffset(offset, count, SqlKit.sqlSelect(AppConstants.value_getKpiData).getExpress(),
                SqlKit.sqlExceptSelect(AppConstants.value_getKpiData).getExpress() + collectBuilder.build(true), city);
    }

    public void submitKpiValueStatus(Integer region, Date date, Integer status, Date submitTime) {
        Object[][] params = new Object[][]{{status, submitTime, date, region}};
        Db.batch(SqlKit.sql(AppConstants.value_submitKpiValueStatus), params, params.length);
    }

    public void submitClassValueStatus(Integer region, Date date, Integer status, Date submitTime) {
        Object[][] params = new Object[][]{{status, submitTime, date, region}};
        Db.batch(SqlKit.sql(AppConstants.value_submitClassValueStatus), params, params.length);
    }

    public void submitCatValueStatus(Integer region, Date date, Integer status, Date submitTime) {
        Object[][] params = new Object[][]{{status, submitTime, date, region}};
        Db.batch(SqlKit.sql(AppConstants.value_submitCatValueStatus), params, params.length);

    }

    public void deleteKpiValues(List<String> dKpidIds, Integer region, Date date, Integer... status) {
        Object[][] params = new Object[][]{{date, region}};
        CollectBuilder collectBuilder = new CollectBuilder();
        collectBuilder.in("vkpiStatus", Arrays.asList(status));
        collectBuilder.in("dkpiId", dKpidIds);
        Db.batch(SqlKit.sql(AppConstants.value_deleteKpiValues) + collectBuilder.build(true), params, params.length);
    }

    public ValueKpi getRandomValueKpi(Integer region, Date date, Integer status) {
        return ValueKpi.dao.findFirst("select vkpiValue from " + TableMapping.me().getTable(ValueKpi.class).getName() +
                " where vkpiDate = ? and vkpiRegion = ?", date, region);
    }

    public ValueSynthetical getValueSyntheticalByDataAndRegion(Integer region, Date date) {
        return ValueSynthetical.dao.findFirst(SqlKit.sql(AppConstants
                .value_getValueSyntheticalByDataAndRegion), date, region);
    }
}