package com.hwtx.fncel.pbc.web;

import com.hwtx.fncel.pbc.entity.AppDataCheck;
import com.hwtx.fncel.pbc.service.AppOrgService;
import com.hwtx.fncel.pbc.service.DataInputService;
import com.hwtx.fncel.pbc.util.AppConstants;
import com.hwtx.fncel.util.FncelUtils;
import com.hwtx.framework.interceptor.ModuleIocInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page1;
import com.thinkgem.jeesite.common.Constants;
import com.thinkgem.jeesite.common.web.BaseController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

@Before(ModuleIocInterceptor.class)
@ControllerBind(controllerKey = "${adminPath}/app/financialData")
public class FinancialDataController extends BaseController {

    @Resource
    private AppOrgService appOrgService;
    @Resource
    private DataInputService dataInputService;

    @ActionKey(value = {"verify"})
    public void verify() {
        setAttr("type", getPara("type"));
        render("/app/input/verify.jsp");
    }

    @Override
    protected Page1<Map<String, Object>> getPageData(int iDisplayStart, int fcount) {
        String type = getPara("type");
        Integer region = appOrgService.getRegion();
        return dataInputService.getFinancialInputDatas(type, region, AppConstants.Value_Inidata_Save_Status,
                iDisplayStart, fcount);
    }

    @ActionKey(value = {"showBack"})
    public void showBack() {
        for (Map.Entry<String, String[]> entry : getParaMap().entrySet()) {
            setAttr(entry.getKey(), entry.getValue()[0]);
        }
        try {
            if (!StrKit.isBlank(getPara("view"))) {
                setAttr("checkNotes", dataInputService.getBackData(getPara("orgId"), getPara("date"),
                        appOrgService.getRegion(), AppConstants.BACK_FINANCIAL_DATA));
            }
        } catch (Throwable throwable) {
            logger.error("{}", throwable);
        }
        render("/app/input/back.jsp");
    }

    @ActionKey(value = {"back"})
    public void back() {
        AppDataCheck appDataCheck = getModel(AppDataCheck.class);
        String type = getPara("type");
        String pdate = getPara("date");
        boolean statistic = false;
        if (getPara("statistic") != null) {
            statistic = getParaToBoolean("statistic");
        }
        try {
            Date date = FncelUtils.getDate(pdate);
            dataInputService.backData(appDataCheck, date, appOrgService.getAppOrg().getOrgId(), AppConstants.BACK_FINANCIAL_DATA);
            dataInputService.updateIniData(appDataCheck.getCheckdRegion(), appDataCheck.getCheckedOrgId(), type, date, statistic);
            renderJson(getRenderJson(Constants.RENDER_JSON_SUCCESS, "退回成功"));
        } catch (ParseException e) {
            logger.error("{}", e);
            renderJson(getErrorRenderJson("日期解析错误" + pdate, HttpServletResponse.SC_BAD_REQUEST));
            return;
        }

    }
}