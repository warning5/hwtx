package com.hwtx.modules.sys.dao;

import com.google.common.collect.Maps;
import com.hwtx.modules.sys.entity.SysOrg;
import com.hwtxframework.ioc.annotation.Component;
import com.jfinal.ext.plugin.sqlinxml.SqlKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.thinkgem.jeesite.common.persistence.AbstractBaseDao;

import java.util.List;
import java.util.Map;

@Component
public class OrgDao extends AbstractBaseDao<SysOrg> {

    public List<SysOrg> getOrgs(String orgId) {
        return SysOrg.dao.find(SqlKit.sql(SysSqlConstants.org_getOrgsByPid), orgId);
    }

    public boolean save(SysOrg sysOrg, String saveType) {
        boolean result = false;
        if (ResultType.INSERT.name().equals(saveType)) {
            result = sysOrg.save();
        } else {
            result = sysOrg.update();
        }
        return result;
    }

    public SysOrg getOrg(String orgId) {
        return SysOrg.dao.findById(orgId);
    }

    /**
     * pair left orgId right orgName
     *
     * @param pids
     * @return
     */
    public Map<String, Long> getChildrenCountByPid(List<String> pids) {

        Map<String, Long> result = Maps.newHashMap();

        if (pids.size() == 0) {
            return result;
        }
        StringBuilder builder = new StringBuilder();
        Object[] param = new Object[pids.size()];
        String sql = "select pid,count(orgId) count from sys_org as a";
        builder.append(sql);
        builder.append(" where pid in (");
        for (int i = 0; i < pids.size(); i++) {
            builder.append("?");
            if (i + 1 != pids.size())
                builder.append(",");
            param[i] = pids.get(i);
            result.put(pids.get(i), 0l);
        }
        builder.append(")");

        builder.append(" group by pid");

        List<Record> records = Db.find(builder.toString(), param);

        for (Record rc : records) {
            result.put(rc.getStr("pid"), rc.getLong("count"));
        }
        return result;
    }

    public Long countUser(String orgId) {
        return Db.queryLong(SqlKit.sql(SysSqlConstants.org_getUserCount), orgId);
    }

    public void assignUser(String sqllabel, Object[][] param) {
        Db.batch(SqlKit.sql(sqllabel), param, param.length);
    }

    public void delAssignedUser(String orgAssignuser, Object[][] param) {
        Db.batch(SqlKit.sql(orgAssignuser), param, param.length);
    }

    public Map<String, String> getOrgRoles(String id) {
        List<Record> records = Db.find(SqlKit.sql(SysSqlConstants.org_getOrgRoles), id);
        Map<String, String> result = Maps.newHashMap();
        for (Record record : records) {
            result.put(record.getStr("roleId"), record.getStr("name"));
        }
        return result;
    }

    public void assignRoles(Object[][] param) {
        Db.batch(SqlKit.sql(SysSqlConstants.org_assignRoles), param, param.length);
    }

    public String getOrgName(String pid) {
        return SysOrg.dao.findById(pid).getName();
    }

    public long getAssignUserCountByOrg(String orgId) {
        return Db.queryLong("select Count(userId) from sys_org_user where orgId = ?", orgId);
    }

    public String getOrgIdByUserId(String userId) {
        return Db.queryStr("select orgId from sys_org_user where userId = ?", userId);
    }

    public long countOrgByNamePidType(String name, String pid, Integer type) {
        return Db.queryLong(SqlKit.sql(SysSqlConstants.org_countOrgByNamePidType), name, pid, type);
    }
}
