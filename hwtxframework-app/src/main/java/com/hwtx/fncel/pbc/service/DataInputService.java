package com.hwtx.fncel.pbc.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hwtx.fncel.pbc.dao.DataInputDao;
import com.hwtx.fncel.pbc.entity.AppDataCheck;
import com.hwtx.fncel.pbc.entity.DefInidata;
import com.hwtx.fncel.pbc.entity.ValueInidata;
import com.hwtx.fncel.pbc.entity.ValueInidatastatics;
import com.hwtx.fncel.pbc.exception.ExistException;
import com.hwtx.fncel.pbc.util.AppConstants;
import com.hwtx.fncel.pbc.vo.ComputeValue;
import com.hwtx.fncel.pbc.vo.DefValueInidata;
import com.hwtx.fncel.pbc.vo.InputDataVo;
import com.hwtx.fncel.pbc.vo.ValueInidataDetail;
import com.hwtx.fncel.util.FncelUtils;
import com.hwtx.modules.sys.utils.UserUtils;
import com.hwtxframework.ioc.annotation.Component;
import com.jfinal.aop.Before;
import com.jfinal.ext.kit.RecordKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page1;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.thinkgem.jeesite.common.utils.IdGen;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by panye on 2014/9/26.
 */
@Component
public class DataInputService {

    @Resource
    private DataInputDao dataInputDao;
    @Resource
    private RuleService ruleService;

    public List<InputDataVo> getTableData(String type, Integer region, String orgId, boolean statistic) {
        return dataInputDao.getTableData(type, region, orgId, statistic);
    }

    public List<DefValueInidata> getDefValueInidatasBySubmitRole(String submitRole, Integer region,
                                                                 Date date, boolean statistic, String orgId,
                                                                 Integer... status) {

        List<Record> records;
        List<DefValueInidata> result = Lists.newArrayList();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        if (!statistic) {
            records = dataInputDao.getDefValueInidatas(submitRole, region, date, status);
        } else {
            records = dataInputDao.getDefStatisticValueInidatas(submitRole, region, date, orgId, status);
        }
        DecimalFormat format2 = new DecimalFormat("0.00");
        if (records != null) {
            for (Record record : records) {
                DefInidata defInidata = (DefInidata) RecordKit.toModel(DefInidata.class, record);
                Float value = record.getFloat("value");
                String fValue = "0";
                int fractionDigits = defInidata.getDiniFractionDigits();
                if (fractionDigits != 2) {
                    DecimalFormat formatn;
                    if (fractionDigits == 0) {
                        formatn = new DecimalFormat("0");
                    } else {
                        formatn = new DecimalFormat("0." + String.format("%0" + fractionDigits + "d", 0));
                    }
                    fValue = value != null ? formatn.format(value) : "0";
                } else {
                    fValue = value != null ? format2.format(value) : "0";
                }
                result.add(new DefValueInidata(defInidata, fValue, simpleDateFormat.format(record.getDate("date"))));
            }
        }
        return result;
    }

    public List<ValueInidataDetail> getValueInidatas(Integer region, Date date, List<String> nonStatisticInidataIds,
                                                     List<String> statisticInidataIds, Integer... status) {
        List<Record> records = Lists.newArrayList();
        List<ValueInidataDetail> result = Lists.newArrayList();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        if (nonStatisticInidataIds.size() != 0) {
            records.addAll(dataInputDao.getValueInidataDetail(region, date, nonStatisticInidataIds, status));
        }
        if (statisticInidataIds.size() != 0) {
            records.addAll(dataInputDao.getStatisticValueInidatasDatail(region, date, statisticInidataIds, status));
        }
        if (records.size() != 0) {
            for (Record record : records) {
                result.add(new ValueInidataDetail(record.getStr("orgName"), record.getFloat("value"),
                        record.getStr("name"), simpleDateFormat.format(date)));
            }
        }
        return result;
    }

    @Before(Tx.class)
    public void save(List<ValueInidata> values, String type, Integer region, Date date, boolean statistic,
                     String orgId) throws ExistException {

        long count;
        if (!statistic) {
            count = dataInputDao.countValueInidata(type, region, date);
        } else {
            count = dataInputDao.countStatisticValueInidata(type, region, date, orgId);
        }
        if (count > 0) {
            throw new ExistException();
        }

        Pair<List<Object[]>, List<Object[]>> pair = getParamData(values);

        List<Object[]> params_value = pair.getLeft();
        if (params_value.size() != 0) {
            dataInputDao.saveValueInidata(params_value.toArray(new Object[0][0]));
        }

        List<Object[]> statistic_param = pair.getRight();
        if (statistic_param.size() != 0) {
            dataInputDao.saveValueInidatastatics(statistic_param.toArray(new Object[0][0]));
        }
    }

