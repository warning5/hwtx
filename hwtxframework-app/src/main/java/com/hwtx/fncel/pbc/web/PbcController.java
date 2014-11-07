package com.hwtx.fncel.pbc.web;

import com.google.common.collect.Lists;
import com.hwtx.modules.sys.service.RegionService;
import com.hwtx.fncel.pbc.entity.AppDataCheck;
import com.hwtx.fncel.pbc.entity.AppOrg;
import com.hwtx.fncel.pbc.entity.AppPbcInfo;
import com.hwtx.fncel.pbc.exception.ExistException;
import com.hwtx.fncel.pbc.service.*;
import com.hwtx.fncel.pbc.util.AppConstants;
import com.hwtx.fncel.pbc.vo.ComputeValue;
import com.hwtx.fncel.pbc.vo.SubmitKpiVo;
import com.hwtx.fncel.util.FncelUtils;
import com.hwtx.framework.interceptor.ModuleIocInterceptor;
import com.hwtx.modules.sys.entity.SysUser;
import com.hwtx.modules.sys.service.SystemService;
import com.hwtx.modules.sys.utils.UserUtils;
import com.hwtx.modules.sys.vo.SearchSysUser;
import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page1;
import com.thinkgem.jeesite.common.Constants;
import com.thinkgem.jeesite.common.utils.IdGen;
import com.thinkgem.jeesite.common.web.BaseController;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Before(ModuleIocInterceptor.class)
@ControllerBind(controllerKey = "${adminPath}/app/pbc")
public class PbcController extends BaseController {

    @Resource
    private PbcService pbcService;
    @Resource
    private RegionService regionService;
    @Resource
    private AppOrgService appOrgService;

    @ActionKey(value = {"user"})
    public void user() {
        render("/app/pbc/pbcUserList.jsp");
    }

    @ActionKey(value = {"orgs"})
    public void orgs() {
        setAttr("roleId", getPara("roleId"));
        render("/app/pbc/pbc_orgs.jsp");
    }

    @ActionKey(value = {"add"})
    public void add() {
        setAttr("provinces", regionService.getProvinces());
        AppOrg appOrg = appOrgService.getAppOrg();
        AppPbcInfo appPbcInfo = new AppPbcInfo();
        appPbcInfo.setProvince(appOrg.getProvince());
        appPbcInfo.setCity(appOrg.getCity());
        setAttr("appPbcInfo", appPbcInfo);
        setAttr("cities", regionService.getCities(appPbcInfo.getProvince()));
        setAttr("areas", regionService.getAreas(appPbcInfo.getCity()));
        setAttr("roleId", getPara("roleId"));
        render("/app/pbc/pbcOrgForm.jsp");
    }

    @ActionKey(value = {"show"})
    public void show() {
        setAttr("provinces", regionService.getProvinces());
        AppPbcInfo appPbcInfo = pbcService.geAppPbcInfo(getPara("id"));
        if (appPbcInfo != null) {
            setAttr("appPbcInfo", appPbcInfo);
            setAttr("name", appPbcInfo.get("name"));
            setAttr("cities", regionService.getCities(appPbcInfo.getProvince()));
            setAttr("areas", regionService.getAreas(appPbcInfo.getCity()));
        }
        render("/app/pbc/pbcOrgForm.jsp");
    }

    @ActionKey(value = {"save"})
    public void save() throws Throwable {
        AppPbcInfo appPbcInfo = getModel(AppPbcInfo.class);
        try {
            AppOrg appOrg = appOrgService.getAppOrg();
            appPbcInfo.setProvince(appOrg.getProvince());
            appPbcInfo.setCity(appOrg.getCity());
            pbcService.savePbcInfo(appPbcInfo, getPara("roleId"), getPara("name"));
        } catch (ExistException e) {
            renderJson(getRenderJson(Constants.RENDER_JSON_ERROR, "机构已存在"));
            throw e;
        } catch (Exception e) {
            renderJson(getRenderJson(Constants.RENDER_JSON_ERROR, e.getMessage()));
            throw e;
        }
        renderJson(getRenderJson(Constants.RENDER_JSON_SUCCESS, "保存成功"));
    }

