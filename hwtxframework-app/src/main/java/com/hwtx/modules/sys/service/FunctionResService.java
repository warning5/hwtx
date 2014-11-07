package com.hwtx.modules.sys.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hwtx.modules.sys.entity.SysFunctionPermission;
import com.hwtx.modules.sys.entity.SysUser;
import com.hwtx.modules.sys.service.ServiceResponse.ResponseType;
import com.hwtx.modules.sys.utils.UserUtils;
import com.hwtxframework.ioc.annotation.Component;
import com.jfinal.plugin.ehcache.CacheKit;
import com.thinkgem.jeesite.common.Constants;
import com.thinkgem.jeesite.common.config.Global;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class FunctionResService {

    @Resource
    private FunctionService functionService;

    public String getResourceFunctionsAsJson() {

        List<Map<String, Object>> result = Lists.newArrayList();

        Map<String, Object> map = Maps.newHashMap();
        map.put("id", Constants.TOPFUNCTIONID);
        map.put("parent", "#");
        map.put("text", Constants.TOPRESNAME);
        Map<String, Object> state = Maps.newHashMap();
        state.put("opened", true);
        map.put("state", state);
        result.add(map);

        for (SysFunctionPermission functionPermission : getResourceFunctions(UserUtils.getUser())) {
            map = Maps.newHashMap();
            map.put("id", functionPermission.getId());
            map.put("parent", functionPermission.getPid());
            map.put("text", getFunctionText(functionPermission));
            result.add(map);
        }
        return JSON.toJSONString(result);
    }

    private String getFunctionText(SysFunctionPermission functionPermission) {
        String text = functionPermission.getName();
        text += "   <font color=green>resId:" + functionPermission.getId() + "</font>";
        if (functionPermission.getAuth().equals("0")) {
            text = "<font color=red>" + text + "</font>";
        }
        return text;
    }

    @PostConstruct
    public void load() {
        List<SysFunctionPermission> resources = functionService.getResourceFunctions();
        Map<String, Integer> result = Maps.newHashMap();
        for (SysFunctionPermission sysFunctionPermission : resources) {
            if (sysFunctionPermission.getAuth().equals("0")) {
                result.put(Global.getAdminPath() + sysFunctionPermission.getUrl(),
                        sysFunctionPermission.getPermissionId());
            }
        }
        CacheKit.put(Constants.SYSCACHE, Constants.CACHE_ALL_RES_FUNPERMISSION_LIST, resources);
        CacheKit.put(Constants.SYSCACHE, Constants.CACHE_ALL_RES_NO_AUTH_MAP, result);
    }

    public List<SysFunctionPermission> getResourceFunctions(SysUser user) {
        if (user.isAdmin()) {
            return CacheKit.get(Constants.SYSCACHE, Constants.CACHE_ALL_RES_FUNPERMISSION_LIST);
        } else {

            Set<Integer> funs = user.getFunctions();
            List<SysFunctionPermission> allResources = CacheKit.get(Constants.SYSCACHE,
                    Constants.CACHE_ALL_RES_FUNPERMISSION_LIST);

            List<SysFunctionPermission> result = Lists.newArrayListWithCapacity(funs.size());
            for (SysFunctionPermission sysFunctionPermission : allResources) {
                if (funs.contains(sysFunctionPermission.getPermissionId())) {
                    result.add(sysFunctionPermission);
                }
            }
            return result;
        }
    }

    public void save(SysFunctionPermission res) {
        functionService.saveFunctionPermission(res);
        load();
    }

    public void delete(String id) {
        functionService.deleteWithChildren(id);
        load();
    }

    public SysFunctionPermission getResource(String id) {
        return functionService.getResourceFunction(id);
    }

    public ServiceResponse move(String id, String from, String to) {
        ServiceResponse serviceResponse = new ServiceResponse();
        if (to.equals(Constants.INVALIDPARENT)) {
            serviceResponse.setMessage("无效的移动位置");
            serviceResponse.setResponseType(ResponseType.ERRPOR);
            return serviceResponse;
        }
        SysFunctionPermission.dao.setPermissionId(Integer.valueOf(id))
                .setPid(Integer.valueOf(to)).update();

        load();
        serviceResponse.setMessage("保存成功");
        serviceResponse.setResponseType(ResponseType.SUCCESS);
        return serviceResponse;
    }
}