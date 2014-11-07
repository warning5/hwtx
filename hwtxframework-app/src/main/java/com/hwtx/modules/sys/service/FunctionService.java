package com.hwtx.modules.sys.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hwtx.modules.sys.dao.FunctionDao;
import com.hwtx.modules.sys.dao.ResultType;
import com.hwtx.modules.sys.entity.SysFunctionPermission;
import com.hwtx.modules.sys.entity.SysPermission;
import com.hwtx.modules.sys.entity.SysRolePermission;
import com.hwtx.modules.sys.utils.Utils;
import com.hwtxframework.ioc.annotation.Component;
import com.thinkgem.jeesite.common.Constants;
import lombok.Getter;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Component
public class FunctionService {

    @Resource
    private FunctionDao functionDao;

    public ResultType saveFunctionPermission(SysFunctionPermission sysFunctionPermission) {
        return functionDao.saveFunctionPermission(sysFunctionPermission);
    }

    public void recursiveExclude(List<SysFunctionPermission> source, Map<Integer, SysRolePermission> rps,
                                 List<SysFunctionPermission> target) {

        for (int i = 0; i < source.size(); i++) {
            SysFunctionPermission fun = source.get(i);
            if (target.contains(fun)) {
                continue;
            }
            if (rps.containsKey(fun.getPermissionId())) {
                List<SysFunctionPermission> cTarget = Lists.newArrayList();
                addFunChildren(source, fun, rps, cTarget);
                if (cTarget.size() != 0) {
                    target.add(fun);
                    target.addAll(cTarget);
                }
            } else {
                addAllChildren(source, fun, target);
                if (!target.contains(fun)) {
                    target.add(fun);
                }
            }
        }
    }

    private void addAllChildren(List<SysFunctionPermission> source, SysFunctionPermission fun,
                                List<SysFunctionPermission> target) {
        for (SysPermission<?> cf : getChildren(source, fun)) {
            target.add((SysFunctionPermission) cf);
            addAllChildren(source, (SysFunctionPermission) cf, target);
        }
    }

    private void addFunChildren(List<SysFunctionPermission> source, SysPermission<?> fun,
                                Map<Integer, SysRolePermission> rps, List<SysFunctionPermission> target) {
        for (SysPermission<?> cf : getChildren(source, fun)) {
            if (!rps.containsKey(cf.getPermissionId())) {
                addAllChildren(source, (SysFunctionPermission) cf, target);
                if (!target.contains(cf)) {
                    target.add((SysFunctionPermission) cf);
                }
            } else {
                List<SysFunctionPermission> cTarget = Lists.newArrayList();
                addFunChildren(source, cf, rps, cTarget);
                if (cTarget.size() != 0) {
                    if (!target.contains(cf)) {
                        target.add((SysFunctionPermission) cf);
                    }
                    target.addAll(cTarget);
                }
            }
        }
    }

    public List<Map<String, Object>> buildMenuTree(List<? extends SysPermission<?>> funs) {
        return buildFunctionTree(funs, Constants.TOPMENUNAME);
    }

    public List<Map<String, Object>> buildResourceTree(List<? extends SysPermission<?>> funs) {
        return buildFunctionTree(funs, Constants.TOPRESNAME);
    }

    public List<Map<String, Object>> buildFunctionTree(List<? extends SysPermission<?>> funs, String name) {

        List<Map<String, Object>> result = Lists.newArrayList();
        Map<String, Object> map = Maps.newHashMap();
        map.put("id", Constants.TOPFUNCTIONID);
        map.put("parent", "#");
        map.put("text", name);
        Map<String, Object> state = Maps.newHashMap();
        state.put("opened", true);
        map.put("state", state);
        result.add(map);
        for (SysPermission<?> fun : funs) {
            if ("0".equals(fun.getEnable())) {
                continue;
            }
            map = Maps.newHashMap();
            map.put("id", fun.getId().toString());
            map.put("parent", fun.getPid() != null ? fun.getPid().toString() : "#");
            map.put("text", fun.getName());
            result.add(map);
        }
        return result;
    }

