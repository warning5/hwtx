package com.hwtx.fncel.pbc.web;

import com.google.common.collect.Maps;
import com.hwtx.fncel.pbc.entity.DefInidata;
import com.hwtx.fncel.pbc.exception.ExistException;
import com.hwtx.fncel.pbc.service.IniDataService;
import com.hwtx.framework.interceptor.ModuleIocInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page1;
import com.thinkgem.jeesite.common.Constants;
import com.thinkgem.jeesite.common.web.BaseController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Before(ModuleIocInterceptor.class)
@ControllerBind(controllerKey = "${adminPath}/app/inidata")
public class IniDataController extends BaseController {

    @Resource
    private IniDataService iniDataService;

    static Map<String, String> sortMapping = Maps.newHashMap();

    static {
        sortMapping.put("1", "diniDataLabel");
        sortMapping.put("2", "diniDataName");
        sortMapping.put("4", "submitOrgRole");
        sortMapping.put("8", "update_time");
    }

    @Override
    protected String getSortName(String sort) {
        return sortMapping.get(sort);
    }

    @ActionKey(value = {"list"})
    public void list() {
        setAttr("orgRoles", iniDataService.getSubmitOrgRole());
        render("/app/rule/inidata.jsp");
    }

    @ActionKey(value = {"showEditInidata"})
    public void showEditInidata() {
        setAttr("orgRoles", iniDataService.getSubmitOrgRole());
        setAttr("defInidata", iniDataService.getDefInidata(getPara("id")));
        render("/app/rule/inidataForm.jsp");
    }

    @ActionKey(value = {"showAddInidata"})
    public void showAddInidata() {
        setAttr("orgRoles", iniDataService.getSubmitOrgRole());
        render("/app/rule/inidataForm.jsp");
    }

    @ActionKey(value = {"save"})
    public void save() throws Throwable {
        DefInidata defInidata = getModel(DefInidata.class);
        if (defInidata.getDiniDataId() != null) {
            if (defInidata.getDiniDataId().length() < 10) {
                defInidata.setDiniDataId(null);
            }
        }
        try {
            iniDataService.save(defInidata);
        } catch (ExistException e) {
            renderJson(getRenderJson(Constants.RENDER_JSON_ERROR, "数据已存在"));
            throw e;
        } catch (Exception e) {
            renderJson(getRenderJson(Constants.RENDER_JSON_ERROR, e.getMessage()));
            throw e;
        }
        renderJson(getRenderJson(Constants.RENDER_JSON_SUCCESS, "保存成功"));
    }

    @ActionKey(value = {"deleteInidata"})
    public void deleteInidata() {
        String ids = getPara("ids");
        try {
            iniDataService.delete(ids.split(","));
            renderJson(getRenderJson(Constants.RENDER_JSON_SUCCESS, "删除成功"));
        } catch (Exception e) {
            renderJson(getErrorRenderJson(e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
        }
    }

    @Override
    protected Page1<DefInidata> getPageData(int iDisplayStart, int fcount) {
        DefInidata defInidata = getModel(DefInidata.class);
        return iniDataService.findInidataByPage(defInidata, iDisplayStart, fcount);
    }
}