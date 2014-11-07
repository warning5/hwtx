package com.hwtx.fncel.pbc.dao;

import com.google.common.collect.Lists;
import com.hwtx.fncel.pbc.entity.AppOrg;
import com.hwtx.fncel.pbc.entity.AppPbcInfo;
import com.hwtx.fncel.pbc.entity.FinancialOrg;
import com.hwtx.fncel.pbc.util.AppConstants;
import com.hwtx.modules.sys.dao.OrgDao;
import com.hwtx.modules.sys.entity.SysOrg;
import com.hwtxframework.ioc.annotation.Component;
import com.jfinal.ext.kit.RecordKit;
import com.jfinal.ext.plugin.sqlinxml.SqlKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.thinkgem.jeesite.common.Constants;
import com.thinkgem.jeesite.common.persistence.CollectBuilder;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by panye on 2014/9/26.
 */
@Component
public class AppOrgDao {

    @Resource
    private OrgDao orgDao;

    public AppPbcInfo getPbcInfo(String orgId, String type) {
        Record record = Db.findFirst(SqlKit.sql(AppConstants.appOrg_getPbcInfoByOrgId), orgId, type);
        if (record == null) {
            return null;
        }
        AppPbcInfo appPbcInfo = (AppPbcInfo) RecordKit.toModel(AppPbcInfo.class, record);
        SysOrg sysOrg = (SysOrg) RecordKit.toModel(SysOrg.class, record);
        if (Constants.TOPORGID.equals(sysOrg.getPid())) {
            appPbcInfo.setPname(Constants.TOPORGNAME);
        } else {
            appPbcInfo.setPname(orgDao.getOrgName(sysOrg.getPid()));
        }
        appPbcInfo.setSysOrg(sysOrg);
        return appPbcInfo;
    }

    public List<AppOrg> getAppOrgs(List<SysOrg> sysOrgs) {
        CollectBuilder collectBuilder = new CollectBuilder();
        List<String> orgIds = Lists.newArrayList();
        Integer type = 2;
        for (SysOrg sysOrg : sysOrgs) {
            orgIds.add(sysOrg.getOrgId());
            type = sysOrg.getType();
        }
        List<AppOrg> appOrgs = Lists.newArrayList();
        collectBuilder.in("orgId", orgIds);
        switch (type) {
            case 2:
                List<Record> records = Db.find(SqlKit.sql(AppConstants.appOrg_getAppPbcOrgs) + CollectBuilder
                        .whereClause() + collectBuilder.build(false));
                if (records != null) {
                    for (Record record : records) {
                        appOrgs.add((AppOrg) RecordKit.toModel(AppPbcInfo.class, record));
                    }
                }
                break;
            case 1:
                records = Db.find(SqlKit.sql(AppConstants.appOrg_getAppFinancialOrgs) + CollectBuilder
                        .whereClause() + collectBuilder.build(false));
                if (records != null) {
                    for (Record record : records) {
                        appOrgs.add((AppOrg) RecordKit.toModel(FinancialOrg.class, record));
                    }
                }
                break;
        }
        return appOrgs;
    }
}
