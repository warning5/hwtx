package com.hwtx.fncel.pbc.util;

import com.google.common.collect.Maps;

import java.util.Map;

public class AppConstants {

    public final static String pbc_getFinancialOrgsByPbcOrg = "pbc.getFinancialOrgsByPbcOrg";
    public final static String pbc_getAppPbcOgs = "pbc.getAppPbcOgs";
    public final static String pbc_insertFinancialOrg = "pbc.insertFinancialOrg";
    public final static String pbc_insertAppPbcInfo = "pbc.insertAppPbcInfo";
    public final static String pbc_updateFinancialOrg = "pbc.updateFinancialOrg";
    public final static String pbc_updateAppPbcInfo = "pbc.updateAppPbcInfo";
    public final static String pbc_relationPbcAndFin = "pbc.relationPbcAndFin";
    public final static String pbc_getAppPbcInfo = "pbc.getAppPbcInfo";
    public final static String pbc_deletePbcOrglByOrgId = "pbc.deletePbcOrglByOrgId";
    public final static String pbc_getPbcUsers = "pbc.getPbcUsers";

    public final static String rule_getDefCats = "rule.getDefCats";
    public final static String rule_getDefClasses = "rule.getDefClasses";
    public final static String rule_getDefKpis = "rule.getDefKpis";
    public final static String rule_getDefCatByName = "rule.getDefCatByName";
    public final static String rule_getDefClassByName = "rule.getDefClassByName";
    public final static String rule_getDefKpiByName = "rule.getDefKpiByName";
    public final static String rule_getDefInidata = "rule.getDefInidata";
    public final static String rule_getDefInidataByName = "rule.getDefInidataByName";
    public final static String rule_getIniDataNameAndLabels = "rule.getIniDataNameAndLabels";
    public final static String rule_getFormulaByKpiId = "rule.getFormulaByKpiId";
    public final static String rule_getDefInidatasBySubmitRole = "rule.getDefInidatasBySubmitRole";
    public final static String rule_deleteDefInidata = "rule.deleteDefInidata";
    public final static String rule_getSubmitOrgRole = "rule.getSubmitOrgRole";
    public final static String rule_getFormulaInidata = "rule.getFormulaInidata";

    public final static String appOrg_getPbcInfoByOrgId = "appOrg.getPbcInfoByOrgId";
    public final static String appOrg_getAppPbcOrgs = "appOrg.getAppPbcOrgs";
    public final static String appOrg_getAppFinancialOrgs = "appOrg.getAppFinancialOrgs";


    public final static String value_countValueInidata = "value.countValueInidata";
    public final static String value_saveValueInidata = "value.saveValueInidata";
    public final static String value_saveValueInidatastatics = "value.saveValueInidatastatics";
    public final static String value_deleteValueInidatastatics = "value.deleteValueInidatastatics";
    public final static String value_deleteValueInidata = "value.deleteValueInidata";
    public final static String value_getTableData = "value.getTableData";
    public final static String value_getStatisticTableData = "value.getStatisticTableData";
    public final static String value_getDefValueInidatas = "value.getDefValueInidatas";
    public final static String value_getDefStatisticValueInidatas = "value.getDefStatisticValueInidatas";
    public final static String value_getValue = "value.getValue";
    public final static String value_getStatisticValue = "value.getStatisticValue";
    public final static String value_getFinancialInputDatas = "value.getFinancialInputDatas";
    public final static String value_updateValueStatusBySubmitRole = "value.updateValueStatusBySubmitRole";
    public final static String value_updateStatisticStatusBySubmitOrg = "value.updateStatisticStatusBySubmitOrg";
    public final static String value_getValues = "value.getValues";
    public final static String value_countValueInidatastatics = "value.countValueInidatastatics";
    public final static String value_getBackData = "value.getBackData";
    public final static String value_countStatisticValueInidata = "value.countStatisticValueInidata";
    public final static String value_mergeStatisticData = "value.mergeStatisticData";
    public final static String value_updateInidataDataValue = "value.updateInidataDataValue";
    public final static String value_getSubmitKpiData = "value.getSubmitKpiData";
    public final static String value_updateValueIinidataStatus = "value.updateValueIinidataStatus";
    public final static String value_updateValueIinidataStatisticStatus = "value.updateValueIinidataStatisticStatus";

