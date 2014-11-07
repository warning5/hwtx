package com.hwtx.fncel.pbc.service;

import com.google.common.collect.Maps;
import com.hwtx.fncel.pbc.dao.IniDataDao;
import com.hwtx.fncel.pbc.entity.DefInidata;
import com.hwtx.fncel.pbc.exception.ExistException;
import com.hwtx.fncel.pbc.util.AppConstants;
import com.hwtx.modules.sys.entity.SysRole;
import com.hwtxframework.ioc.annotation.Component;
import com.jfinal.plugin.activerecord.Page1;
import com.jfinal.plugin.activerecord.Table;
import com.jfinal.plugin.activerecord.TableMapping;
import com.jfinal.plugin.ehcache.CacheKit;
import com.thinkgem.jeesite.common.persistence.CollectBuilder;
import com.thinkgem.jeesite.common.utils.IdGen;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by panye on 2014/9/15.
 */
@Component
public class IniDataService {

    @Resource
    private IniDataDao iniDataDao;

    public void save(DefInidata defInidata) throws ExistException {
        DefInidata exist = iniDataDao.getDefInidataByName(defInidata.getDiniDataName());

        if (defInidata.getDiniDataId() == null) {

            if (exist != null) {
                throw new ExistException();
            }
            defInidata.prePersist();
            defInidata.setDiniDataId(IdGen.uuid());
            iniDataDao.save(defInidata);
        } else {
            if (exist != null && !exist.getDiniDataId().equals(defInidata.getDiniDataId())) {
                throw new ExistException();
            }
            defInidata.preUpdate();
            iniDataDao.update(defInidata);
        }

        CacheKit.remove(AppConstants.CACHE_APP, AppConstants.CACHE_APP_ITEM_DEFINIDATA);
    }

    public Map<String, String> getSubmitOrgRole() {
        List<SysRole> roles = iniDataDao.getSubmitOrgRoleByName();
        Map<String, String> result = Maps.newHashMapWithExpectedSize(roles.size());
        for (SysRole sysRole : roles) {
            result.put(sysRole.getRoleId(), sysRole.getName());
        }
        return result;
    }

    public Page1<DefInidata> findInidataByPage(DefInidata defInidata, int iDisplayStart, int fcount) {
        return iniDataDao.findInidataByPage(defInidata, iDisplayStart, fcount);
    }

    public DefInidata getDefInidata(String id) {
        return (DefInidata) iniDataDao.findById(DefInidata.dao, id);
    }

    public void delete(String[] ids) {
        iniDataDao.deleteDefInidata(ids);
    }

    public List<DefInidata> getIniDataNameAndLabels() {
        return iniDataDao.getIniDataNameAndLabels();
    }

    public List<DefInidata> getIniDataNameAndLabelsByNames(String[] names) {
        return iniDataDao.getIniDataNameAndLabelsByNames(names);
    }

    public List<DefInidata> getDefInidatasBySubmitRole(String submitRole) {
        Map<String, List<DefInidata>> definidataCache = CacheKit.get(AppConstants.CACHE_APP,
                AppConstants.CACHE_APP_ITEM_DEFINIDATA);
        List<DefInidata> defInidatas = null;
        if (definidataCache != null) {
            defInidatas = definidataCache.get(submitRole);
            if (defInidatas == null) {
                defInidatas = iniDataDao.getDefInidatasBySubmitRole(submitRole);
            }
        } else {
            definidataCache = Maps.newHashMap();
            defInidatas = iniDataDao.getDefInidatasBySubmitRole(submitRole);
            definidataCache.put(submitRole, defInidatas);
            CacheKit.put(AppConstants.CACHE_APP, AppConstants.CACHE_APP_ITEM_DEFINIDATA, definidataCache);
        }
        return defInidatas;
    }

    public List<DefInidata> getDefInidatasByIds(List<String> ids) {
        CollectBuilder collectBuilder = new CollectBuilder();
        collectBuilder.in("diniDataId", ids);
        Table table = TableMapping.me().getTable(DefInidata.class);
        return DefInidata.dao.find("select * from " + table.getName() + " " + CollectBuilder.whereClause() +
                collectBuilder.build(false));
    }
}
