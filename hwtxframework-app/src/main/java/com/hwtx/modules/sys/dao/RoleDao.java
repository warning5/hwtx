package com.hwtx.modules.sys.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hwtx.modules.sys.entity.SysRole;
import com.hwtx.modules.sys.entity.SysRolePermission;
import com.hwtxframework.ioc.annotation.Component;
import com.jfinal.ext.plugin.sqlinxml.SqlKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.thinkgem.jeesite.common.Constants;
import com.thinkgem.jeesite.common.persistence.AbstractBaseDao;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Component
public class RoleDao extends AbstractBaseDao<SysRole> {

    public List<SysRole> getAllRoles() {
        List<Record> records = findComplex(SysSqlConstants.role_findAllRoles);

        Map<String, SysRole> roleMapping = Maps.newHashMap();
        List<SysRole> result = Lists.newArrayList();
        for (Record record : records) {
            Map<String, Object> mapRecords = getRecordModel(record);
            SysRole sysRole = (SysRole) mapRecords.get("role");
            roleMapping.put(sysRole.getRoleId(), sysRole);
        }

        for (Entry<String, SysRole> entry : roleMapping.entrySet()) {
            SysRole sysRole = entry.getValue();
            String pid = sysRole.getPid();
            if (Constants.TOPROLEID.equals(pid)) {
                sysRole.setParent(Constants.TOPROLEID);
                result.add(sysRole);
            } else {
                SysRole p = roleMapping.get(pid);
                p.getChildRoles().add(sysRole);
                sysRole.setParent(p.getPid());
            }
        }
        return result;
    }

    public void deleteRoles(String[][] result) {
        delete(SysSqlConstants.role_deleteRoles, result);
    }

    public List<SysRolePermission> getFunPermissionByRoleId(String roleId) {
        return getList(SysRolePermission.dao, SysSqlConstants.role_getRolePermissions, roleId);
    }

    public void insertRolePermission(Object[][] sysRolePermissions) {
        Db.batch(SqlKit.sql(SysSqlConstants.role_insertRolePermissions), sysRolePermissions, sysRolePermissions.length);
    }

    public void deleteRolePermission(Object[][] sysRolePermissions) {
        Db.batch(SqlKit.sql(SysSqlConstants.role_deleteRolePermissions), sysRolePermissions, sysRolePermissions.length);
    }

    public Map<String, String> getAssignedRoles() {
        List<Record> records = Db.find(SqlKit.sql(SysSqlConstants.role_getAssignedRoles));
        Map<String, String> result = Maps.newHashMapWithExpectedSize(records.size());
        for (Record rec : records) {
            result.put(rec.getStr("roleId"), rec.getStr("roleName"));
        }
        return result;
    }

    public List<SysRole> getRolesByUserId(String userId) {
        return SysRole.dao.find(SqlKit.sql(SysSqlConstants.role_getRolesByUserId), userId);
    }

    public SysRole getRoleByName(String name) {
        return SysRole.dao.findFirst(SqlKit.sql(SysSqlConstants.role_getRoleByName), name);
    }

    public List<String> getChildrenId(String pid) {
        Record record = Db.findFirst(SqlKit.sql(SysSqlConstants.role_getAllChildren), pid);
        String ids = record.get("ids");
        List<String> result = Lists.newArrayList();
        if (ids != null) {
            for (String id : ids.split(",")) {
                if (!id.equals("$"))
                    result.add(id);
            }
        }
        return result;
    }

    public String getRoleName(String id) {
        SysRole sysRole = SysRole.dao.findById(id);
        if (sysRole != null) {
            return sysRole.getName();
        }
        return null;
    }
}