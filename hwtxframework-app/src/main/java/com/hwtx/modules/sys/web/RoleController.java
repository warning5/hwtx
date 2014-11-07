package com.hwtx.modules.sys.web;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hwtx.fncel.pbc.exception.ExistException;
import com.hwtx.framework.interceptor.ModuleIocInterceptor;
import com.hwtx.modules.sys.entity.SysRole;
import com.hwtx.modules.sys.service.RoleService;
import com.hwtx.modules.sys.service.ServiceResponse;
import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.thinkgem.jeesite.common.Constants;
import com.thinkgem.jeesite.common.web.BaseController;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 角色Controller
 */
@Before(ModuleIocInterceptor.class)
@ControllerBind(controllerKey = "${adminPath}/sys/role")
public class RoleController extends BaseController {

    @Resource
    private RoleService roleService;

    @ActionKey(value = {"list", "/"})
    public void list() {
        String message = getAttrForStr(Constants.WEB_MESSAGE);
        if (StringUtils.isNotEmpty(message)) {
            addMessage(message);
        }
        render("/modules/sys/role/roleList.jsp");
    }

    @ActionKey(value = "detail")
    public void getDetail() {
        String roleId = getPara("roleId");
        String pid = getPara("pid");
        String text = getPara("text");
        SysRole role = roleService.getRole(roleId, pid, text);
        setAttr("sysRole", role);
        render("/modules/sys/role/roleForm.jsp");
    }

    @ActionKey(value = {"tree"})
    public void tree() {
        List<SysRole> roles = roleService.getRoles();
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append("\"id\"");
        builder.append(":");
        builder.append("\"" + Constants.TOPROLEID + "\"");
        builder.append(",");
        builder.append("\"text\"");
        builder.append(":");
        builder.append("\"" + Constants.TOPROLENAME + "\"");
        builder.append(",");
        builder.append("\"state\"");
        builder.append(": {");
        builder.append("\"opened\"");
        builder.append(":");
        builder.append("\"true\"");
        builder.append(",");
        builder.append("\"selected\"");
        builder.append(":");
        builder.append("\"true\"");
        builder.append("}");
        builder.append(",");
        builder.append("\"children\"");
        builder.append(":");
        builder.append("[");
        buildRoleTree(roles, builder);
        builder.append("]");
        builder.append("}");

        renderJson(builder.toString());
    }

    private void buildRoleTree(List<SysRole> roles, StringBuilder builder) {
        for (int i = 0; i < roles.size(); i++) {
            SysRole sysRole = roles.get(i);
            builder.append("{");
            builder.append("\"id\"");
            builder.append(":");
            builder.append("\"" + sysRole.getRoleId() + "\"");
            builder.append(",");
            builder.append("\"text\"");
            builder.append(":");
            builder.append("\"" + sysRole.getName() + "\"");
            if (sysRole.getType().equals("1")) {
                // 角色不显示图标
                builder.append(",");
                builder.append("\"icon\"");
                builder.append(":");
                builder.append("false");
            }
            if (sysRole.getChildRoles().size() != 0) {
                builder.append(",");
                builder.append("\"children\"");
                builder.append(":");
                builder.append("[");
                buildRoleTree(sysRole.getChildRoles(), builder);
                builder.append("]");
            }
            builder.append("}");
            if (i + 1 != roles.size()) {
                builder.append(",");
            }
        }
    }

    @ActionKey(value = "move")
    public void move() {
        String _old_parent = getPara("_old_parent");
        String _new_parent = getPara("_new_parent");
        String _moved_id = getPara("_moved_id");
        ServiceResponse serviceResponse = roleService.move(_moved_id,
                _old_parent, _new_parent);
        renderJson(serviceResponse.json());
    }

    @ActionKey(value = "save")
    public void save() {
        SysRole sysRole = getModel(SysRole.class);
        Map<String, String> result = Maps.newHashMap();
        String saveType = "保存角色";
        if ("0".equals(sysRole.getType())) {
            saveType = "保存分类";
        }
        try {
            roleService.saveRole(sysRole);
            result.put("code", Constants.RENDER_JSON_SUCCESS);
            result.put("id", sysRole.getRoleId());
            result.put("name", sysRole.getName());
            result.put("type", sysRole.getType());
            result.put("message", saveType + "'" + sysRole.getName() + "'成功");
        } catch (ExistException e) {
            result.put("code", Constants.RENDER_JSON_ERROR);
            result.put("message", saveType + "'" + sysRole.getName() + "'失败, 角色名已存在");
        }
        renderJson(JSON.toJSONString(result));
    }

