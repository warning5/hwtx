package com.hwtx.modules.sys.service;

import com.google.common.collect.Maps;
import com.hwtx.modules.sys.dao.ResultType;
import com.hwtx.modules.sys.dao.SysSqlConstants;
import com.hwtx.modules.sys.dao.UserDao;
import com.hwtx.modules.sys.entity.SysFunctionPermission;
import com.hwtx.modules.sys.entity.SysOrg;
import com.hwtx.modules.sys.entity.SysUser;
import com.hwtx.modules.sys.utils.UserUtils;
import com.hwtx.modules.sys.vo.SearchSysUser;
import com.hwtxframework.ioc.annotation.Component;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page1;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.thinkgem.jeesite.common.Constants;
import com.thinkgem.jeesite.common.config.Global;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.shiro.SecurityUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class SysUserService {

    @Resource
    private UserDao userDao;
    @Resource
    private MenuService menuService;
    @Resource
    private FunctionResService functionResService;

    public Page1<SysUser> findUserByPage(SearchSysUser searchSysUser, int offset, int number) {
        return userDao.findUserByPage(searchSysUser, offset, number);
    }

    public void deleteUser(Object[][] ids) {
        userDao.delete(SysSqlConstants.user_delUser, ids);
    }

    public ResultType save(SysUser sysUser) {
        SysUser exitUser = userDao.getUserByName(sysUser.getName());
        if (exitUser == null) {
            sysUser.prePersist();
            sysUser.setPwd(SystemService.entryptPassword(sysUser.getPwd()));
            userDao.saveUser(sysUser);
            return ResultType.SUCCESS;
        } else {
            return ResultType.EXIST;
        }
    }

    public void edit(SysUser sysUser) {
        sysUser.prePersist();
        if (sysUser.getPwd() != null) {
            userDao.update(sysUser);
        } else {
            userDao.updateSysUserWithOutPwd(sysUser);
        }
    }

    public SysUser getSysUser(String userId) {
        return userDao.getUser(userId);
    }

    @Before(Tx.class)
    public void assignRoles(String userId, String roles) {
        String[] rols = roles.split(",");
        userDao.delete(SysSqlConstants.user_delUserRoles, new Object[][]{{userId}});
        if (StringUtils.isNotEmpty(roles)) {
            Object[][] param = new Object[rols.length][2];
            for (int i = 0; i < rols.length; i++) {
                param[i][0] = userId;
                param[i][1] = rols[i];
            }
            userDao.assignRoles(param);
        }
    }

    public Map<String, String> getUserRoles(String userId) {
        return userDao.getUserRoles(userId);
    }

    public void updatePasswordById(String id, String loginName, String newPassword) {
        userDao.updatePasswordById(SystemService.entryptPassword(newPassword), id);
    }

    public void updateUserLoginInfo(String id) {
        userDao.updateLoginInfo(SecurityUtils.getSubject().getSession().getHost(), new Date(), id);
    }

    public void doAuthorization(SysUser sysUser) {
        Pair<List<Integer>, List<SysOrg>> orgFuns = userDao.getOrgAndFuns(sysUser.getUserId());
        Set<Integer> roleFuns = userDao.getRoleFuns(sysUser.getUserId());
        roleFuns.addAll(orgFuns.getLeft());
        sysUser.setOrgs(orgFuns.getRight());
        sysUser.setFunctions(roleFuns);
        List<SysFunctionPermission> menus = menuService.getMenuList(sysUser);
        List<SysFunctionPermission> resources = functionResService.getResourceFunctions(sysUser);
        Map<String, String> urlMapping = Maps.newHashMap();
        addUrl(menus, urlMapping);
        addUrl(resources, urlMapping);
        UserUtils.putCache(Constants.CACHE_USER_URL_MAPPING, urlMapping);
    }

    private void addUrl(List<SysFunctionPermission> funs, Map<String, String> urlMapping) {
        for (SysFunctionPermission fun : funs) {
            String url = fun.getUrl();
            int index = url.indexOf("?");
            if (index > 0) {
                url = url.substring(0, index);
            }
            urlMapping.put(Global.getAdminPath() + url, "");
        }
    }

    public String getUserOrg(String userId) {
        return userDao.getUserOrg(userId);
    }
}