    /**
     * 年份不允许修改
     *
     * @param values
     * @param date
     * @param statistic
     * @param submitRole
     * @param region
     * @param orgId
     * @throws ExistException
     */
    @Before(Tx.class)
    public void update(List<ValueInidata> values, Date date, boolean statistic, String submitRole, Integer region,
                       String orgId) throws ExistException {

        if (statistic) {
            dataInputDao.deleteValueInidatastatics(date, region, orgId);
        } else {
            dataInputDao.deleteValueInidata(date, submitRole, region);
        }
        Pair<List<Object[]>, List<Object[]>> pair = getParamData(values);

        List<Object[]> params_value = pair.getLeft();
        if (params_value.size() != 0) {
            dataInputDao.saveValueInidata(params_value.toArray(new Object[0][0]));
        }

        List<Object[]> statistic_param = pair.getRight();
        if (statistic_param.size() != 0) {
            dataInputDao.saveValueInidatastatics(statistic_param.toArray(new Object[0][0]));
        }
    }

    private Pair<List<Object[]>, List<Object[]>> getParamData(List<ValueInidata> values) {
        List<Object[]> params_value = Lists.newArrayListWithCapacity(values.size());
        List<Object[]> statistic_param = Lists.newArrayList();
        for (ValueInidata valueInidata : values) {
            if (valueInidata.isInsert()) {
                Object[] param = new Object[]{valueInidata.getViniDataId(), valueInidata.getDiniDataId(),
                        valueInidata.getVkpiId(), valueInidata.getViniDataValue(), valueInidata.getViniDataCalRuleId(),
                        valueInidata.getViniDataSubDate(), valueInidata.getViniDataAuthRuleId(), valueInidata.getViniDataRegion()
                        , valueInidata.getViniDataSubmitOrgRole(), valueInidata.getIsStastic(),
                        valueInidata.getViniDataCheckMark(), valueInidata.getViniDate(), valueInidata.getViniStatus(),
                        valueInidata.getViniDateFillDate()};
                params_value.add(param);
            }

            ValueInidatastatics valueInidatastatics = valueInidata.getValueInidatastatistic();
            if (valueInidatastatics != null) {
                Object[] param = new Object[]{valueInidatastatics.getViniDataId(),
                        valueInidatastatics.getSiniDataId(), valueInidatastatics.getSubmitOrg(), valueInidatastatics.getSiniDataValue(),
                        valueInidatastatics.getSiniDataRegion(), valueInidatastatics.getSiniDataCheckMark(),
                        valueInidatastatics.getSiniDate(), valueInidatastatics.getSiniStatus()};
                statistic_param.add(param);
            }
        }
        return new ImmutablePair<>(params_value, statistic_param);
    }

    public void delete(Date date, boolean statistic, String submitRole, Integer region, String orgId) {
        if (statistic) {
            dataInputDao.deleteValueInidatastatics(date, region, orgId);
            Long count = dataInputDao.countValueInidatastatics(date, region, submitRole);
            if (count == 0) {
                dataInputDao.deleteValueInidata(date, submitRole, region);
            }
        } else {
            dataInputDao.deleteValueInidata(date, submitRole, region);
        }
    }

    public boolean checkValueExist(Integer region, Date date, String diniDataId, Integer isStaticMark,
                                   String submitOrgRole, Integer... status) {
        if (isStaticMark == 0) {
            ValueInidata valueInidata = getValue(region, date, diniDataId, submitOrgRole, status);
            if (valueInidata != null && valueInidata.getViniDataValue() != null) {
                return true;
            }
        } else {
            return mergeStatisticData(region, date, diniDataId, submitOrgRole, status);
        }
        return false;
    }

    private boolean mergeStatisticData(Integer region, Date date, String diniDataId, String submitOrgRole,
                                       Integer... status) {
        Double tvalue = dataInputDao.mergeStatisticData(region, date, diniDataId, submitOrgRole, status);
        if (tvalue == null) {
            return false;
        }
        dataInputDao.updateInidataDataValue(region, date, diniDataId, submitOrgRole, tvalue.floatValue());
        return true;
    }

    public ValueInidata getValue(Integer region, Date date, String diniDataId, String submitOrgRole,
                                 Integer... status) {
        return dataInputDao.getValue(diniDataId, date, region, submitOrgRole, status);
    }

    public ValueInidata getStatisticValue(Integer region, Date date, String diniDataId, String submitOrgRole) {
        return dataInputDao.getStatisticValue(diniDataId, date, region, submitOrgRole);
    }

