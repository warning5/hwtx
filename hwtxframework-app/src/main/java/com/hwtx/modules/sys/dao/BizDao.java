package com.hwtx.modules.sys.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hwtx.modules.sys.entity.SysBizPermission;
import com.hwtx.modules.sys.entity.SysBizPermissionParam;
import com.hwtxframework.ioc.annotation.Component;
import com.jfinal.aop.Before;
import com.jfinal.ext.plugin.sqlinxml.SqlKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page1;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.thinkgem.jeesite.common.persistence.AbstractBaseDao;
import com.thinkgem.jeesite.common.utils.IdGen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class BizDao extends AbstractBaseDao<SysBizPermission> {

    public List<SysBizPermission> getAllBizs() {
        return SysBizPermission.dao.find(SqlKit.sql(SysSqlConstants.biz_getAllBizs));
    }

    public ResultType save(SysBizPermission sysBizPermission, String saveType) {
        ResultType resultType = null;
        if (ResultType.INSERT.name().equals(saveType)) {
            sysBizPermission.prePersist();
            sysBizPermission.save();
            resultType = ResultType.INSERT;
        } else {
            sysBizPermission.preUpdate();
            sysBizPermission.update();
            resultType = ResultType.UPDATE;
        }
        return resultType;
    }

    /**
     * key:bizId,value:bizName
     *
     * @return
     */
    public Map<String, String> getAssignedBizs() {
        List<Record> records = Db.find(SqlKit
                .sql(SysSqlConstants.biz_getAssignedBizs));
        Map<String, String> result = Maps.newHashMapWithExpectedSize(records
                .size());
        for (Record rec : records) {
            result.put(rec.getStr("permissionId"), rec.getStr("name"));
        }
        return result;
    }

    public List<SysBizPermissionParam> getBizParams(String sql, String bizId) {
        return SysBizPermissionParam.dao.find(SqlKit.sql(sql), bizId);
    }

    public void savePermissionDef(String bizId, String def) {
        Db.update(SqlKit.sql(SysSqlConstants.biz_insertOrUpdateDef), def, bizId);
    }

    @Before(Tx.class)
    public void updateAllParams(String bizId,
                                List<SysBizPermissionParam> lparams) {
        Db.update(SqlKit.sql(SysSqlConstants.biz_deleteAllParams), bizId);
        if (lparams.size() != 0) {
            Object[][] params = new Object[lparams.size()][5];
            int i = 0;
            for (SysBizPermissionParam param : lparams) {
                params[i][0] = IdGen.uuid();
                params[i][1] = param.getName();
                params[i][2] = param.getType();
                params[i][3] = param.getSerial();
                params[i][4] = bizId;
                i++;
            }
            Db.batch(SqlKit.sql(SysSqlConstants.biz_insertParams), params,
                    params.length);
        }
    }

    public String[] getSampleColumns(String sql, Object[] paras) {
        Record record = Db.findFirst(sql, paras);
        if (record != null) {
            return record.getColumnNames();
        }
        return null;
    }

    public Page1<Map<String, Object>> getPageValues(String def,
                                                    Object[] params, int offset, int count) {
        int index = def.indexOf("from");
        if (index < 0) {
            Page1<Map<String, Object>> page1 = new Page1<Map<String, Object>>(
                    new ArrayList<Map<String, Object>>(), 0);
            return page1;
        }
        String select = def.substring(0, index);
        String sqlExceptSelect = def.substring(index);
        Page1<Record> result = Db.paginateOverOffset(offset, count, select,
                sqlExceptSelect, params);
        List<Map<String, Object>> ll = Lists.newArrayList();
        for (Record record : result.getList()) {
            ll.add(record.getColumns());
        }
        return new Page1<Map<String, Object>>(ll, result.getTotalRow());
    }

    public List<String> getAllChildren(String pid) {
        Record record = Db.findFirst(SqlKit.sql(SysSqlConstants.biz_getAllChildren), pid);
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
