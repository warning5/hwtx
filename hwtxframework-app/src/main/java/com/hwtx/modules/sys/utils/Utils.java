package com.hwtx.modules.sys.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.hwtx.modules.sys.entity.SysBizPermission;
import com.hwtx.modules.sys.entity.SysFunctionPermission;
import com.hwtx.modules.sys.entity.SysPermission;
import com.hwtx.modules.sys.entity.SysRole;
import com.thinkgem.jeesite.common.Constants;

public class Utils {

    private static class Holder {
        static SysFunctionPermission instance = new SysFunctionPermission();
        static SysBizPermission biz = new SysBizPermission();
        static SysRole role = new SysRole();
    }

    public static SysFunctionPermission getTopFunctionPermission() {
        SysFunctionPermission instance = Holder.instance;
        instance = new SysFunctionPermission();
        instance.setPermissionId(Constants.TOPFUNCTIONID);
        instance.setName(Constants.TOPMENUNAME);
        return instance;
    }

    public static SysBizPermission getTopBizPermission() {
        SysBizPermission instance = Holder.biz;
        instance = new SysBizPermission();
        instance.setPermissionId(Constants.TOPFUNCTIONID);
        instance.setName(Constants.TOPBIZNAME);
        return instance;
    }

    public static SysRole getTopRole() {
        SysRole instance = Holder.role;
        instance = new SysRole();
        instance.setRoleId(Constants.TOPROLEID);
        instance.setName(Constants.TOPROLENAME);
        return instance;
    }

    public static String getUrl(HttpServletRequest req) {
        String contextPath = req.getContextPath();
        String uri = req.getRequestURI().substring(contextPath.length());
        if (uri.indexOf(";") > 0) {
            uri = uri.substring(0, uri.indexOf(";"));
        }
        return uri;
    }

    public static void sortPermission(
            List<? extends SysPermission<?>> permissions) {
        Collections.sort(permissions, new Comparator<SysPermission<?>>() {

            @Override
            public int compare(SysPermission<?> o1, SysPermission<?> o2) {
                if (o1.getSequence() > o2.getSequence()) {
                    return 1;
                } else if (o1.getSequence() < o2.getSequence()) {
                    return -1;
                }
                return 0;
            }
        });
    }
}
