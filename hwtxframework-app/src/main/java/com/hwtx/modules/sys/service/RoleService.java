package com.hwtx.modules.sys.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hwtx.fncel.pbc.exception.ExistException;
import com.hwtx.modules.sys.dao.RoleDao;
import com.hwtx.modules.sys.entity.*;
import com.hwtx.modules.sys.service.ServiceResponse.ResponseType;
import com.hwtx.modules.sys.utils.UserUtils;
import com.hwtxframework.ioc.annotation.Component;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;
import com.thinkgem.jeesite.common.Constants;
import com.thinkgem.jeesite.common.utils.IdGen;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class RoleService {

    @Resource
    private RoleDao roleDao;
    @Resource
    private FunctionService functionService;

    public List<SysRole> getRoles() {
        SysUser user = UserUtils.getUser();
        @SuppressWarnings("unchecked")
        List<SysRole> roles = null;
        if (user.isAdmin()) {
            roles = getAllRoles();
        } else {
            roles = getRolesByUserId(user.getUserId());
        }
        return roles;
    }

    public List<SysRole> getAllRoles() {
        return roleDao.getAllRoles();
    }

    public List<SysRole> getRolesByUserId(String userId) {
        return roleDao.getRolesByUserId(userId);
    }

    public SysRole findRoleByName(String name) {
        return roleDao.getRoleByName(name);
    }

    public void saveRole(SysRole sysRole) throws ExistException {
        SysRole findIt = findRoleByName(sysRole.getName());
        if (findIt == null) {
            if (sysRole.getRoleId() == null) {
                sysRole.setRoleId(IdGen.uuid());
                roleDao.save(sysRole);
            } else {
                roleDao.update(sysRole);
            }
        } else {
            if (findIt.getRoleId().equals(sysRole.getRoleId())) {
                roleDao.update(sysRole);
            } else {
                throw new ExistException();
            }
        }
    }

    public SysRole getRole(String roleId, String pid, String text) {
        SysRole sysRole = (SysRole) roleDao.findById(SysRole.dao, roleId);
        if (sysRole == null) {
            sysRole = new SysRole();
            sysRole.setName(text);
            sysRole.setPid(pid);
            sysRole.setRoleId(roleId);
        }
        if (Constants.TOPROLEID.equals(pid)) {
            sysRole.setPname(Constants.TOPORGNAME);
        } else {
            sysRole.setPname(roleDao.getRoleName(pid));
        }
        return sysRole;
    }

    public Collection<String> deleteRole(String id) {
        List<String[]> removing = Lists.newArrayList();
        Map<String, String> assignedRoles = getAssignedRoles();
        List<String> children = roleDao.getChildrenId(id);

        Collection<String> returnValue = Sets.newHashSet();
        for (String child : children) {
            if (!assignedRoles.containsKey(child)) {
                removing.add(new String[]{child});
            } else {
                returnValue.add(assignedRoles.get(id));
            }
        }

        if (removing.size() != 0 && returnValue.size() == 0) {
            roleDao.deleteRoles(removing.toArray(new String[0][0]));
        }
        return returnValue;
    }

    @Before(Tx.class)
    public ServiceResponse move(String id, String from, String to) {

        ServiceResponse serviceResponse = new ServiceResponse();
        if (to.equals(Constants.INVALIDPARENT)) {
            serviceResponse.setMessage("无效的移动位置");
            serviceResponse.setResponseType(ResponseType.ERRPOR);
            return serviceResponse;
        }
        SysRole.dao.setRoleId(id).setPid(to).update();
        serviceResponse.setMessage("保存成功");
        serviceResponse.setResponseType(ResponseType.SUCCESS);
        return serviceResponse;
    }

    public List<SysFunctionPermission> getExcludeFunctionsByRole(
            List<SysFunctionPermission> funs, String roleId) {

        Map<Integer, SysRolePermission> rp = getSysRolePermissions(roleId);
        List<SysFunctionPermission> result = Lists.newArrayList();
        functionService.recursiveExclude(funs, rp, result);
        return result;
    }

    public String getExcludeMenusAsTreeByRole(String roleId) {
        @SuppressWarnings("unchecked")
        List<SysFunctionPermission> sysFunctionPermissions = getExcludeFunctionsByRole(
                (List<SysFunctionPermission>) CacheKit.get(Constants.SYSCACHE,
                        Constants.CACHE_ALL_MENU_FUNPERMISSION_LIST), roleId);
        return JSON.toJSONString(functionService.buildMenuTree(sysFunctionPermissions));
    }

    public String getExcludeResourcesAsTreeByRole(String roleId) {
        List<SysFunctionPermission> sysFunctionPermissions = getExcludeFunctionsByRole(
                functionService.getAllResourcesRecursive(), roleId);
        return JSON.toJSONString(functionService
                .buildResourceTree(sysFunctionPermissions));
    }

    @SuppressWarnings("unchecked")
    public String getMenusAsTreeByRole(String roleId) {
        List<SysFunctionPermission> sysFunctionPermissions = getFunctionsByRole(
                (Map<Integer, SysFunctionPermission>)
                        CacheKit.get(Constants.SYSCACHE, Constants.CACHE_ALL_MENU_FUNPERMISSION_MAP), roleId);
        return JSON.toJSONString(functionService
                .buildMenuTree(sysFunctionPermissions));
    }

    public String getResourcesAsTreeByRole(String roleId) {
        List<SysFunctionPermission> sysFunctionPermissions = getFunctionsByRole(
                functionService.getResourceFunctions(), roleId);
        return JSON.toJSONString(functionService
                .buildResourceTree(sysFunctionPermissions));
    }

    public List<SysFunctionPermission> getFunctionsByRole(Map<Integer, SysFunctionPermission> funs,
                                                          String roleId) {
        Map<Integer, SysRolePermission> rp = getSysRolePermissions(roleId);
        List<SysFunctionPermission> result = Lists.newArrayList();
        for (SysRolePermission sysRolePermission : rp.values()) {
            SysFunctionPermission fun = funs.get(sysRolePermission.getPermissionId());
            if (fun != null) {
                result.add(fun);
            }
        }
        return result;
    }

    public List<SysFunctionPermission> getFunctionsByRole(List<SysFunctionPermission> funs,
                                                          String roleId) {
        Map<Integer, SysRolePermission> maps = getSysRolePermissions(roleId);
        List<SysFunctionPermission> result = Lists.newArrayList();
        for (SysFunctionPermission sysFunctionPermission : funs) {
            if (maps.containsKey(sysFunctionPermission.getPermissionId())) {
                result.add(sysFunctionPermission);
            }
        }
        return result;
    }

    public void assign(String roleId, String[] funs, String type) {

        List<Object[]> sysRolePermissions = Lists.newArrayList();
        List<SysFunctionPermission> sysFunctionPermissions = CacheKit.get(Constants.SYSCACHE,
                Constants.CACHE_ALL_MENU_FUNPERMISSION_LIST);
        if ("res".equals(type)) {
            sysFunctionPermissions = functionService.getAllResourcesRecursive();
        }
        Map<Integer, SysRolePermission> rp = getSysRolePermissions(roleId);
        List<SysFunctionPermission> result = Lists.newArrayList();
        loadFuns(result, Arrays.asList(funs), rp, sysFunctionPermissions);
        Integer key;
        for (SysFunctionPermission sysFunctionPermission : result) {
            key = sysFunctionPermission.getPermissionId();
            while (key != null) {
                if (!rp.containsKey(key)) {
                    Object[] param = new Object[3];
                    param[0] = IdGen.uuid();
                    param[1] = sysFunctionPermission.getPermissionId();
                    param[2] = roleId;
                    SysRolePermission sysRolePermission = new SysRolePermission();
                    sysRolePermission.setPermissionId(sysFunctionPermission
                            .getPermissionId());
                    sysRolePermission.setRoleId(roleId);
                    rp.put(sysFunctionPermission.getPermissionId(), sysRolePermission);
                    sysRolePermissions.add(param);
                }

                SysPermission<?> parent = sysFunctionPermission.getParent();
                if (parent != null && parent.getPermissionId() != Constants.TOPFUNCTIONID) {
                    sysFunctionPermission = (SysFunctionPermission) parent;
                    key = parent.getPermissionId();
                } else {
                    key = null;
                }
            }
        }
        if (result.size() != 0) {
            roleDao.insertRolePermission(sysRolePermissions.toArray(new Object[0][0]));
        }
    }

    public void deAssign(String roleId, List<Integer> funs, String type) {

        List<Object[]> sysRolePermissions = Lists.newArrayList();
        List<SysFunctionPermission> sysFunctionPermissions = CacheKit.get(Constants.SYSCACHE,
                Constants.CACHE_ALL_MENU_FUNPERMISSION_LIST);
        if ("res".equals(type)) {
            sysFunctionPermissions = functionService.getAllResourcesRecursive();
        }

        Map<Integer, SysRolePermission> rolePermissionMap = getSysRolePermissions(roleId);

        for (Integer fun : funs) {
            SysFunctionPermission sysFunctionPermission = functionService.getTargetFunction(sysFunctionPermissions, fun);
            if (sysFunctionPermission == null) {
                continue;
            }
            boolean containAllChildren = true;
            for (SysPermission<?> sysPermission : functionService.getChildren
                    (sysFunctionPermissions, sysFunctionPermission)) {
                if (rolePermissionMap.containsKey(sysPermission.getPermissionId()) &&
                        !funs.contains(sysPermission.getPermissionId())) {
                    containAllChildren = false;
                }
            }
            if (!containAllChildren) {
                continue;
            }
            Object[] param = new Object[2];
            param[0] = fun;
            param[1] = roleId;
            sysRolePermissions.add(param);
        }
        if (sysRolePermissions.size() != 0) {
            roleDao.deleteRolePermission(sysRolePermissions.toArray(new Object[0][0]));
        }
    }

    private void loadFuns(List<SysFunctionPermission> result, List<String> funs, Map<Integer, SysRolePermission> rp,
                          List<SysFunctionPermission> sysFunctionPermissions) {
        for (String funp : funs) {
            for (SysFunctionPermission fun : sysFunctionPermissions) {
                if (funp.equals(fun.getPermissionId().toString())) {
                    if (!rp.containsKey(fun.getPid())) {
                        if(fun.getPid()!=Constants.TOPFUNCTIONID){
                            result.add(functionService.getResourceFunction(fun.getPid().toString()));
                        }
                    }
                    result.add(fun);
                    break;
                }
            }
        }
    }

    private Map<String, String> getAssignedRoles() {
        return roleDao.getAssignedRoles();
    }

    private Map<Integer, SysRolePermission> getSysRolePermissions(String roleId) {
        List<SysRolePermission> rp = roleDao.getFunPermissionByRoleId(roleId);
        Map<Integer, SysRolePermission> maps = Maps.newHashMap();
        for (SysRolePermission sysRolePermission : rp) {
            maps.put(sysRolePermission.getPermissionId(), sysRolePermission);
        }
        return maps;
    }

    public String getRoleIdByName(String name) {
        SysRole sysRole = roleDao.getRoleByName(name);
        if (sysRole != null) {
            return sysRole.getRoleId();
        }
        return null;
    }

    public String getRoleName(String type) {
        return roleDao.getRoleName(type);
    }
}

class Person {
    String name;
    String age;

    @Override
    public String toString() {
        return "Person [name=" + name + ", age=" + age + "]";
    }
}