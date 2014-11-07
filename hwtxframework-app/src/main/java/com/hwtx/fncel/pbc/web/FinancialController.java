package com.hwtx.fncel.pbc.web;

import com.hwtx.modules.sys.service.RegionService;
import com.hwtx.fncel.pbc.entity.AppOrg;
import com.hwtx.fncel.pbc.entity.FinancialOrg;
import com.hwtx.fncel.pbc.service.AppOrgService;
import com.hwtx.fncel.pbc.exception.ExistException;
import com.hwtx.fncel.pbc.service.FinancialService;
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
import java.util.Map;

@Before(ModuleIocInterceptor.class)
@ControllerBind(controllerKey = "${adminPath}/app/financial")
public class FinancialController extends BaseController {

    @Resource
    private FinancialService financialService;
    @Resource
    private AppOrgService appOrgService;
    @Resource
    private RegionService regionService;

    @ActionKey(value = {"user"})
    public void user() {
        render("/app/financial/financialUserList.jsp");
    }

    @Override
    protected Page1<Map<String, Object>> getPageData(int iDisplayStart, int fcount) {
        SearchSysUser searchSysUser = getModel(SearchSysUser.class);
        AppOrg appOrg = appOrgService.getAppOrg();
        Integer region = null;
        String type = null;
        if (appOrg.getArea() != null) {
            region = appOrg.getArea();
            type = "area";
        } else {
            region = appOrg.getCity();
            type = "city";
        }
        return financialService.getFinancialUsers(searchSysUser, type, region, iDisplayStart, fcount);
    }

    @ActionKey(value = "financial_data")
    public void financial_data() {
        String name = getPara("name");
        SysUser sysUser = UserUtils.getUser();
        int offset = getOffset();
        int count = getPageCount(offset);
        Page1<FinancialOrg> page1 = financialService.getFinancialOrgs(name, sysUser, offset, count);
        sortAndSend(page1);
    }


    @ActionKey(value = "showAddUser")
    public void showAddUser() {
        Page1<FinancialOrg> page = financialService.getFinancialOrgs("", UserUtils.getUser(), -1, -1);
        setAttr("orgs", page.getList());
        render("/app/financial/financialUserForm.jsp");
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
            financialService.updateUser(sysUser, userOrg);
        } else {
            sysUser.setUserId(IdGen.uuid());
            try {
                financialService.saveUser(sysUser, userOrg);
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
        SysUser sysUser = financialService.getUser(userId);
        Page1<FinancialOrg> page = financialService.getFinancialOrgs("", UserUtils.getUser(), -1, -1);
        setAttr("orgs", page.getList());
        setAttr("userOrg", appOrgService.getAppOrgByUserId(sysUser.getUserId()));
        setAttr("sysUser", sysUser);
        render("/app/financial/financialUserForm.jsp");
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
                financialService.deleteUser(new String[]{id}, userOrg);
            }
        } else {
            message = "无法获得用户标识";
            code = Constants.RENDER_JSON_ERROR;
        }
        renderJson(getRenderJson(code, message));
    }

    @ActionKey(value = {"orgs"})
    public void orgs() {
        render("/app/financial/financials_list.jsp");
    }

    @ActionKey(value = {"showAddFinancial"})
    public void showAddFinancial() {
        render("/app/financial/financialForm.jsp");
    }

    @ActionKey(value = {"showEditFinancial"})
    public void showEditFinancial() {
        setAttr("provinces", regionService.getProvinces());
        FinancialOrg financialOrg = financialService.getFinancialOrg(getPara("id"));
        if (financialOrg != null) {
            setAttr("financialOrg", financialOrg);
        }
        render("/app/financial/financialForm.jsp");
    }

    @ActionKey(value = {"save"})
    public void save() throws Throwable {
        FinancialOrg financialOrg = getModel(FinancialOrg.class);
        AppOrg appOrg = appOrgService.getAppOrg();
        financialOrg.setProvince(appOrg.getProvince());
        financialOrg.setCity(appOrg.getCity());
        financialOrg.setArea(appOrg.getArea());
        try {
            financialService.save(financialOrg);
        } catch (ExistException e) {
            renderJson(getRenderJson(Constants.RENDER_JSON_ERROR, "金融机构已经存在"));
            throw e;
        } catch (Exception e) {
            renderJson(getRenderJson(Constants.RENDER_JSON_ERROR, e.getMessage()));
            throw e;
        }
        renderJson(getRenderJson(Constants.RENDER_JSON_SUCCESS, "保存成功"));
    }

    @ActionKey(value = {"deleteFinancial"})
    public void deleteFinancial() {
        String ids = getPara("ids");
        try {
            financialService.deleteFinancialOrg(ids);
        } catch (Exception e) {
            renderJson(getRenderJson(Constants.RENDER_JSON_ERROR, e.getMessage()));
            throw e;
        }
        renderJson(getRenderJson(Constants.RENDER_JSON_SUCCESS, "删除成功"));
    }
}