    public Page1<Map<String, Object>> getFinancialInputDatas(String type, Integer region, Integer status, int offset,
                                                             int count) {
        Page1<Record> page1 = dataInputDao.getFinancialInputDatas(type, region, status, offset, count);
        List<Map<String, Object>> result = Lists.newArrayList();
        List<Record> records = page1.getList();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        for (int i = 0; i < records.size(); i++) {
            Map<String, Object> map = Maps.newHashMap();
            map.put("num", i + 1);
            map.put("date", simpleDateFormat.format(records.get(i).getDate("date")));
            map.put("orgName", records.get(i).getStr("name"));
            map.put("orgId", records.get(i).getStr("orgId"));
            map.put("type", type);
            map.put("region", region);
            map.put("actions", "");
            result.add(map);
        }
        return new Page1<>(result, result.size());
    }

    public void backData(AppDataCheck appDataCheck, Date date, String orgId, int backType) {
        appDataCheck.setCheckedDate(date);
        appDataCheck.setCheckDate(new Date());
        appDataCheck.setCheckUserId(UserUtils.getUser().getUserId());
        appDataCheck.setCheckId(IdGen.uuid());
        appDataCheck.setCheckOrgId(orgId);
        appDataCheck.setCheckType(backType);
        dataInputDao.saveAppDataCheck(appDataCheck);
    }

    public void updateIniData(Integer region, String orgId, String type, Date date, boolean statistic) {
        if (statistic) {
            dataInputDao.updateStatisticStatusBySubmitOrg(type, date, region, orgId, AppConstants.Value_Inidata_Back_Status);
        } else {
            dataInputDao.updateValueStatusBySubmitRole(type, date, region, AppConstants.Value_Inidata_Back_Status);
        }
    }

    public List<ValueInidata> getValues(Date date, Integer region, String type) {
        return dataInputDao.getValues(date, region, type);
    }

    public String getBackData(String orgId, String date, Integer region, Integer checkType) throws Throwable {
        return dataInputDao.getBackData(orgId, FncelUtils.getDate(date), region, checkType);
    }

    public void updateValueIinidataStatus(Integer region, Date date, Integer status) {
        dataInputDao.updateValueIinidataStatus(region, date, status);
    }

    public void updateValueIinidataStatisticStatus(Integer region, Date date, Integer status) {
        dataInputDao.updateValueIinidataStatisticStatus(region, date, status);
    }

    public List<ComputeValue> getKpiData(Integer region, Date date, Integer... status) {
        List<Record> records = dataInputDao.getValueCat(region, date, status);
        List<ComputeValue> result = Lists.newArrayList();
        for (Record record : records) {
            String vcatId = record.getStr("vcatId");
            String dcatId = record.getStr("dcatId");
            result.add(new ComputeValue(dcatId, record.getStr("dcatName"), "0", record.getFloat("vcatValue")));
            List<Record> classRecords = dataInputDao.getValueClass(region, date, vcatId, status);
            for (Record clRecord : classRecords) {
                String vclassId = clRecord.getStr("vclassId");
                String dclassId = clRecord.getStr("dclassId");
                result.add(new ComputeValue(dclassId, clRecord.getStr("dclassName"), dcatId, clRecord.getFloat("vclassValue")));
                fillKpis(vclassId, dclassId, region, date, result, status);
            }
        }
        return result;
    }

    private void fillKpis(String vclassId, String dclassId, Integer region, Date date, List<ComputeValue> result, Integer... status) {
        List<Record> records = dataInputDao.getValueKpi(vclassId, region, date, status);
        List<ComputeValue> delayKpis = Lists.newArrayList();
        Map<String, Integer> visitedKpis = Maps.newHashMap();
        for (Record record : records) {
            String dkpiId = record.getStr("dkpiId");
            visitedKpis.put(dkpiId, result.size());
            String pid = record.getStr("pid");
            if (StrKit.isBlank(pid)) {
                ComputeValue computeValue = new ComputeValue(dkpiId, record.getStr("dkpiName"), dclassId,
                        record.getFloat("vkpiValue"), record.getInt("complex") != 1);
                result.add(computeValue);
            } else {
                ComputeValue computeValue = new ComputeValue(dkpiId, record.getStr("dkpiName"), pid, record.getFloat("vkpiValue"), true);
                if (visitedKpis.containsKey(pid)) {
                    Integer site = visitedKpis.get(pid);
                    if (site != null) {
                        result.add(site + 1, computeValue);
                        visitedKpis.put(pid, site + 1);
                    }
                } else {
                    delayKpis.add(computeValue);
                }
            }
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

    public void updateSubmitKpiData(Date date, Integer region, Integer status) {
        dataInputDao.updateValueIinidataStatus(region, date, status);
        dataInputDao.updateValueIinidataStatisticStatus(region, date, status);
        dataInputDao.handleKpiValueStatus(date, region, status, new Date());
        dataInputDao.handleClassValueStatus(date, region, status, new Date());
        dataInputDao.handleCatValueStatus(date, region, status, new Date());
    }
}
