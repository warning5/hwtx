package com.hwtx.fncel.pbc.service;

import com.google.common.collect.Lists;
import com.hwtx.fncel.pbc.dao.DataComputeDao;
import com.hwtx.fncel.pbc.entity.ValueCat;
import com.hwtx.fncel.pbc.entity.ValueClass;
import com.hwtx.fncel.pbc.entity.ValueKpi;
import com.hwtx.fncel.pbc.entity.ValueSynthetical;
import com.hwtx.fncel.pbc.util.AppConstants;
import com.hwtx.fncel.pbc.vo.SaveKpiValue;
import com.hwtx.fncel.pbc.vo.SubmitKpiVo;
import com.hwtx.modules.sys.service.RegionService;
import com.hwtxframework.ioc.annotation.Component;
import com.jfinal.plugin.activerecord.Page1;
import com.jfinal.plugin.activerecord.Record;
import com.thinkgem.jeesite.common.utils.IdGen;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created by panye on 2014/10/7.
 */
@Component
public class DataComputeService {

    @Resource
    private DataComputeDao dataComputeDao;
    @Resource
    private DataInputService dataInputService;
    @Resource
    private RegionService regionService;

    public void saveKpiValue(String kpiId, String vClassId, Integer region, Date date, Float result) {
        deleteKpiValue(region, date, kpiId);
        dataComputeDao.saveKpiValue(buildValueKpi(kpiId, result, region, vClassId, date, AppConstants.Value_Inidata_Save_Status));
    }

    public void saveKpiValues(List<SaveKpiValue> saveKpis, Integer region, Date date) {
        List<ValueKpi> valueKpis = Lists.newArrayListWithCapacity(saveKpis.size());
        List<String> dKpidIds = Lists.newArrayListWithCapacity(saveKpis.size());
        for (SaveKpiValue saveKpiValue : saveKpis) {
            valueKpis.add(buildValueKpi(saveKpiValue.getDkpiId(), saveKpiValue.getValue(), region,
                    saveKpiValue.getVclassId(), date, AppConstants.Value_Inidata_Save_Status));
            dKpidIds.add(saveKpiValue.getDkpiId());
        }
        deleteKpiValues(dKpidIds, region, date);
        ValueKpi.dao.batchSave(valueKpis);
    }

    private ValueKpi buildValueKpi(String kpiId, float result, Integer region, String vClassId, Date date,
                                   Integer status) {
        ValueKpi valueKpi = new ValueKpi();
        valueKpi.setDkpiId(kpiId);
        valueKpi.setVkpiId(IdGen.uuid());
        valueKpi.setVkpiCalDate(new Date());
        valueKpi.setVkpiValue(result);
        valueKpi.setVkpiRegion(region);
        if (vClassId != null) {
            valueKpi.setVclassId(vClassId);
        }
        valueKpi.setVkpiDate(date);
        valueKpi.setVkpiStatus(status);
        return valueKpi;
    }

    public void deleteKpiValue(Integer region, Date date, String kpiId) {
        dataComputeDao.deleteKpiValue(region, date, kpiId, AppConstants.Value_Inidata_Save_Status, AppConstants.Value_Inidata_Back_Status);
    }

    public void deleteKpiValues(List<String> dKpidIds, Integer region, Date date) {
        dataComputeDao.deleteKpiValues(dKpidIds, region, date, AppConstants.Value_Inidata_Save_Status,
                AppConstants.Value_Inidata_Back_Status);
    }

    public Float getKpiValue(String kpiId, Date date) {
        return dataComputeDao.getKpiValue(kpiId, date);
    }

    public String saveCatValue(String defCatId, float value, Integer region, Date date) {
        deleteCatValue(region, date, defCatId);
        ValueCat valueCat = buildValueCat(defCatId, region, date, value, AppConstants.Value_Inidata_Save_Status);
        dataComputeDao.saveCatValue(valueCat);
        return valueCat.getVcatId();
    }

    public void saveCatValues(List<Triple<String, String, Float>> deCatIdAndValue, Date date, Integer region) {
        List<ValueCat> valueCats = Lists.newArrayListWithCapacity(deCatIdAndValue.size());
        List<String> dCatIds = Lists.newArrayListWithCapacity(deCatIdAndValue.size());
        for (Triple<String, String, Float> triple : deCatIdAndValue) {
            valueCats.add(buildValueCat(triple.getLeft(), triple.getMiddle(), region, date, triple.getRight(), AppConstants.Value_Inidata_Save_Status));
            dCatIds.add(triple.getLeft());
        }
        deleteCatValues(region, date, dCatIds);
        ValueCat.dao.batchSave(valueCats);
    }

    private ValueCat buildValueCat(String defCatId, Integer region, Date date, Float value, Integer status) {
        return buildValueCat(defCatId, IdGen.uuid(), region, date, value, status);
    }

    private ValueCat buildValueCat(String defCatId, String vcatId, Integer region, Date date, Float value, Integer status) {
        ValueCat valueCat = new ValueCat();
        valueCat.setDcatId(defCatId);
        valueCat.setVcatCalDate(new Date());
        valueCat.setVcatRegion(region);
        valueCat.setVcatId(vcatId);
        valueCat.setVcatDate(date);
        valueCat.setVcatValue(value);
        valueCat.setVcatStatus(status);
        return valueCat;
    }

    public void deleteCatValue(Integer region, Date date, String defCatId) {
        dataComputeDao.deleteCatValue(region, date, defCatId, AppConstants.Value_Inidata_Save_Status, AppConstants.Value_Inidata_Back_Status);
    }

