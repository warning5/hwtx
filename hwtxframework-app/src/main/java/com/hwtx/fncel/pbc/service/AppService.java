package com.hwtx.fncel.pbc.service;

import com.hwtx.fncel.pbc.dao.AppDao;
import com.hwtx.fncel.pbc.entity.AppUser;
import com.hwtx.fncel.pbc.exception.ExistException;
import com.hwtx.modules.sys.dao.ResultType;
import com.hwtx.modules.sys.entity.SysUser;
import com.hwtx.modules.sys.service.OrgService;
import com.hwtx.modules.sys.service.SysUserService;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.thinkgem.jeesite.common.utils.IdGen;

import javax.annotation.Resource;

/**
 * Created by panye on 2014/10/16.
 */
public class AppService {

    @Resource
    protected SysUserService sysUserService;
    @Resource
    protected OrgService orgService;
    protected AppDao appDao;

    @Before(Tx.class)
    public void updateUser(SysUser sysUser, String userOrg) {
        sysUserService.edit(sysUser);
        orgService.assignUser(new String[]{sysUser.getUserId()}, userOrg);
        AppUser appUser = new AppUser();
        appUser.setName(sysUser.getName());
        appUser.setUserId(sysUser.getUserId());
        appDao.updateAppUser(appUser);
    }

    @Before(Tx.class)
    public void saveUser(SysUser sysUser, String userOrg) throws ExistException {
        ResultType resultType = sysUserService.save(sysUser);
        if (resultType == ResultType.EXIST) {
            throw new ExistException();
        }
        orgService.assignUser(new String[]{sysUser.getUserId()}, userOrg);
        AppUser appUser = new AppUser();
        appUser.setName(sysUser.getName());
        appUser.setId(IdGen.uuid());
        appUser.setUserId(sysUser.getUserId());
        appDao.saveAppUser(appUser);
    }

    public SysUser getUser(String userId) {
        return sysUserService.getSysUser(userId);
    }

    public void deleteUser(String[] userIds, String userOrg) {
        sysUserService.deleteUser(new String[][]{userIds});
        orgService.delAssignedUser(userIds, userOrg);
    }

    protected String getUserOrgId(SysUser sysUser) {
        return sysUserService.getUserOrg(sysUser.getUserId());
    }
}