    @ActionKey(value = {"delete"})
    public void delete() {
        String ids = getPara("ids");
        try {
            pbcService.deletePbc(ids);
        } catch (Exception e) {
            renderJson(getRenderJson(Constants.RENDER_JSON_ERROR, e.getMessage()));
            throw e;
        }
        renderJson(getRenderJson(Constants.RENDER_JSON_SUCCESS, "删除成功"));
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Page1<AppPbcInfo> getPageData(int iDisplayStart, int fcount) {
        String name = getPara("name");
        SysUser sysUser = UserUtils.getUser();
        return pbcService.getPbcOgs(name, sysUser, iDisplayStart, fcount);
    }

    @ActionKey(value = "user_data")
    public void user_data() {
        SearchSysUser searchSysUser = getModel(SearchSysUser.class);
        AppOrg appOrg = appOrgService.getAppOrg();
        Integer region = null;
        String type = null;
        if (appOrg.getArea() != null) {
            region = appOrg.getArea();
            type = "area";
        } else if (appOrg.getCity() != null) {
            region = appOrg.getCity();
            type = "city";
        }
        int offset = getOffset();
        int count = getPageCount(offset);
        Page1<Map<String, Object>> page1 = null;
        if (region != null) {
            page1 = pbcService.getPbcUsers(searchSysUser, type, region, offset, count);
        }
        if (page1 == null) {
            List<Map<String, Object>> list = Lists.newArrayList();
            page1 = new Page1<>(list, list.size());
        }
        sortAndSendWithDateFormat(page1, "yyyy-MM-dd HH:mm:ss");
    }

    @ActionKey(value = "showAddUser")
    public void showAddUser() {
        Page1<AppPbcInfo> page = pbcService.getPbcOgs("", UserUtils.getUser(), -1, -1);
        setAttr("orgs", page.getList());
        render("/app/pbc/pbcUserForm.jsp");
    }

    @ActionKey(value = "saveUser")
    public void saveUser() {
        SysUser sysUser = getModel(SysUser.class);
        String userOrg = getPara("userOrg");
        if (StrKit.isBlank(userOrg)) {
            renderJson(getErrorRenderJson("必须选择机构", HttpServletResponse.SC_BAD_REQUEST));
            return;
        }
        if (sysUser.getUserId() != null) {
            if (sysUser.getPwd() != null) {
                sysUser.setPwd(SystemService.entryptPassword(sysUser.getPwd()));
            }
            pbcService.updateUser(sysUser, userOrg);
        } else {
            sysUser.setUserId(IdGen.uuid());
            try {
                pbcService.saveUser(sysUser, userOrg);
            } catch (ExistException e) {
                logger.error("{}", e);
                renderJson(getRenderJson(Constants.RENDER_JSON_ERROR, "用户名称已存在"));

            }
        }
        renderJson(getRenderJson(Constants.RENDER_JSON_SUCCESS, "保存成功"));
    }

    @ActionKey(value = "showEditUser")
    public void showEditUser() {
        String userId = getPara("id");
        SysUser sysUser = pbcService.getUser(userId);
        Page1<AppPbcInfo> page = pbcService.getPbcOgs("", UserUtils.getUser(), -1, -1);
        setAttr("orgs", page.getList());
        setAttr("userOrg", appOrgService.getAppOrgByUserId(sysUser.getUserId()));
        setAttr("sysUser", sysUser);
        render("/app/pbc/pbcUserForm.jsp");
    }

    /**
     * 只支持删除单个用户
     */
    @ActionKey(value = "deleteUser")
    public void deleteUser() {
        String id = getPara("ids");
        String userOrg = getPara("userOrg");
        String message = "删除用户成功";
        String code = Constants.RENDER_JSON_SUCCESS;
        if (StringUtils.isNotEmpty(id)) {
            if (UserUtils.getUser().getUserId().equals(id)) {
                message = "删除[" + id + "]失败, 不允许删除当前用户";
                code = Constants.RENDER_JSON_ERROR;
            } else if (SysUser.isAdmin(id)) {
                message = "删除" + id + "失败, 不允许删除超级管理员用户";
                code = Constants.RENDER_JSON_ERROR;
            } else {
                pbcService.deleteUser(new String[]{id}, userOrg);
            }
        } else {
            message = "无法获得用户标识";
            code = Constants.RENDER_JSON_ERROR;
        }
        renderJson(getRenderJson(code, message));
    }

    @Resource
    private DataComputeService dataComputeService;

    @ActionKey(value = "checkKpi")
    public void checkKpi() {
        render("/app/pbc/kpi_list.jsp");
    }

    @ActionKey(value = "kpi_data")
    public void kpi_data() {
        Integer region = appOrgService.getRegion();
        int offset = getOffset();
        int count = getPageCount(offset);
        Page1<SubmitKpiVo> page1 = null;
        if (region != null) {
            page1 = dataComputeService.getCityKpiData(offset, count, region, AppConstants.Value_Inidata_Submit_Status,
                    AppConstants.Value_Inidata_Handle_Status);
        }
        if (page1 == null) {
            List<SubmitKpiVo> list = Lists.newArrayList();
            page1 = new Page1<>(list, list.size());
        }
        sortAndSend(page1);
    }

    @ActionKey(value = "viewKpi")
    public void viewKpi() {
        Integer region = getParaToInt("region");
        String date = getPara("date");
        Date startdate = null;
        try {
            startdate = FncelUtils.getDate(date);
        } catch (ParseException e) {
            renderHtml("选择的日期不正确");
            logger.error("{}", e);
            return;
        }
        List<ComputeValue> computeValues = pbcService.getKpiData(region, startdate);
        setAttr("result", computeValues);
        setAttr("date", date);
        setAttr("region", region);
        render("/app/pbc/computeTable.jsp");
    }

    @ActionKey(value = "viewIniData")
    public void viewIniData() {
        Integer region = getParaToInt("region");
        String date = getPara("date");
        Date startdate = null;
        try {
            startdate = FncelUtils.getDate(date);
        } catch (ParseException e) {
            renderHtml("选择的日期不正确");
            logger.error("{}", e);
            return;
        }
        setAttr("result", pbcService.getKpiInidata(region, startdate, getPara("kpiId")));
        render("/app/pbc/kpiIniData.jsp");
    }

    @Resource
    private DataInputService dataInputService;

    @ActionKey(value = {"showBack"})
    public void showBack() {
        for (Map.Entry<String, String[]> entry : getParaMap().entrySet()) {
            setAttr(entry.getKey(), entry.getValue()[0]);
        }
        try {
            if (!StrKit.isBlank(getPara("view"))) {
                AppOrg appOrg = appOrgService.getAppOrg();
                setAttr("checkNotes", dataInputService.getBackData(appOrg.getOrgId(), getPara("date"),
                        appOrgService.getRegion(appOrg), AppConstants.BACK_PBC_DATA));
            }
        } catch (Throwable throwable) {
            logger.error("{}", throwable);
        }
        render("/app/pbc/back.jsp");
    }

    @ActionKey(value = {"backKpi"})
    public void backKpi() {
        AppDataCheck appDataCheck = getModel(AppDataCheck.class);
        String pdate = getPara("date");
        try {
            Date date = FncelUtils.getDate(pdate);
            pbcService.backData(appDataCheck, date, appOrgService.getAppOrg().getOrgId());
            renderJson(getRenderJson(Constants.RENDER_JSON_SUCCESS, "退回成功"));
        } catch (ParseException e) {
            logger.error("{}", e);
            renderJson(getErrorRenderJson("日期解析错误" + pdate, HttpServletResponse.SC_BAD_REQUEST));
            return;
        }

    }
}