    public final static String value_getMaxKpiValueOfRegions = "value.getMaxKpiValueOfRegions";
    public final static String value_getMinKpiValueOfRegions = "value.getMinKpiValueOfRegions";
    public final static String value_updateKpisScore = "value.updateKpisScore";
    public final static String value_updateClassScore = "value.updateClassScore";
    public final static String value_updateCatScore = "value.updateCatScore";

    public final static String value_handleKpiValueStatus = "value.handleKpiValueStatus";
    public final static String value_handleClassValueStatus = "value.handleClassValueStatus";
    public final static String value_handleCatValueStatus = "value.handleCatValueStatus";

    public final static String value_submitKpiValueStatus = "value.submitKpiValueStatus";
    public final static String value_submitClassValueStatus = "value.submitClassValueStatus";
    public final static String value_submitCatValueStatus = "value.submitCatValueStatus";

    public final static String value_countKpis = "value.countKpis";
    public final static String value_deleteKpiValue = "value.deleteKpiValue";
    public final static String value_deleteKpiValues = "value.deleteKpiValues";
    public final static String value_deleteCatValue = "value.deleteCatValue";
    public final static String value_deleteCatValues = "value.deleteCatValues";
    public final static String value_deleteClassValue = "value.deleteClassValue";
    public final static String value_deleteClassValues = "value.deleteClassValues";
    public final static String value_getKpiData = "value.getKpiData";
    public final static String value_getValueCat = "value.getValueCat";
    public final static String value_getValueClass = "value.getValueClass";
    public final static String value_getValueKpi = "value.getValueKpi";
    public final static String value_getValueSyntheticalByDataAndRegion = "value.getValueSyntheticalByDataAndRegion";

    public final static String valueSubmit_getValueInidataDetail = "valueSubmit.getValueInidataDetail";
    public final static String valueSubmit_getStatisticValueInidatasDatail = "valueSubmit.getStatisticValueInidatasDatail";

    public final static String financial_getFinancialUsers = "financial.getFinancialUsers";
    public final static String financial_updateAppUserBySysUserId = "financial.updateAppUserBySysUserId";
    public final static String financial_deleteFinancialByOrgId = "financial.deleteFinancialByOrgId";

    public static final String survey_getSurveies = "survey.getSurveies";
    public static final String survey_getQuestions = "survey.getQuestions";
    public static final String survey_saveAnswers = "survey.saveAnswers";
    public static final String survey_getSurveiesCount = "survey.getSurveiesCount";
    public static final String survey_getQuestionCountAndType = "survey.getQuestionCountAndType";
    public static final String survey_countOptions = "survey.countOptions";

    public final static String SUBMITORGROLE = "申报单位";

    public static final int ORG_PBC_TYPE = 2;
    public static final int ORG_FINANCIAL_TYPE = 1;

    public static final int BACK_FINANCIAL_DATA = 1;
    public static final int BACK_PBC_DATA = 2;

    public static final String CACHE_APP = "_cache_app";
    public static final String CACHE_APP_ITEM_DEFINIDATA = "cache_app_item_definidata";
    public static final String CACHE_APP_USER_ORG = "cache_app_user_org";

    public static final Integer Value_Inidata_Save_Status = 0;
    public static final Integer Value_Inidata_Submit_Status = 1;
    public static final Integer Value_Inidata_Back_Status = 2;
    public static final Integer Value_Inidata_Handle_Status = 3;

    public static Map<Integer, String> app_status_mapping = Maps.newHashMap();
    public static final String inidata_prefix = "def_inidata.";
    public static final String kpi_prefix = "def_kpi.";

    public static final String OPTION = "option";

    public static final String DICT_DATA_TYPE = "dict_dataType";

    public static final int KPI_STANDARDIZE_TYPE_FORWARD = 1;
    public static final int KPI_STANDARDIZE_TYPE_BACKFORWARD = 2;
    public static final int KPI_STANDARDIZE_TYPE_NOYES = 3;

    static {
        app_status_mapping.put(Value_Inidata_Save_Status, "已保存");
        app_status_mapping.put(Value_Inidata_Submit_Status, "已提交");
        app_status_mapping.put(Value_Inidata_Back_Status, "退回");
        app_status_mapping.put(Value_Inidata_Handle_Status, "已处理");
    }

    public static String getAppStatusShow(Integer status) {
        return app_status_mapping.get(status);
    }
}
