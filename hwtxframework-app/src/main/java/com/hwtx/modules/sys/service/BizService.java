package com.hwtx.modules.sys.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hwtx.modules.sys.dao.BizDao;
import com.hwtx.modules.sys.dao.SysSqlConstants;
import com.hwtx.modules.sys.entity.SysBizPermission;
import com.hwtx.modules.sys.entity.SysBizPermissionParam;
import com.hwtx.modules.sys.entity.SysPermission;
import com.hwtx.modules.sys.service.ServiceResponse.ResponseType;
import com.hwtxframework.ioc.annotation.Component;
import com.hwtxframework.ioc.annotation.Dependon;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page1;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;
import com.thinkgem.jeesite.common.Constants;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;

@Component
@Dependon("jFinalCache")
public class BizService {

    @Resource
    private BizDao bizDao;
    @Resource
    private FunctionService functionService;

    public String getBizTree() {
        List<Map<String, Object>> mapList = Lists.newArrayList();
        Map<String, Object> map = Maps.newHashMap();
        map.put("id", Constants.TOPFUNCTIONID);
        map.put("parent", "#");
        map.put("text", Constants.TOPBIZNAME);
        Map<String, Object> state = Maps.newHashMap();
        state.put("opened", true);
        map.put("state", state);
        mapList.add(map);
        buildBizTree(getCacheBizPermissions(), mapList);
        return JSON.toJSONString(mapList);
    }

    @PostConstruct
    public void loadBiz() {
        setCacheBizPermissions(bizDao.getAllBizs());
    }

    private void buildBizTree(List<SysBizPermission> sysBizPermissions, List<Map<String, Object>> mapList) {

        for (SysBizPermission sysBizPermission : sysBizPermissions) {

            Map<String, Object> map = Maps.newHashMap();
            map.put("id", sysBizPermission.getPermissionId());
            map.put("parent", sysBizPermission.getPid());
            map.put("text", sysBizPermission.getName());
            if (sysBizPermission.getType().equals("1")) {
                map.put("icon", false);
            }
            mapList.add(map);
        }
    }

    public void save(SysBizPermission sysBizPermission, String save_type) {

        bizDao.save(sysBizPermission, save_type);
        loadBiz();
    }

    private List<SysBizPermission> getCacheBizPermissions() {
        return CacheKit.get(Constants.SYSCACHE, Constants.CACHE_ALL_BIZPERMISSIONS);
    }

    private void setCacheBizPermissions(List<SysBizPermission> sysBizPermissions) {
        CacheKit.put(Constants.SYSCACHE, Constants.CACHE_ALL_BIZPERMISSIONS, sysBizPermissions);
    }

    public SysBizPermission getBiz(String bizId) {
        return getBiz(getCacheBizPermissions(), bizId);
    }

    private SysBizPermission getBiz(List<SysBizPermission> bizs, String bizId) {
        for (SysPermission<?> sysPermission : bizs) {
            if (sysPermission.getPermissionId().toString().equals(bizId)) {
                return (SysBizPermission) sysPermission;
            }
        }
        return null;
    }

    public SysBizPermission findBizByName(String name) {
        return findBizByName(getCacheBizPermissions(), name);
    }

    private SysBizPermission findBizByName(List<SysBizPermission> bizs, String name) {
        for (SysBizPermission sysBizPermission : bizs) {
            if (sysBizPermission.getName().equals(name)) {
                return sysBizPermission;
            }
        }
        return null;
    }

    public Collection<String> delete(String id) {
        List<Integer[]> removing = Lists.newArrayList();
        Map<String, String> assignedBizs = getAssignedBizs();
        List<String> children = bizDao.getAllChildren(id);

        Collection<String> returnValue = Sets.newHashSet();
        for (String child : children) {
            if (!assignedBizs.containsKey(child)) {
                removing.add(new Integer[]{Integer.valueOf(child)});
            } else {
                returnValue.add(assignedBizs.get(id));
            }
        }

        if (removing.size() != 0 && returnValue.size() == 0) {
            bizDao.delete(SysSqlConstants.biz_delete, removing.toArray(new Integer[0][0]));
        }
        loadBiz();
        return returnValue;
    }

    private Map<String, String> getAssignedBizs() {
        return bizDao.getAssignedBizs();
    }

    @Before(Tx.class)
    public ServiceResponse move(String id, String from, String to) {

        ServiceResponse serviceResponse = new ServiceResponse();
        if (to.equals(Constants.INVALIDPARENT)) {
            serviceResponse.setMessage("无效的移动位置");
            serviceResponse.setResponseType(ResponseType.ERRPOR);
            return serviceResponse;
        }

        SysBizPermission.dao.setPermissionId(Integer.valueOf(id))
                .setPid(Integer.valueOf(to)).update();
        serviceResponse.setMessage("保存成功");
        serviceResponse.setResponseType(ResponseType.SUCCESS);
        loadBiz();
        return serviceResponse;
    }

    public List<SysBizPermissionParam> getBizParams(String bizId) {
        return bizDao.getBizParams(SysSqlConstants.biz_getBizParams, bizId);
    }

    @Before(Tx.class)
    public void saveDef(String bizId, String def, List<SysBizPermissionParam> lparams) {
        bizDao.savePermissionDef(bizId, def);
        bizDao.updateAllParams(bizId, lparams);
        loadBiz();
    }

    public String[] getSampleColumns(String def, List<SysBizPermissionParam> lparams, Map<String, String[]> allparams) {
        return bizDao.getSampleColumns(def, getParams(lparams, allparams));
    }

    public Page1<Map<String, Object>> getPageValues(String def, List<SysBizPermissionParam> lparams,
                                                    Map<String, String[]> allparams, int offset, int count) {
        return bizDao.getPageValues(def, getParams(lparams, allparams), offset, count);
    }

    private Object[] getParams(List<SysBizPermissionParam> lparams, Map<String, String[]> allparams) {
        Object[] params = new Object[]{};
        if (lparams.size() != 0) {
            Collections.sort(lparams, new Comparator<SysBizPermissionParam>() {

                @Override
                public int compare(SysBizPermissionParam o1, SysBizPermissionParam o2) {
                    if (o1.getSerial() > o2.getSerial()) {
                        return -1;
                    } else if (o1.getSerial() < o2.getSerial()) {
                        return 1;
                    }
                    return 0;
                }
            });
            params = new Object[lparams.size()];
            int i = 0;
            for (SysBizPermissionParam param : lparams) {
                String type = param.getType();
                if (type.equals("collection")) {

                } else {
                    params[i] = allparams.get(param.getName())[0];
                }
                i++;
            }
        }
        return params;
    }
}
