package com.hwtx.fncel.pbc.dao;

import com.hwtx.fncel.pbc.entity.DefInidata;
import com.hwtx.fncel.pbc.util.AppConstants;
import com.hwtx.modules.sys.entity.SysRole;
import com.hwtxframework.ioc.annotation.Component;
import com.jfinal.ext.plugin.sqlinxml.SqlKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page1;
import com.thinkgem.jeesite.common.persistence.AbstractBaseDao;
import com.thinkgem.jeesite.common.persistence.CollectBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * Created by panye on 2014/9/15.
 */
@Component
public class IniDataDao extends AbstractBaseDao<DefInidata> {

    public DefInidata getDefInidataByName(String diniDataName) {
        return DefInidata.dao.findFirst(SqlKit.sql(AppConstants.rule_getDefInidataByName), diniDataName);
    }

    public List<SysRole> getSubmitOrgRoleByName() {
        return SysRole.dao.find(SqlKit.sql(AppConstants.rule_getSubmitOrgRole), AppConstants.SUBMITORGROLE);
    }

    public Page1<DefInidata> findInidataByPage(DefInidata defInidata, int offset, int number) {
        if (defInidata.isSeachItemNull()) {
            return DefInidata.dao.paginateOverOffset(offset, number, SqlKit
                            .sqlSelect(AppConstants.rule_getDefInidata).getExpress(),
                    SqlKit.sqlExceptSelect(AppConstants.rule_getDefInidata).getExpress(), offset);
        }
        CollectBuilder collectBuilder = new CollectBuilder();
        if (StrKit.notBlank(defInidata.getDiniDataLabel())) {
            collectBuilder.like("diniDataLabel", "%" + defInidata.getDiniDataLabel() + "%");
        }
        if (!StrKit.isBlank(defInidata.getSubmitOrgRole())) {
            collectBuilder.in("submitOrgRole", Arrays.asList(defInidata.getSubmitOrgRole().split(",")));
        }
        return DefInidata.dao.paginateOverOffset(offset, number, SqlKit.sqlSelect(AppConstants.rule_getDefInidata)
                .getExpress(), SqlKit.sqlExceptSelect(AppConstants.rule_getDefInidata).getExpress() +
                collectBuilder.build(true), offset);
    }

    public void deleteDefInidata(String[] ids) {
        String sql = SqlKit.sql(AppConstants.rule_deleteDefInidata);
        String[][] paras = new String[ids.length][];
        for (int i = 0; i < ids.length; i++) {
            paras[i] = new String[]{ids[i]};
        }
        Db.batch(sql, paras, paras.length);
    }

    public List<DefInidata> getIniDataNameAndLabels() {
        return DefInidata.dao.find(SqlKit.sql(AppConstants.rule_getIniDataNameAndLabels));
    }

    public List<DefInidata> getIniDataNameAndLabelsByNames(String[] names) {
        CollectBuilder collectBuilder = new CollectBuilder();
        collectBuilder.in("diniDataName", Arrays.asList(names));
        return DefInidata.dao.find(SqlKit.sql(AppConstants.rule_getIniDataNameAndLabels) + collectBuilder.build(true));
    }

    public List<DefInidata> getDefInidatasBySubmitRole(String submitRole) {
        return DefInidata.dao.find(SqlKit.sql(AppConstants.rule_getDefInidatasBySubmitRole), submitRole);
    }
}
