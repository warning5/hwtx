package com.hwtx.fncel.pbc.dao;

import com.hwtx.fncel.pbc.entity.AppPbcInfo;
import com.hwtx.fncel.pbc.util.AppConstants;
import com.hwtx.modules.sys.vo.SearchSysUser;
import com.hwtxframework.ioc.annotation.Component;
import com.jfinal.ext.plugin.sqlinxml.SqlKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page1;
import com.jfinal.plugin.activerecord.Record;
import com.thinkgem.jeesite.common.persistence.CollectBuilder;

import java.util.List;

/**
 * Created by panye on 2014/9/3.
 */
@Component
public class PbcDao extends AppDao {

    public AppPbcInfo getAppPbcInfo(String id) {
        return AppPbcInfo.dao.findFirst(SqlKit.sql(AppConstants.pbc_getAppPbcInfo), id);
    }

    public Page1<AppPbcInfo> getAppPbcOgs(String name, String userOrg, int offset, int number) {

        if (offset == -1 && number == -1) {
            String sql = SqlKit.sqlSelect(AppConstants.pbc_getAppPbcOgs).getExpress() + SqlKit
                    .sqlExceptSelect(AppConstants.pbc_getAppPbcOgs).getExpress();
            List<AppPbcInfo> appPbcInfos = AppPbcInfo.dao.find(sql, offset, userOrg);
            return new Page1<>(appPbcInfos, appPbcInfos.size());
        }

        if (StrKit.isBlank(name)) {
            return AppPbcInfo.dao.paginateOverOffset(offset, number, SqlKit
                            .sqlSelect(AppConstants.pbc_getAppPbcOgs).getExpress(),
                    SqlKit.sqlExceptSelect(AppConstants.pbc_getAppPbcOgs).getExpress(), offset, userOrg);
        }
        CollectBuilder collectBuilder = new CollectBuilder();
        collectBuilder.like("name", "%" + name + "%");

        return AppPbcInfo.dao.paginateOverOffset(offset, number, SqlKit.sqlSelect(AppConstants.pbc_getAppPbcOgs)
                .getExpress(), SqlKit.sqlExceptSelect(AppConstants.pbc_getAppPbcOgs).getExpress() +
                collectBuilder.build(true), offset, userOrg);

    }

    public int updateAppPbcInfo(AppPbcInfo appPbcInfo) {
        return Db.update(SqlKit.sql(AppConstants.pbc_updateAppPbcInfo),
                appPbcInfo.getProvince(), appPbcInfo.getCity(), appPbcInfo.getArea(),
                appPbcInfo.getContact(), appPbcInfo.getPhone(), appPbcInfo.getAddress(), appPbcInfo.getOrgId(), appPbcInfo.getId());
    }

    public int insertAppPbcInfo(AppPbcInfo appPbcInfo) {
        return Db.update(SqlKit.sql(AppConstants.pbc_insertAppPbcInfo), appPbcInfo.getId(),
                appPbcInfo.getProvince(), appPbcInfo.getCity(), appPbcInfo.getArea(),
                appPbcInfo.getContact(), appPbcInfo.getPhone(), appPbcInfo.getAddress(), appPbcInfo.getOrgId());
    }

    public void deletePbcOrg(String orgId) {
        Db.batch(SqlKit.sql(AppConstants.pbc_deletePbcOrglByOrgId), new Object[][]{{orgId}}, 1);
    }

    public Page1<Record> getPbcUsers(SearchSysUser searchSysUser, String type, Integer region, int offset, int count) {
        return handleUser(searchSysUser, type, region, offset, count, AppConstants.pbc_getPbcUsers);
    }
}
