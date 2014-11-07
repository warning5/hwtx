package com.hwtx.fncel.pbc.web;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.hwtx.modules.sys.service.RegionService;
import com.hwtx.fncel.pbc.entity.AppPbcInfo;
import com.hwtx.fncel.pbc.service.AppOrgService;
import com.hwtx.fncel.pbc.exception.ExistException;
import com.hwtx.framework.interceptor.ModuleIocInterceptor;
import com.hwtx.modules.sys.entity.SysOrg;
import com.hwtx.modules.sys.web.OrgController;
import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.thinkgem.jeesite.common.Constants;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

@Before(ModuleIocInterceptor.class)
@ControllerBind(controllerKey = "${adminPath}/app/org")
public class AppOrgController extends OrgController {

    @Resource
    private AppOrgService appOrgService;
    @Resource
    private RegionService regionService;

    @ActionKey(value = {"list", "/"})
    public void list() {
        render("/app/org/orgList.jsp");
    }

    @ActionKey(value = "save")
    public void save() {
        AppPbcInfo appPbcInfo = getModel(AppPbcInfo.class);
        SysOrg sysOrg = getModel(SysOrg.class);
        Map<String, String> result = Maps.newHashMap();
        try {
            appOrgService.save(appPbcInfo, sysOrg);
            result.put("code", Constants.RENDER_JSON_SUCCESS);
            result.put("message", "更新机构'" + sysOrg.getName() + "'成功");
        } catch (ExistException e) {
            result.put("message", "机构'" + sysOrg.getName() + "'已存在");
        } catch (Exception e) {
            logger.error("{}", e);
            result.put("code", Constants.RENDER_JSON_ERROR);
            result.put("message", "更新机构'" + sysOrg.getName() + "'失败");
        }
        result.put("id", sysOrg.getOrgId());
        result.put("name", sysOrg.getName());
        renderJson(JSON.toJSONString(result));
    }

    @ActionKey(value = "detail")
    public void getDetail() {
        String orgId = getPara("orgId");
        String type = getPara("type");
        String pid = getPara("pid");
        String text = getPara("text");
        setAttr("provinces", regionService.getProvinces());
        AppPbcInfo appPbcInfo = appOrgService.getOrg(orgId, type, pid, text);
        if (appPbcInfo != null) {
            setAttr("appPbcInfo", appPbcInfo);
            setAttr("cities", regionService.getCities(appPbcInfo.getProvince()));
            setAttr("areas", regionService.getAreas(appPbcInfo.getCity()));
        }
        render("/app/org/appOrgForm.jsp");
    }

    @ActionKey(value = "delete")
    public void delete() {
        String ids = getPara("ids");
        String type = getPara("type");
        if (StringUtils.isEmpty(ids)) {
            getResponse().setStatus(HttpServletResponse.SC_FORBIDDEN);
            renderJson(getRenderJson(Constants.RENDER_JSON_ERROR, "无法机构!"));
        } else {
            Collection<String> orgIds = appOrgService.delete(Arrays.asList(ids.split(",")), type);
            if (orgIds.size() != 0) {
                renderJson(getRenderJson(Constants.RENDER_JSON_ERROR, Joiner.on(",").join(orgIds)));
            } else {
                renderJson(getRenderJson(Constants.RENDER_JSON_SUCCESS, "删除成功!"));
            }
        }
    }
}
