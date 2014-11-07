package com.hwtx.modules.sys.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hwtx.modules.sys.dao.UserDao;
import com.hwtx.modules.sys.entity.SysFunctionPermission;
import com.hwtx.modules.sys.entity.SysUser;
import com.hwtx.modules.sys.utils.UserUtils;
import com.hwtxframework.ioc.annotation.Component;
import com.hwtxframework.ioc.annotation.Dependon;
import com.jfinal.plugin.ehcache.CacheKit;
import com.thinkgem.jeesite.common.Constants;
import com.thinkgem.jeesite.common.config.Global;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

@Component
@Dependon("jFinalCache")
public class MenuService {

    @Resource
    private UserDao userDao;
    @Resource
    private FunctionService functionService;

    public List<SysFunctionPermission> getMenuList(SysUser user) {

        List<SysFunctionPermission> menuList;
        if (user.isAdmin()) {
            menuList = getAllMenu();
        } else {
            menuList = getMenuByUserId(user);
        }
        user.setMenus(menuList);
        return menuList;
    }

    private void loadMenus() {
        buildAllMenus();
        getMenuList(UserUtils.getUser());
    }

    @PostConstruct
    public void buildAllMenus() {
        List<SysFunctionPermission> allMenus = functionService.getAllMenusRecursive();
        Map<Integer, SysFunctionPermission> result = Maps.newHashMap();
        for (SysFunctionPermission sysFunctionPermission : allMenus) {
            result.put(sysFunctionPermission.getPermissionId(), sysFunctionPermission);
        }
        CacheKit.put(Constants.SYSCACHE, Constants.CACHE_ALL_MENU_FUNPERMISSION_LIST, allMenus);
        CacheKit.put(Constants.SYSCACHE, Constants.CACHE_ALL_MENU_FUNPERMISSION_MAP, result);
    }


    public List<SysFunctionPermission> getAllMenu() {
        return CacheKit.get(Constants.SYSCACHE, Constants.CACHE_ALL_MENU_FUNPERMISSION_LIST);
    }

    public List<SysFunctionPermission> getMenuByUserId(SysUser user) {

        List<SysFunctionPermission> menus = user.getMenus();
        if (menus != null && menus.size() != 0) {
            return menus;
        }
        Set<Integer> funs = user.getFunctions();
        List<SysFunctionPermission> allMenus = CacheKit.get(Constants.SYSCACHE,
                Constants.CACHE_ALL_MENU_FUNPERMISSION_LIST);

        List<SysFunctionPermission> result = Lists.newArrayListWithCapacity(funs.size());
        for (SysFunctionPermission sysFunctionPermission : allMenus) {
            if (funs.contains(sysFunctionPermission.getPermissionId())) {
                result.add(sysFunctionPermission);
            }
        }
        return result;
    }

    public void sortPermission(String[] ids, Integer[] squences) {
        Map<String, Integer> map = Maps.newHashMap();
        for (int i = 0; i < ids.length; i++) {
            map.put(ids[i], squences[i]);
        }
        userDao.updateFuncPermissionsSort(map);
        loadMenus();
    }

    public SysFunctionPermission getMenuById(String id) {
        Map<Integer, SysFunctionPermission> menus = CacheKit.get(Constants.SYSCACHE,
                Constants.CACHE_ALL_MENU_FUNPERMISSION_MAP);
        return menus.get(Integer.parseInt(id));
    }

    @SuppressWarnings("unchecked")
    public void saveMenu(SysFunctionPermission menu) {
        if (menu.getPermissionId() == null) {
            menu.prePersist();
        } else {
            menu.preUpdate();
        }
        functionService.saveFunctionPermission(menu);
        loadMenus();
    }

    public void deleteMenu(String id) {
        functionService.deleteWithChildren(id);
        loadMenus();
    }

