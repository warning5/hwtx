package com.hwtx.fncel.pbc.dao;

import com.hwtx.fncel.pbc.entity.AppUser;
import com.hwtx.fncel.pbc.util.AppConstants;
import com.hwtx.modules.sys.vo.SearchSysUser;
import com.jfinal.ext.plugin.sqlinxml.SqlKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page1;
import com.jfinal.plugin.activerecord.Record;
import com.thinkgem.jeesite.common.persistence.CollectBuilder;

/**
 * Created by panye on 2014/10/16.
 */
public class AppDao {

    protected void buildRegion(String type, Integer region, CollectBuilder collectBuilder) {
        collectBuilder.eq(type, region);
    }

    protected Page1<Record> handleUser(SearchSysUser searchSysUser, String type, Integer region, int offset,
                                       int count, String sql_clause) {
        CollectBuilder collectBuilder = new CollectBuilder();
        buildRegion(type, region, collectBuilder);
        if (searchSysUser.isNull()) {
            return Db.paginateOverOffset(offset, count, SqlKit.sqlSelect(sql_clause).getExpress(),
                    SqlKit.sqlExceptSelect(sql_clause).getExpress() + collectBuilder.build(true), offset);
        }

        String sqlExceptSelect = SqlKit.sqlExceptSelect(sql_clause).getExpress();
        if (searchSysUser.getUserName() != null) {
            collectBuilder.like("b.name", "%" + searchSysUser.getUserName() + "%");
        }

        if (searchSysUser.getFinishDate() != null && searchSysUser.getFinishDate() != null) {
            collectBuilder.between("login_date", "'" + searchSysUser.getStartDate() + "'", "'"
                    + searchSysUser.getFinishDate() + "'");
        } else if (searchSysUser.getFinishDate() == null && searchSysUser.getFinishDate() != null) {
            collectBuilder.between("login_date", "now()", "'" + searchSysUser.getFinishDate() + "'");
        } else if (searchSysUser.getFinishDate() != null && searchSysUser.getFinishDate() == null) {
            collectBuilder.between("login_date", "'" + searchSysUser.getStartDate() + "'", "now()");
        }

        if (!collectBuilder.isEmpty()) {
            sqlExceptSelect += collectBuilder.build(true);
        }

        return Db.paginateOverOffset(offset, count, SqlKit.sqlSelect(sql_clause).getExpress(), sqlExceptSelect, offset);
    }

    public void saveAppUser(AppUser appUser) {
        appUser.save();
    }

    public void updateAppUser(AppUser appUser) {
        Db.update(SqlKit.sql(AppConstants.financial_updateAppUserBySysUserId), appUser.getName(), appUser.getUserId());
    }

}
