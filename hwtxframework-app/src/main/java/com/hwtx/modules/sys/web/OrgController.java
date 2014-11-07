package com.hwtx.modules.sys.web;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.hwtx.framework.interceptor.ModuleIocInterceptor;
import com.hwtx.modules.sys.entity.SysOrg;
import com.hwtx.modules.sys.service.OrgService;
import com.hwtx.modules.sys.service.ServiceResponse;
import com.hwtx.modules.sys.validator.SysOrgValidator;
import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.thinkgem.jeesite.common.Constants;
import com.thinkgem.jeesite.common.web.BaseController;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

@Before(ModuleIocInterceptor.class)
@ControllerBind(controllerKey = "${adminPath}/sys/org")
public class OrgController extends BaseController {

    @Resource
    private OrgService orgService;

    @ActionKey(value = {"list", "/"})
    public void list() {
        render("/modules/sys/orgList.jsp");
    }

    @ActionKey(value = "showAssignRole")
    public void showAssignRole() {
        String id = getPara("orgId");
        setAttr("orgId", id);
        Map<String, String> roles = orgService.getOrgRoles(id);
        setAttr("roles", roles);
        setAttr("roleIds", Joiner.on(",").join(roles.keySet()));
        render("/modules/sys/orgAssignRole.jsp");
    }

    @ActionKey(value = "assignRole")
    public void assignRole() {
        String orgId = getPara("orgId");
        String roles = getPara("roles");
        orgService.assignRoles(orgId, roles);
        renderJson(getRenderJson(Constants.RENDER_JSON_SUCCESS, "保存成功!"));
    }

    @ActionKey(value = "save")
    @Before(SysOrgValidator.class)
    public void save() {
        SysOrg sysOrg = getModel(SysOrg.class);
        String save_type = getPara("save.type");
        orgService.save(sysOrg, save_type);
        Map<String, String> result = Maps.newHashMap();
        result.put("code", Constants.RENDER_JSON_SUCCESS);
        result.put("id", sysOrg.getOrgId());
        result.put("name", sysOrg.getName());
        result.put("message", "更新机构'" + sysOrg.getName() + "'成功");
        renderJson(JSON.toJSONString(result));
    }

    @ActionKey(value = "detail")
    public void getDetail() {
        String orgId = getPara("orgId");
        SysOrg sysOrg = orgService.getOrg(orgId);
        if (sysOrg != null) {
            renderJson("{\"orgId\":\"" + sysOrg.getOrgId() + "\",\"name\":\"" + sysOrg.getName() + "\",\"des\":\"" + sysOrg.getDescription() + "\"}");
        }
    }

    @ActionKey(value = "assignUser")
    public void assignUser() {
        String userIds = getPara("userIds");
        String orgId = getPara("orgId");
        String[] uIds = userIds.split(",");
        if (uIds != null && uIds.length != 0) {
            Long count = orgService.assignUser(uIds, orgId);
            Map<String, Object> param = Maps.newHashMap();
            param.put("code", Constants.RENDER_JSON_SUCCESS);
            param.put("message", "分配成功!");
            param.put("userCount", count);
            renderJson(getRenderJson(param));
        } else {
            getResponse().setStatus(HttpServletResponse.SC_BAD_REQUEST);
            renderJson(getRenderJson(Constants.RENDER_JSON_ERROR, "分配失败!"));
        }
    }

    @ActionKey(value = "delAssignedUser")
    public void delAssignedUser() {
        String userIds = getPara("userIds");
        String orgId = getPara("orgId");
        String[] uIds = userIds.split(",");
        if (uIds != null && uIds.length != 0) {
            Long count = orgService.delAssignedUser(uIds, orgId);
            Map<String, Object> param = Maps.newHashMap();
            param.put("code", Constants.RENDER_JSON_SUCCESS);
            param.put("message", "操作成功!");
            param.put("userCount", count);
            renderJson(getRenderJson(param));
        } else {
            getResponse().setStatus(HttpServletResponse.SC_BAD_REQUEST);
            renderJson(getRenderJson(Constants.RENDER_JSON_ERROR, "提交的用户无效!"));
        }
    }

    @ActionKey(value = "move")
    public void move() {
        String _old_parent = getPara("_old_parent");
        String _new_parent = getPara("_new_parent");
        String _moved_id = getPara("_moved_id");
        ServiceResponse serviceResponse = orgService.move(_moved_id, _old_parent, _new_parent);
        renderJson(serviceResponse.json());
    }

    @ActionKey(value = "delete")
    public void delete() {

        String ids = getPara("ids");
        if (StringUtils.isEmpty(ids)) {
            getResponse().setStatus(HttpServletResponse.SC_FORBIDDEN);
            renderJson(getRenderJson(Constants.RENDER_JSON_ERROR, "无法机构!"));
        } else {
            Collection<String> orgIds = orgService.delete(Arrays.asList(ids.split(",")));
            if (orgIds.size() != 0) {
                renderJson(getRenderJson(Constants.RENDER_JSON_ERROR, Joiner.on(",").join(orgIds)));
            } else {
                renderJson(getRenderJson(Constants.RENDER_JSON_SUCCESS, "删除成功!"));
            }
        }
    }

    @ActionKey(value = "treeData")
    public void treeData() {
        String pid = getPara("id");
        String jsonTree = orgService.getOrgTree(pid);
        renderJson(jsonTree);
    }
}
