package com.hwtx.modules.sys.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hwtx.modules.sys.entity.SysOrg;
import com.hwtx.modules.sys.entity.SysUser;
import com.hwtx.modules.sys.utils.UserUtils;
import com.hwtx.modules.sys.vo.SearchSysUser;
import com.hwtxframework.ioc.annotation.Component;
import com.jfinal.ext.plugin.sqlinxml.SqlKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page1;
import com.jfinal.plugin.activerecord.Record;
import com.thinkgem.jeesite.common.persistence.AbstractBaseDao;
import com.thinkgem.jeesite.common.persistence.CollectBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@Component
public class UserDao extends AbstractBaseDao<SysUser> {

    public SysUser findByLoginName(String loginName) {
        return findOne(SysUser.dao, SysSqlConstants.user_findByLoginName, loginName);
    }

    public SysUser getUser(String key) {
        return SysUser.dao.findById(key);
    }

    public Page1<SysUser> findUserByPage(SearchSysUser searchSysUser, int offset, int number) {

        if (searchSysUser.isNull()) {
            return SysUser.dao.paginateOverOffset(offset, number, SqlKit
                            .sqlSelect(SysSqlConstants.user_findAllUser).getExpress(),
                    SqlKit.sqlExceptSelect(SysSqlConstants.user_findAllUser).getExpress(), offset);
        }

        String sqlExceptSelect = SqlKit.sqlExceptSelect(SysSqlConstants.user_findAllUser).getExpress();
        CollectBuilder collectBuilder = new CollectBuilder();
        if (searchSysUser.getUserName() != null) {
            collectBuilder.like("name", "%" + searchSysUser.getUserName() + "%");
        }

        if (searchSysUser.getFinishDate() != null && searchSysUser.getFinishDate() != null) {
            collectBuilder.between("login_date", "'" + searchSysUser.getStartDate() + "'", "'"
                    + searchSysUser.getFinishDate() + "'");
        } else if (searchSysUser.getFinishDate() == null && searchSysUser.getFinishDate() != null) {
            collectBuilder.between("login_date", "now()", "'" + searchSysUser.getFinishDate() + "'");
        } else if (searchSysUser.getFinishDate() != null && searchSysUser.getFinishDate() == null) {
            collectBuilder.between("login_date", "'" + searchSysUser.getStartDate() + "'", "now()");
        }

        if (searchSysUser.getOrgId() != null) {
            if (StringUtils.isEmpty(searchSysUser.getIncludeUser())) {
                collectBuilder.add("userId not in ( select userId from sys_org_user where orgId = '"
                        + searchSysUser.getOrgId() + "')");
            } else {
                collectBuilder.add("userId in ( select userId from sys_org_user where orgId = '"
                        + searchSysUser.getOrgId() + "')");
            }
        }

        if (!collectBuilder.isEmpty()) {
            sqlExceptSelect += CollectBuilder.whereClause() + collectBuilder.build(false);
        }

        return SysUser.dao.paginateOverOffset(offset, number, SqlKit.sqlSelect(SysSqlConstants.user_findAllUser)
                .getExpress(), sqlExceptSelect, offset);
    }

    public void updatePasswordById(String newPassword, String id) {
        SysUser.dao.findById(id).set("password", newPassword).update();
    }

    public void updateLoginInfo(String loginIp, Date loginDate, String id) {
        SysUser.dao.findById(id).set("login_ip", loginIp).set("login_date", loginDate);
    }

    public Pair<List<Integer>, List<SysOrg>> getOrgAndFuns(String userId) {
        List<Record> records = Db.find(SqlKit.sql(SysSqlConstants.user_getOrgFuns), userId);
        List<SysOrg> orgs = Lists.newArrayList();
        List<Integer> funs = Lists.newArrayList();
        for (Record record : records) {
            SysOrg sysOrg = new SysOrg();
            String orgId = record.get("orgId");
            sysOrg.setOrgId(orgId);
            if (orgs.contains(sysOrg)) {
                funs.add(record.getInt("permissionId"));
                continue;
            }
            for (String columnName : record.getColumnNames()) {
                if (sysOrg.hasAttr(columnName)) {
                    sysOrg.set(columnName, record.get(columnName));
                } else {
                    funs.add(record.getInt(columnName));
                }
            }
            orgs.add(sysOrg);
        }
        return new ImmutablePair<>(funs, orgs);
    }

    public Set<Integer> getRoleFuns(String userId) {
        List<Integer> roles = Db.query(SqlKit.sql(SysSqlConstants.role_getRoleFuns), userId);
        return Sets.newHashSet(roles);
    }

    public void saveUser(SysUser sysUser) {
        sysUser.save();
    }

    public SysUser getUserByName(String name) {
        return findOne(SysUser.dao, SysSqlConstants.user_getUserByName, name);
    }

    public void updateSysUserWithOutPwd(SysUser sysUser) {
        Db.update(SqlKit.sql(SysSqlConstants.user_updateSysUserWithOutPwd),
                sysUser.getName(), new Timestamp(new Date().getTime()),
                UserUtils.getUser().getUserId(), sysUser.getUserId());
    }

    public void assignRoles(Object[][] paras) {
        Db.batch(SqlKit.sql(SysSqlConstants.user_assignRoles), paras, paras.length);
    }

    public Map<String, String> getUserRoles(String userId) {
        List<Record> records = Db.find(
                SqlKit.sql(SysSqlConstants.user_getUserRoles), userId);
        Map<String, String> result = Maps.newHashMap();
        for (Record record : records) {
            result.put(record.getStr("roleId"), record.getStr("name"));
        }
        return result;
    }

    public void updateFuncPermissionsSort(Map<String, Integer> map) {
        Object[][] params = new Object[map.size()][2];
        int i = 0;
        for (Entry<String, Integer> entry : map.entrySet()) {
            params[i][0] = entry.getValue();
            params[i][1] = entry.getKey();
            i++;
        }
        Db.batch(SqlKit.sql(SysSqlConstants.user_updateFuncPermissionsSort), params, params.length);
    }

    public String getUserOrg(String userId) {
        Record record = Db.findFirst(SqlKit.sql(SysSqlConstants.user_getUserOrg), userId);
        if (record != null) {
            return record.getStr("orgId");
        }
        return null;
    }
}
