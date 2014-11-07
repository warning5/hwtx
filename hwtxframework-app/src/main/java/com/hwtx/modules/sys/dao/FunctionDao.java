package com.hwtx.modules.sys.dao;

import com.google.common.collect.Lists;
import com.hwtx.modules.sys.entity.SysFunctionPermission;
import com.hwtxframework.ioc.annotation.Component;
import com.jfinal.ext.plugin.sqlinxml.SqlKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.thinkgem.jeesite.common.persistence.AbstractBaseDao;

import java.util.List;

@Component
public class FunctionDao extends AbstractBaseDao<SysFunctionPermission> {

    public List<SysFunctionPermission> getResourceFunctions() {
        return SysFunctionPermission.dao.find(SqlKit
                .sql(SysSqlConstants.function_getResourceFunctions));
    }

    public ResultType saveFunctionPermission(
            SysFunctionPermission sysFunctionPermission) {
        ResultType resultType = null;
        if (sysFunctionPermission.getPermissionId() == null) {
            sysFunctionPermission.save();
            resultType = ResultType.INSERT;
        } else {
            sysFunctionPermission.update();
            resultType = ResultType.UPDATE;
        }
        return resultType;
    }

    public SysFunctionPermission getResourceFunction(String id) {
        return SysFunctionPermission.dao.findFirst(
                SqlKit.sql(SysSqlConstants.function_getResourceFunction), id);
    }

    public void delete(String[][] ids) {
        Db.batch(SqlKit.sql(SysSqlConstants.function_batchDelete), ids, ids.length);
    }

    public List<SysFunctionPermission> getAllMenusRecursive() {
        return SysFunctionPermission.dao.find(SqlKit.sql(SysSqlConstants.function_getAllMenus));
    }

    public List<SysFunctionPermission> getAllResourcesRecursive() {
        return SysFunctionPermission.dao.find(SqlKit.sql(SysSqlConstants.function_getAllResources));
    }

    public List<String> getAllChildren(String pid) {
        Record record = Db.findFirst(SqlKit.sql(SysSqlConstants.function_getAllChildren), pid);
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
}
