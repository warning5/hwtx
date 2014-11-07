package com.hwtx.fncel.pbc.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hwtx.fncel.pbc.dao.DataInputDao;
import com.hwtx.fncel.pbc.entity.ValueSynthetical;
import com.hwtx.fncel.pbc.util.AppConstants;
import com.hwtxframework.ioc.annotation.Component;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;
import com.thinkgem.jeesite.common.utils.IdGen;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Resource;
import javax.el.ELProcessor;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by panye on 2014/11/2.
 */
@Component
public class StatisticService {

    @Resource
    private DataInputDao dataInputDao;
    @Resource
    private DataComputeService dataComputeService;

    private float getMaxKpiValueOfRegions(Date date, String dkpiId, List<Integer> regions, Integer status) {
        return dataInputDao.getMaxKpiValueOfRegions(date, dkpiId, regions, status);
    }

    private float getMinKpiValueOfRegions(Date date, String dkpiId, List<Integer> regions, Integer status) {
        return dataInputDao.getMinKpiValueOfRegions(date, dkpiId, regions, status);
    }

    public void standardize(Integer region, Date date, Integer status, List<Integer> regions) throws Exception {
        List<Record> records = dataInputDao.getValueCat(region, date, status);
        List<Object[]> kpiScore = Lists.newArrayList();
        List<Object[]> classScore = Lists.newArrayList();
        List<Object[]> catScore = Lists.newArrayList();
        Float catValue = 0f, syntheticalValue = 0f;
        for (Record record : records) {
            String vcatId = record.getStr("vcatId");
            List<Record> classRecords = dataInputDao.getValueClass(region, date, vcatId, status);
            for (Record clRecord : classRecords) {
                String vclassId = clRecord.getStr("vclassId");
                Float result = fillKpis(vclassId, region, date, status, regions, kpiScore);
                classScore.add(new Object[]{result, vclassId});
                catValue += result;
            }
            catScore.add(new Object[]{catValue, vcatId});
            syntheticalValue += catValue;
        }

        int syntheticalScore = standradizeSynthetical(syntheticalValue);

        if (kpiScore.size() != 0) {
            dataInputDao.updateKpisScore(kpiScore.toArray(new Object[0][0]));
        }
        if (classScore.size() != 0) {
            dataInputDao.updateClassScore(classScore.toArray(new Object[0][0]));
        }
        if (catScore.size() != 0) {
            dataInputDao.updateCatScore(catScore.toArray(new Object[0][0]));
        }
        ValueSynthetical valueSynthetical = new ValueSynthetical();
        valueSynthetical.setId(IdGen.uuid());
        valueSynthetical.setDate(date);
        valueSynthetical.setRegion(region);
        valueSynthetical.setScore(syntheticalScore);
        valueSynthetical.setValue(syntheticalValue);
        dataComputeService.updateValueSynthetical(valueSynthetical);
    }

    private int standradizeSynthetical(Float syntheticalValue) {
        return (int) (syntheticalValue * 40) / 100 + 60;
    }

    @AllArgsConstructor
    @Getter
    class ComplexKpi {
        Float score;
        Float value;
        String dkpiId;
    }

    private Float fillKpis(String vclassId, Integer region, Date date, Integer status,
                           List<Integer> regions, List<Object[]> kpiScore) throws Exception {
        List<Record> records = dataInputDao.getValueKpi(vclassId, region, date, status);
        Map<String, List<ComplexKpi>> complexKpi = Maps.newHashMap();
        //key:kpiId value: valueId
        Map<String, String> valueIdAndKpiId = Maps.newHashMap();
        Float result = 0f;
        for (Record record : records) {
            String dkpiId = record.getStr("dkpiId");
            Integer complex = record.getInt("complex");
            float dkpiscore = record.getFloat("dkpiscore");
            String vkpiId = record.getStr("vkpiId");
            if (1 != complex) {
                Float value = record.getFloat("vkpiValue");
                Object standardizeType = record.get("dkpiStandardizeType");
                if (standardizeType == null) {
                    throw new Exception("无法获取指标[" + record.getStr("dkpiName") + "]的类型");
                }
                Integer type = (Integer) standardizeType;
                String dkpiExtendExp = record.getStr("dkpiExtendExp");
                Float score = null;
                switch (type) {
                    case AppConstants.KPI_STANDARDIZE_TYPE_FORWARD:
                        score = handleForward(dkpiId, date, value, regions, dkpiExtendExp, status);
                        break;
                    case AppConstants.KPI_STANDARDIZE_TYPE_BACKFORWARD:
                        score = handleBackForward(dkpiId, date, value, regions, dkpiExtendExp,
                                status);
                        break;
                    case AppConstants.KPI_STANDARDIZE_TYPE_NOYES:
                        if (value > 0) {
                            score = 100f;
                        } else {
                            score = 0f;
                        }
                        break;
                }
                String pid = record.getStr("pid");
                if (StrKit.notBlank(pid)) {
                    List<ComplexKpi> kpis = complexKpi.get(pid);
                    if (kpis == null) {
                        kpis = Lists.newArrayList();
                    }
                    kpis.add(new ComplexKpi(dkpiscore, score, dkpiId));
                    complexKpi.put(pid, kpis);
                }
                Float tv = (score * dkpiscore) / 100;
                if (tv.isNaN()) {
                    throw new Exception("指标[" + record.getStr("dkpiName") + "]的标准化值[" + tv +
                            "]无效");
                }
                result += tv;
                kpiScore.add(new Object[]{tv, vkpiId});
            } else {
                valueIdAndKpiId.put(dkpiId, vkpiId);
            }
        }
        if (complexKpi.size() != 0) {
            for (Map.Entry<String, List<ComplexKpi>> entry : complexKpi.entrySet()) {
                float value = 0;
                for (ComplexKpi kpi : entry.getValue()) {
                    value += (kpi.getValue() * kpi.getScore()) / 100;
                }
                kpiScore.add(new Object[]{value, valueIdAndKpiId.get(entry.getKey())});
            }
        }
        return result;
    }

    private float handleForward(String dkpiId, Date date, Float value, List<Integer> regions,
                                String dkpiExtendExp, Integer status) {
        if (StrKit.notBlank(dkpiExtendExp)) {
            boolean result = handleExtendExp(value, dkpiExtendExp);
            if (result) {
                return 100;
            }
        }

        float max = getMaxKpiValueOfRegions(date, dkpiId, regions, status);
        float min = getMinKpiValueOfRegions(date, dkpiId, regions, status);
        if ((max - min) == 0) {
            return 0;
        }
        return ((value - min) / (max - min)) * 100;
    }

    private float handleBackForward(String dkpiId, Date date, Float value, List<Integer> regions,
                                    String dkpiExtendExp, Integer status) {

        if (StrKit.notBlank(dkpiExtendExp)) {
            boolean result = handleExtendExp(value, dkpiExtendExp);
            if (result) {
                return 100;
            }
        }

        return 100 - handleForward(dkpiId, date, value, regions, dkpiExtendExp, status);
    }

    private boolean handleExtendExp(float value, String dkpiExtendExp) {
        ELProcessor elp = new ELProcessor();
        if (dkpiExtendExp.indexOf("%") > 0) {
            elp.setValue("a", value * 100 + "%");
        } else {
            elp.setValue("a", value);
        }
        return (boolean) elp.eval("a " + dkpiExtendExp);
    }
}
