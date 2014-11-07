package com.hwtx.modules.sys.web;

import com.alibaba.fastjson.JSON;
import com.hwtx.framework.interceptor.ModuleIocInterceptor;
import com.hwtx.modules.sys.entity.SysFunctionPermission;
import com.hwtx.modules.sys.service.MenuService;
import com.hwtx.modules.sys.utils.UserUtils;
import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.web.BaseController;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;

/**
 * 菜单Controller
 */
@Before(ModuleIocInterceptor.class)
@ControllerBind(controllerKey = "${adminPath}/sys/menu")
public class MenuController extends BaseController {

    @Resource
    private MenuService menuService;

    @ActionKey(value = {"list", "/"})
    public void list() {
        setAttr("list", UserUtils.getUser().getMenus());
        render("/modules/sys/menuList.jsp");
    }

    @ActionKey(value = "form")
    public void form() {
        String id = getPara("id");
        String pid = getPara("parent.id");
        SysFunctionPermission functionPermission = null;
        if (StringUtils.isNotEmpty(pid)) {
            functionPermission = new SysFunctionPermission();
            functionPermission.setParent(menuService.getMenuById(pid));
        } else {
            if(StringUtils.isNotEmpty(id)){
                functionPermission = menuService.getMenuById(id);
            }
        }
        setAttr("menu", functionPermission);
        render("/modules/sys/menuForm.jsp");
    }

    @ActionKey(value = "save")
    public void save() {
        SysFunctionPermission menu = getModel(SysFunctionPermission.class);
        menu.setMenu("1");
        menuService.saveMenu(menu);
        addMessage("保存菜单'" + menu.getName() + "'成功");
        redirect(Global.getAdminPath() + "/sys/menu/");
    }

    @ActionKey(value = "delete")
    public void delete() {
        if (SysFunctionPermission.isRootMenu(Integer.valueOf(getPara("id")))) {
            addMessage("删除菜单失败, 不允许删除顶级菜单或编号为空");
        } else {
            menuService.deleteMenu(getPara("id"));
            addMessage("删除菜单成功");
        }
        redirect(Global.getAdminPath() + "/sys/menu/");
    }

    @ActionKey(value = "tree")
    public void tree() {

//        String id = getPara("parentId");
//        SysFunctionPermission sysFunctionPermission = menuService.getMenuById(id);
//        if (sysFunctionPermission != null) {
//            setAttr("menu_List", sysFunctionPermission.getChildren());
//        }
        render("/modules/sys/menuTree.jsp");
    }

    @ActionKey(value = "all")
    public void all() {
        renderText(menuService.buildLeftMenu(getRequest().getContextPath(), menuService.getMenuList(UserUtils.getUser())));
    }

    @ActionKey(value = "updateSort")
    public void updateSort() {

        String[] ids = getParaValues("ids");
        menuService.sortPermission(ids, getParaValuesToInt("sequence"));
        addMessage("保存菜单排序成功!");
        redirect(Global.getAdminPath() + "/sys/menu/");
    }

    @ActionKey(value = "treeData")
    public void treeData() {
        String selectId = getPara("selectIds");
        renderJson(JSON.toJSONString(menuService.getMenuTree(selectId)));
    }
}