    @ActionKey(value = "delete")
    public void delete() {
        Collection<String> unDelIds = roleService.deleteRole(getPara("id"));
        if (unDelIds.size() == 0) {
            renderText("删除角色成功");
        } else {
            renderText("角色 " + unDelIds.toString() + " 无法删除,已被使用!");
        }
    }

    @ActionKey(value = "assign")
    public void assign() {
        render("/modules/sys/roleAssign.jsp");
    }

    @ActionKey(value = "assignIt")
    public void assignIt() {
        String ids = getPara("selected");
        String roleId = getPara("roleId");
        if (StringUtils.isNotEmpty(ids) && StringUtils.isNotEmpty(roleId)) {
            roleService.assign(roleId, ids.split(","), getPara("type"));
        }
        renderJson(getRenderJson(Constants.RENDER_JSON_SUCCESS, "分配成功"));
    }

    @ActionKey(value = "deleteIt")
    public void deleteIt() {
        String ids = getPara("selected");
        String roleId = getPara("roleId");
        if (StringUtils.isNotEmpty(ids) && StringUtils.isNotEmpty(roleId)) {
            String[] funs = ids.split(",");
            List<Integer> funIds = Lists.newArrayListWithCapacity(funs.length);
            for (int i = 0; i < funs.length; i++) {
                funIds.add(Integer.valueOf(funs[i]));
            }
            roleService.deAssign(roleId, funIds, getPara("type"));
        }
        renderJson(getRenderJson(Constants.RENDER_JSON_SUCCESS, "删除成功"));
    }

    @ActionKey(value = "menus")
    public void menus() {
        String roleId = getPara("id");
        if (StringUtils.isNotEmpty(roleId)) {
            renderJson(roleService.getMenusAsTreeByRole(roleId));
        } else {
            getResponse().setStatus(HttpServletResponse.SC_BAD_REQUEST);
            renderJson(getRenderJson(Constants.RENDER_JSON_ERROR, "角色ID不能为空."));
        }
    }

    @ActionKey(value = "resources")
    public void resources() {
        String roleId = getPara("id");
        if (StringUtils.isNotEmpty(roleId)) {
            renderJson(roleService.getResourcesAsTreeByRole(roleId));
        } else {
            getResponse().setStatus(HttpServletResponse.SC_BAD_REQUEST);
            renderJson(getRenderJson(Constants.RENDER_JSON_ERROR, "角色ID不能为空."));
        }
    }

    @ActionKey(value = "excludeMenus")
    public void excludeMenus() {
        String roleId = getPara("roleId");
        if (StringUtils.isNotEmpty(roleId)) {
            renderJson(roleService.getExcludeMenusAsTreeByRole(roleId));
        } else {
            getResponse().setStatus(HttpServletResponse.SC_BAD_REQUEST);
            renderJson(getRenderJson(Constants.RENDER_JSON_ERROR, "角色ID不能为空."));
        }
    }

    @ActionKey(value = "excludeResources")
    public void excludeResources() {
        String roleId = getPara("roleId");
        if (StringUtils.isNotEmpty(roleId)) {
            renderJson(roleService.getExcludeResourcesAsTreeByRole(roleId));
        } else {
            getResponse().setStatus(HttpServletResponse.SC_BAD_REQUEST);
            renderJson(getRenderJson(Constants.RENDER_JSON_ERROR, "角色ID不能为空."));
        }
    }

    @ActionKey(value = "sysFunTree")
    public void sysFunTree() {
        setAttr("roleId", getPara("id"));
        setAttr("type", getPara("type"));
        if (getPara("type").equals("menu")) {
            setAttr("excludeUrl", "excludeMenus");
        } else {
            setAttr("excludeUrl", "excludeResources");
        }
        render("/modules/sys/sysfun.jsp");
    }
}