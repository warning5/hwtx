package com.hwtx.modules.sys.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hwtx.modules.sys.dao.OrgDao;
import com.hwtx.modules.sys.dao.SysSqlConstants;
import com.hwtx.modules.sys.entity.SysOrg;
import com.hwtx.modules.sys.service.ServiceResponse.ResponseType;
import com.hwtxframework.ioc.annotation.Component;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.thinkgem.jeesite.common.Constants;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Component
public class OrgService {

    @Resource
    private OrgDao orgDao;

    public String getOrgTree(String orgId) {
        List<Map<String, Object>> mapList = Lists.newArrayList();
        Map<String, Object> map = Maps.newHashMap();
        if (orgId.equals("#")) {
            orgId = Constants.TOPORGID;
            map.put("id", Constants.TOPORGID);
            map.put("parent", "#");
            map.put("text", getTreeText(Constants.TOPORGNAME, -1l));
            Map<String, Object> state = Maps.newHashMap();
            state.put("opened", true);
            map.put("state", state);
            mapList.add(map);
        }
        List<SysOrg> orgs = getOrgsById(orgId);
        if (orgs.size() != 0) {
            for (SysOrg sysOrg : orgs) {
                map = Maps.newHashMap();
                map.put("id", sysOrg.getOrgId());
                map.put("parent", sysOrg.getPid());
                map.put("text", getTreeText(sysOrg.getName(), sysOrg.getUserCount()));
                map.put("children", true);
                Map<String, Object> attrs = Maps.newHashMap();
                attrs.put("type", sysOrg.getType());
                map.put("li_attr", attrs);
                mapList.add(map);
            }
        }
        return JSON.toJSONString(mapList);
    }

    public List<SysOrg> getOrgsById(String id) {
        return orgDao.getOrgs(id);
    }

    public boolean save(SysOrg sysOrg, String save_type) {
        return orgDao.save(sysOrg, save_type);
    }

    private String getTreeText(String realText, Long userSize) {

        if (userSize <= 0) {
            return realText;
        }
        return "<bb>" + realText + "</bb>" + "  <span class=\"badge bg-color-blue txt-color-white\">" + userSize + "</span>";
    }

    /**
     * 只能删除单个没有子元素且没有分配用户的机构
     */
    public Collection<String> delete(List<String> id) {

        Map<String, Long> childrenCount = getChildrenAndUserCountByPid(id);
        List<String[]> removing = Lists.newArrayList();
        Collection<String> returnValue = Sets.newHashSet();
        for (Entry<String, Long> entry : childrenCount.entrySet()) {
            if (entry.getValue() == 0) {
                if (!hasUser(entry.getKey())) {
                    removing.add(new String[]{entry.getKey()});
                } else {
                    returnValue.add(entry.getKey());
                }
            } else {
                returnValue.add(entry.getKey());
            }
        }
        if (removing.size() != 0 && returnValue.size() == 0) {
            String[][] ids = removing.toArray(new String[0][0]);
            deAssignOrgRoles(ids);
            orgDao.delete(SysSqlConstants.org_deleteOrgs, ids);
        }
        return returnValue;
    }

    private boolean hasUser(String orgId) {
        return orgDao.countUser(orgId) != 0;
    }

    public Map<String, Long> getChildrenAndUserCountByPid(List<String> pids) {
        return orgDao.getChildrenCountByPid(pids);
    }

    public ServiceResponse move(String id, String from, String to) {

        ServiceResponse serviceResponse = new ServiceResponse();
        if (to.equals(Constants.INVALIDPARENT)) {
            serviceResponse.setMessage("无效的移动位置");
            serviceResponse.setResponseType(ResponseType.ERRPOR);
            return serviceResponse;
        }
        SysOrg.dao.setOrgId(id).setPid(to).update();
        serviceResponse.setMessage("保存成功");
        serviceResponse.setResponseType(ResponseType.SUCCESS);
        return serviceResponse;
    }

    public SysOrg getOrg(String orgId) {
        return orgDao.getOrg(orgId);
    }

    public Long assignUser(String[] userIds, String orgId) {

        Object[][] param = new Object[userIds.length][2];
        for (int i = 0; i < userIds.length; i++) {
            param[i][0] = orgId;
            param[i][1] = userIds[i];
        }
        orgDao.assignUser(SysSqlConstants.org_assignUser, param);
        return orgDao.countUser(orgId);
    }

    public Long delAssignedUser(String[] userIds, String orgId) {

        Object[][] param = new Object[userIds.length][2];
        for (int i = 0; i < userIds.length; i++) {
            param[i][0] = orgId;
            param[i][1] = userIds[i];
        }
        orgDao.delAssignedUser(SysSqlConstants.org_delAssignedUser, param);
        return orgDao.countUser(orgId);
    }

    public Map<String, String> getOrgRoles(String id) {
        return orgDao.getOrgRoles(id);
    }

    @Before(Tx.class)
    public void assignRoles(String orgId, String roles) {
        String[] rols = roles.split(",");
        deAssignOrgRoles(new String[][]{{orgId}});
        if (StringUtils.isNotEmpty(roles)) {
            Object[][] param = new Object[rols.length][2];
            for (int i = 0; i < rols.length; i++) {
                param[i][0] = orgId;
                param[i][1] = rols[i];
            }
            orgDao.assignRoles(param);
        }
    }

    public void deAssignOrgRoles(String[][] ids) {
        orgDao.delete(SysSqlConstants.org_delOrgRoles, ids);
    }

    public void update(SysOrg sysOrg) {
        sysOrg.update();
    }

    public long getAssignUserCountByOrg(String orgId) {
        return orgDao.getAssignUserCountByOrg(orgId);
    }
}