    public void deleteCatValues(Integer region, Date date, List<String> dCatIds) {
        dataComputeDao.deleteCatValues(region, date, dCatIds, AppConstants.Value_Inidata_Save_Status,
                AppConstants.Value_Inidata_Back_Status);
    }

    public String saveClassValue(String dClassId, String vcatId, float value, Integer region, Date date) {
        deleteClassValue(region, date, dClassId);
        ValueClass valueClass = buildValueClass(vcatId, dClassId, region, date, value, AppConstants.Value_Inidata_Save_Status);
        dataComputeDao.saveClassValue(valueClass);
        return valueClass.getVclassId();
    }

    public void saveClassValues(List<Triple<String, String, Float>> classIdAndValue, String vcatId, Date date, Integer region) {
        List<ValueClass> valueClasses = Lists.newArrayListWithCapacity(classIdAndValue.size());
        List<String> dClassIds = Lists.newArrayListWithCapacity(classIdAndValue.size());
        for (Triple<String, String, Float> triple : classIdAndValue) {
            valueClasses.add(buildValueClass(vcatId, triple.getMiddle(), triple.getLeft(), region, date, triple.getRight(), AppConstants.Value_Inidata_Save_Status));
            dClassIds.add(triple.getLeft());
        }
        deleteClassValues(region, date, dClassIds);
        ValueClass.dao.batchSave(valueClasses);
    }

    private ValueClass buildValueClass(String vcatId, String dClassId, Integer region, Date date, Float value, Integer status) {
        return buildValueClass(vcatId, IdGen.uuid(), dClassId, region, date, value, status);
    }

    private ValueClass buildValueClass(String vcatId, String vclassId, String dClassId, Integer region, Date date, Float value, Integer status) {
        ValueClass valueClass = new ValueClass();
        valueClass.setVclassId(vclassId);
        valueClass.setVcatId(vcatId);
        valueClass.setDclassId(dClassId);
        valueClass.setVclassCalDate(new Date());
        valueClass.setVclassDate(date);
        valueClass.setVclassRegion(region);
        valueClass.setVclassValue(value);
        valueClass.setVclassStatus(status);
        return valueClass;
    }

    public void deleteClassValue(Integer region, Date date, String dClassId) {
        dataComputeDao.deleteClassValue(region, date, dClassId, AppConstants.Value_Inidata_Save_Status, AppConstants.Value_Inidata_Back_Status);
    }

    public void deleteClassValues(Integer region, Date date, List<String> dClassIds) {
        dataComputeDao.deleteClassValues(region, date, dClassIds, AppConstants.Value_Inidata_Save_Status,
                AppConstants.Value_Inidata_Back_Status);
    }

    public Page1<SubmitKpiVo> getSubmitKpiData(int offset, int count, Integer region, Integer excludeStatus) {
        return dataComputeDao.getSubmitKpiData(offset, count, region, excludeStatus);
    }

    public void submitData(Integer region, Date date) {
        dataInputService.updateValueIinidataStatus(region, date, AppConstants.Value_Inidata_Submit_Status);
        dataInputService.updateValueIinidataStatisticStatus(region, date, AppConstants.Value_Inidata_Submit_Status);
        dataComputeDao.submitKpiValueStatus(region, date, AppConstants.Value_Inidata_Submit_Status, new Date());
        dataComputeDao.submitClassValueStatus(region, date, AppConstants.Value_Inidata_Submit_Status, new Date());
        dataComputeDao.submitCatValueStatus(region, date, AppConstants.Value_Inidata_Submit_Status, new Date());
    }

    public boolean checkSubmitDataExist(Integer region, Date startdate, Integer status) {
        long count = dataComputeDao.countKpis(region, startdate, status);
        if (count == 0) {
            return false;
        }
        return true;
    }

    public Page1<SubmitKpiVo> getCityKpiData(int offset, int count, Integer region, Integer... status) {
        List<SubmitKpiVo> list = Lists.newArrayList();
        Page1<Record> page1 = dataComputeDao.getKpiData(offset, count, regionService.getAreaCodes(region), region, status);
        for (Record record : page1.getList()) {
            SubmitKpiVo submitKpiVo = new SubmitKpiVo(record.getDate("date"), record.getDate("vkpiSubmitTime"),
                    record.getDate("vkpiHandleTime"), record.getInt("vkpiStatus"), record.getStr("orgName"));
            submitKpiVo.setVkpiRegion(record.getInt("vkpiRegion"));
            submitKpiVo.setOrgId(record.getStr("orgId"));
            list.add(submitKpiVo);
        }
        return new Page1<>(list, list.size());
    }

    public void updateValueIinidataStatisticStatus(Integer region, Date date, Integer status) {
        dataInputService.updateValueIinidataStatisticStatus(region, date, status);
    }

    public boolean hasComputeKpi(Integer region, Date date) {
        ValueKpi valueKpi = dataComputeDao.getRandomValueKpi(region, date, AppConstants.Value_Inidata_Save_Status);
        return valueKpi.getVkpiValue() != null;
    }

    public void updateValueSynthetical(ValueSynthetical valueSynthetical) {
        ValueSynthetical exist = dataComputeDao.getValueSyntheticalByDataAndRegion(valueSynthetical.getRegion(),
                valueSynthetical.getDate());
        if (exist != null) {
            valueSynthetical.update();
        } else {
            valueSynthetical.save();
        }

    }
}
