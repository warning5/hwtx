package com.hwtx.fncel.pbc.dao;

import com.hwtx.fncel.pbc.entity.AppUser;
import com.hwtx.fncel.pbc.entity.FinancialOrg;
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
 * Created by panye on 2014/10/2.
 */
@Component
public class FinancialDao extends AppDao {
    public Page1<Record> getFinancialUsers(SearchSysUser searchSysUser, String type, Integer region, int offset,
                                           int count) {
        return handleUser(searchSysUser, type, region, offset, count, AppConstants.financial_getFinancialUsers);
    }

    public Page1<FinancialOrg> getFinancialOrgs(String name, String userOrg, int offset, int number) {

        if (offset == -1 && number == -1) {
            String sql = SqlKit.sqlSelect(AppConstants.pbc_getFinancialOrgsByPbcOrg).getExpress() + SqlKit
                    .sqlExceptSelect(AppConstants.pbc_getFinancialOrgsByPbcOrg).getExpress();
            List<FinancialOrg> financialOrgs = FinancialOrg.dao.find(sql, offset, userOrg);
            return new Page1<>(financialOrgs, financialOrgs.size());
        }

        if (StrKit.isBlank(name)) {
            return FinancialOrg.dao.paginateOverOffset(offset, number, SqlKit
                            .sqlSelect(AppConstants.pbc_getFinancialOrgsByPbcOrg).getExpress(),
                    SqlKit.sqlExceptSelect(AppConstants.pbc_getFinancialOrgsByPbcOrg).getExpress(), offset, userOrg);
        }
        CollectBuilder collectBuilder = new CollectBuilder();
        collectBuilder.like("name", "%" + name + "%");

        return FinancialOrg.dao.paginateOverOffset(offset, number, SqlKit.sqlSelect(AppConstants.pbc_getFinancialOrgsByPbcOrg)
                .getExpress(), SqlKit.sqlExceptSelect(AppConstants.pbc_getFinancialOrgsByPbcOrg).getExpress() +
                collectBuilder.build(true), offset, userOrg);
    }

    public void deleteFinancialByOrgId(String orgId) {
        Db.batch(SqlKit.sql(AppConstants.financial_deleteFinancialByOrgId), new Object[][]{{orgId}}, 1);
    }

    public FinancialOrg getFinancialOrg(String id) {
        return FinancialOrg.dao.findById(id);
    }

    public int insertFinancialOrg(FinancialOrg financialOrg) {
        return Db.update(SqlKit.sql(AppConstants.pbc_insertFinancialOrg), financialOrg.getId(),
                financialOrg.getName(), financialOrg.getProvince(), financialOrg.getCity(), financialOrg.getArea(),
                financialOrg.getUniCode(), financialOrg.getContact(), financialOrg.getPhone(),
                financialOrg.getAddress(), financialOrg.getOrgId());
    }

    public int updateFinancialOrg(FinancialOrg financialOrg) {
        return Db.update(SqlKit.sql(AppConstants.pbc_updateFinancialOrg),
                financialOrg.getName(), financialOrg.getProvince(), financialOrg.getCity(), financialOrg.getArea(),
                financialOrg.getUniCode(), financialOrg.getContact(), financialOrg.getPhone(),
                financialOrg.getAddress(), financialOrg.getOrgId(), financialOrg.getId());
    }

    public int relationPbcAndFin(String orgId, String financialId) {
        return Db.update(SqlKit.sql(AppConstants.pbc_relationPbcAndFin), orgId, financialId);
    }
}