    public void delete(List<String> ids) {

        String[][] params = new String[ids.size()][1];
        for (int i = 0; i < ids.size(); i++) {
            params[i][0] = ids.get(i);
        }
        functionDao.delete(params);
    }

    public List<SysFunctionPermission> getResourceFunctions() {
        return functionDao.getResourceFunctions();
    }

    public SysFunctionPermission getResourceFunction(String id) {
        return functionDao.getResourceFunction(id);
    }

    public List<SysFunctionPermission> getAllResourcesRecursive() {
        return functionDao.getAllResourcesRecursive();
    }

    public List<SysFunctionPermission> getAllMenusRecursive() {
        return flatAndSort(functionDao.getAllMenusRecursive());
    }

    public SysFunctionPermission getTargetFunction(List<? extends SysPermission<?>>
                                                           sysFunctionPermissions, Integer target) {
        for (SysPermission<?> permission : sysFunctionPermissions) {
            if (permission.getPermissionId().equals(target)) {
                return (SysFunctionPermission) permission;
            }
        }
        return null;
    }

    public List<SysPermission<?>> getChildren(List<? extends SysPermission<?>> sysPermissions,
                                              SysPermission source) {
        List<SysPermission<?>> result = Lists.newArrayList();
        for (SysPermission<?> sysPermission : sysPermissions) {
            if (sysPermission.getPid().equals(source.getPermissionId())) {
                result.add(sysPermission);
            }
        }
        return result;

    }

    public List<String> getAllChildren(String pid) {
        return functionDao.getAllChildren(pid);
    }

    public void deleteWithChildren(String id) {
        List<String> ids = getAllChildren(id);
        if (ids.size() != 0) {
            delete(ids);
        }
    }

    private List<SysFunctionPermission> flatAndSort(List<SysFunctionPermission> funs) {
        Map<Integer, FunctionWrap> permissionMapping = Maps.newHashMap();
        for (SysFunctionPermission fun : funs) {
            permissionMapping.put(fun.getPermissionId(), new FunctionWrap(fun));
        }

        List<FunctionWrap> tempList = Lists.newArrayList();

        for (Map.Entry<Integer, FunctionWrap> entry : permissionMapping.entrySet()) {
            SysPermission<?> sysPermission = entry.getValue().getSysPermission();
            Integer pid = sysPermission.getPid();
            if (Constants.TOPFUNCTIONID == pid) {
                tempList.add(entry.getValue());
                entry.getValue().getSysPermission().setParent(Utils.getTopFunctionPermission());
            } else {
                FunctionWrap parent = permissionMapping.get(pid);
                parent.addChild(entry.getValue());
                entry.getValue().getSysPermission().setParent(parent.getSysPermission());
            }
        }
        List<SysFunctionPermission> result = Lists.newArrayList();
        sort(tempList);
        flatFunctionPermissions(tempList, result);
        return result;
    }

    private void flatFunctionPermissions(List<FunctionWrap> list, List<SysFunctionPermission> result) {
        for (FunctionWrap functionWrap : list) {
            result.add((SysFunctionPermission) functionWrap.getSysPermission());
            flatFunctionPermissions(functionWrap.getChildren(), result);
        }
    }


    public void sort(List<FunctionWrap> collections) {
        Collections.sort(collections, new Comparator<FunctionWrap>() {
            @Override
            public int compare(FunctionWrap o1, FunctionWrap o2) {
                if (o1.getSysPermission().getSequence() > o2.getSysPermission().getSequence()) {
                    return 1;
                } else if (o1.getSysPermission().getSequence() < o2.getSysPermission().getSequence())
                    return -1;
                return 0;
            }
        });
    }

    class FunctionWrap {
        public List<FunctionWrap> children = Lists.newArrayList();
        @Getter
        private SysPermission<?> sysPermission;

        public FunctionWrap(SysPermission<?> sysPermission) {
            this.sysPermission = sysPermission;
        }

        public List<FunctionWrap> getChildren() {
            sort(children);
            return children;
        }

        public void addChild(FunctionWrap functionWrap) {
            children.add(functionWrap);
        }
    }
}