    public List<Map<String, Object>> getMenuTree(String selectId) {
        List<Map<String, Object>> mapList = Lists.newArrayList();
        Map<String, Object> map = Maps.newHashMap();
        map.put("id", Constants.TOPFUNCTIONID);
        map.put("parent", "#");
        map.put("text", Constants.TOPMENUNAME);
        Map<String, Object> state = Maps.newHashMap();
        state.put("opened", true);
        map.put("state", state);
        mapList.add(map);
        buildMenuTree(UserUtils.getUser().getMenus(), mapList, selectId);
        return mapList;
    }

    private void buildMenuTree(List<SysFunctionPermission> menus, List<Map<String, Object>> mapList, String selectId) {
        for (SysFunctionPermission functionPermission : menus) {
            Map<String, Object> map = Maps.newHashMap();
            map.put("id", functionPermission.getId());
            map.put("parent", functionPermission.getPid());
            map.put("text", functionPermission.getName());
            Map<String, Object> state = Maps.newHashMap();
            state.put("opened", true);
            map.put("state", state);
            if (functionPermission.getPermissionId().toString().equals(selectId)) {
                map.put("selected", true);
            }
            mapList.add(map);
        }
    }

    public String buildLeftMenu(String contextPath, List<SysFunctionPermission> menus) {
        Stack<MenuItem> stack = new Stack<>();
        StringBuilder builder = new StringBuilder();
        for (SysFunctionPermission menu : menus) {

            MenuItem menuItem = null;
            if (!stack.isEmpty()) {
                menuItem = stack.peek();
            }

            while (menuItem != null && menuItem.item.getPermissionId() != menu.getPid()) {
                if (!stack.isEmpty()) {
                    MenuItem out = stack.pop();
                    if (out.enable) {
                        if (builder.lastIndexOf("<ul>") != builder.length() - 4) {
                            builder.append("</ul>");
                        } else {
                            builder.delete(builder.length() - 4, builder.length());
                        }
                        builder.append("</li>");
                    }
                    if (!stack.isEmpty()) {
                        menuItem = stack.peek();
                    } else {
                        menuItem = null;
                        break;
                    }
                } else {
                    break;
                }
            }


            if (menuItem != null) {
                if (!menuItem.enable) {
                    stack.push(new MenuItem(menu, false));
                    continue;
                }
            } else {
                if (menu.getEnable().equals("0")) {
                    stack.push(new MenuItem(menu, false));
                    continue;
                }
            }

            stack.push(new MenuItem(menu, !menu.getEnable().equals("0")));

            if (menu.isTop()) {
                builder.append("<li class=\"active\">");
            } else {
                builder.append("<li>");
            }

            if (menu.getEnable().equals("1")) {
                buildMenuItem(menu, builder, contextPath);
                builder.append("<ul>");
            }
        }

        while (!stack.isEmpty()) {
            MenuItem out = stack.pop();
            if (out.enable) {
                if (builder.lastIndexOf("<ul>") != builder.length() - 4) {
                    builder.append("</ul>");
                } else {
                    builder.delete(builder.length() - 4, builder.length());
                }
                builder.append("</li>");
            }
        }
        return builder.toString();
    }

    @AllArgsConstructor
    class MenuItem {
        SysFunctionPermission item;
        boolean enable;
    }

    private void buildMenuItem(SysFunctionPermission menu, StringBuilder builder, String contextPath) {

        String url = "#";
        if (!"#".equals(menu.getUrl())) {
            url = contextPath + Global.getAdminPath() + menu.getUrl();
        }
        builder.append("<a url=\"" + url + "\" title=\"" + menu.getName() + "\" href=\"#\">");

        if (StringUtils.isNotEmpty(menu.getIcon())) {
            if (menu.isTop()) {
                builder.append("<i class=\"fa fa-lg fa-fw ");
            } else {
                builder.append("<i class=\"fa fa-fw ");
            }
            builder.append(menu.getIcon());
            builder.append("\"></i>");
        }
        if (menu.isTop()) {
            builder.append("<span class=\"menu-item-parent\">");
            builder.append(menu.getName());
            builder.append("</span>");
        } else {
            builder.append(" " + menu.getName());
        }
        builder.append("</a>");
    }
}