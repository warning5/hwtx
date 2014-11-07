package com.hwtx.fncel.pbc.service;

import com.google.common.collect.Lists;
import com.hwtx.fncel.pbc.dao.AppOrgDao;
import com.hwtx.fncel.pbc.entity.AppOrg;
import com.hwtx.fncel.pbc.entity.AppPbcInfo;
import com.hwtx.fncel.pbc.exception.ExistException;
import com.hwtx.fncel.pbc.util.AppConstants;
import com.hwtx.modules.sys.dao.OrgDao;
import com.hwtx.modules.sys.entity.SysOrg;
import com.hwtx.modules.sys.entity.SysUser;
import com.hwtx.modules.sys.service.OrgService;
import com.hwtx.modules.sys.utils.UserUtils;
import com.hwtxframework.ioc.annotation.Component;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.thinkgem.jeesite.common.Constants;
import com.thinkgem.jeesite.common.utils.IdGen;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * Created by panye on 2014/9/26.
 */
@Component
public class AppOrgService {

    @Resource
    private AppOrgDao appOrgDao;
    @Resource
    private OrgDao orgDao;
    @Resource
    private OrgService orgService;
    @Resource
    private FinancialService financialService;
    @Resource
    private PbcService pbcService;

    public AppPbcInfo getOrg(String orgId, String type, String pid, String text) {
        if (StrKit.isBlank(orgId)) {
            AppPbcInfo appPbcInfo = new AppPbcInfo();
            if (Constants.TOPORGID.equals(pid)) {
                appPbcInfo.setPname(Constants.TOPORGNAME);
            } else {
                appPbcInfo.setPname(orgDao.getOrgName(pid));
            }
            SysOrg sysOrg = new SysOrg();
            sysOrg.setPid(pid);
            appPbcInfo.setSysOrg(sysOrg);
            return appPbcInfo;
        }
        AppPbcInfo appPbcInfo = appOrgDao.getPbcInfo(orgId, type);
        if (appPbcInfo == null) {
            appPbcInfo = new AppPbcInfo();
            if (Constants.TOPORGID.equals(pid)) {
                appPbcInfo.setPname(Constants.TOPORGNAME);
            } else {
                appPbcInfo.setPname(orgDao.getOrgName(pid));
            }
            SysOrg sysOrg = new SysOrg();
            sysOrg.setName(text);
            sysOrg.setPid(pid);
            sysOrg.setOrgId(orgId);
            appPbcInfo.setSysOrg(sysOrg);
        }
        return appPbcInfo;
    }

    public void save(AppPbcInfo appPbcInfo, SysOrg sysOrg) throws ExistException {

        sysOrg.setType(AppConstants.ORG_PBC_TYPE);
        long count = orgDao.countOrgByNamePidType(sysOrg.getName(), sysOrg.getPid(), sysOrg.getType());
        if (sysOrg.getOrgId() == null) {
            if (count > 0) {
                throw new ExistException();
            }
            String orgId = IdGen.uuid();
            appPbcInfo.setOrgId(orgId);
            appPbcInfo.setId(IdGen.uuid());
            sysOrg.setOrgId(orgId);
            sysOrg.save();
            appPbcInfo.save();
        } else {
            if (count > 1) {
                throw new ExistException();
            }
            sysOrg.update();
            if (appPbcInfo.getId() == null) {
                appPbcInfo.setId(IdGen.uuid());
                appPbcInfo.setOrgId(sysOrg.getOrgId());
                appPbcInfo.save();
            } else {
                appPbcInfo.update();
            }
        }
    }

    public AppOrg getUserOrg() {
        List<AppOrg> orgs = (List<AppOrg>) UserUtils.getCache(AppConstants.CACHE_APP_USER_ORG);
        if (orgs == null) {
            orgs = Lists.newArrayList();
            SysUser sysUser = UserUtils.getUser();
            List<SysOrg> sysOrgs = sysUser.getOrgs();
            if (sysOrgs != null && sysOrgs.size() != 0) {
                orgs.addAll(appOrgDao.getAppOrgs(sysOrgs));
                UserUtils.putCache(AppConstants.CACHE_APP_USER_ORG, orgs);
            }
        }
        if (orgs.size() != 0)
            return orgs.get(0);

        return null;
    }

    public AppOrg getAppOrg() {
        AppOrg appOrg = getUserOrg();
        if (appOrg == null) {
            throw new IllegalArgumentException();
        }
        return appOrg;
    }

    public String getAppOrgByUserId(String userId) {
        return orgDao.getOrgIdByUserId(userId);
    }

    public Integer getRegion() {
        AppOrg appOrg = getAppOrg();
        return appOrg.getArea() != null ? appOrg.getArea() : appOrg.getCity() != null ? appOrg.getCity() : appOrg
                .getProvince();
    }

    public Integer getRegion(AppOrg appOrg) {
        return appOrg.getArea() != null ? appOrg.getArea() : appOrg.getCity();
    }

    @Before(Tx.class)
    public Collection<String> delete(List<String> ids, String type) {
        Collection<String> orgIds = orgService.delete(ids);
        if (Integer.toString(AppConstants.ORG_FINANCIAL_TYPE).equals(type)) {
            for (String id : ids) {
                financialService.deleteFinancialOrg(id);
            }
        } else {
            for (String id : ids) {
                pbcService.deletePbc(id);
            }
        }
        return orgIds;
    }
}