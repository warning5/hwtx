/**
 * Copyright &copy; 2012-2013 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.hwtx.modules.sys.utils;

import com.google.common.collect.Maps;
import com.hwtx.modules.sys.dao.UserDao;
import com.hwtx.modules.sys.entity.SysFunctionPermission;
import com.hwtx.modules.sys.entity.SysUser;
import com.hwtx.modules.sys.security.SystemAuthorizingRealm.Principal;
import com.hwtx.modules.sys.service.MenuService;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.IocContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.subject.Subject;

import java.util.List;
import java.util.Map;

/**
 * 用户工具类
 *
 * @author ThinkGem
 * @version 2013-5-29
 */
public class UserUtils extends BaseService {

    public static final String CACHE_USER = "user";

    public static SysUser getUser() {
        SysUser user = (SysUser) getCache(CACHE_USER);
        if (user == null) {
            try {
                Subject subject = SecurityUtils.getSubject();
                Principal principal = (Principal) subject.getPrincipal();
                if (principal != null) {
                    UserDao userDao = IocContextHolder.getComponent(UserDao.class.getSimpleName());
                    user = userDao.getUser(principal.getSysUser().getUserId());
                    putCache(CACHE_USER, user);
                }
            } catch (UnavailableSecurityManagerException e) {

            } catch (InvalidSessionException e) {

            }
        }
        if (user == null) {
            user = new SysUser();
            try {
                SecurityUtils.getSubject().logout();
            } catch (UnavailableSecurityManagerException e) {

            } catch (InvalidSessionException e) {

            }
        }
        return user;
    }

    public static SysUser getUser(boolean isRefresh) {
        if (isRefresh) {
            removeCache(CACHE_USER);
        }
        return getUser();
    }

    public static SysUser getUserById(String id) {
        if (StringUtils.isNotBlank(id)) {
            UserDao userDao = IocContextHolder.getComponent(UserDao.class
                    .getSimpleName());
            return userDao.getUser(id);
        } else {
            return null;
        }
    }

    // ============== User Cache ==============

    public static Object getCache(String key) {
        return getCache(key, null);
    }

    public static Object getCache(String key, Object defaultValue) {
        Object obj = getCacheMap().get(key);
        return obj == null ? defaultValue : obj;
    }

    public static void putCache(String key, Object value) {
        getCacheMap().put(key, value);
    }

    public static void removeCache(String key) {
        getCacheMap().remove(key);
    }

    public static Map<String, Object> getCacheMap() {
        Map<String, Object> map = Maps.newHashMap();
        try {
            Subject subject = SecurityUtils.getSubject();
            Principal principal = (Principal) subject.getPrincipal();
            return principal != null ? principal.getCacheMap() : map;
        } catch (UnavailableSecurityManagerException e) {

        } catch (InvalidSessionException e) {

        }
        return map;
    }

}
