package com.hwtx.modules.sys.entity;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.thinkgem.jeesite.common.Constants;

@TableBind(tableName = "sys_function_permission", pkName = "permissionId")
public class SysFunctionPermission extends SysPermission<SysFunctionPermission> {

    private static final long serialVersionUID = -1924839397403542145L;

    public static SysFunctionPermission dao = new SysFunctionPermission();

    public String getUrl() {
        return get("url");
    }

    public void setUrl(String url) {
        set("url", url);
    }

    public String getTarget() {
        return get("target");
    }

    public void setTarget(String target) {
        set("target", target);
    }

    public String getResId() {
        return get("resId");
    }

    public void setResId(String resId) {
        set("resId", resId);
    }

    public String getIcon() {
        return get("icon");
    }

    public void setIcon(String icon) {
        set("icon", icon);
    }

    public String getMenu() {
        return get("menu");
    }

    public SysFunctionPermission setMenu(String menu) {
        set("menu", menu);
        return this;
    }

    public boolean isTop() {
        return getPid() == Constants.TOPFUNCTIONID;
    }

    public String getAuth() {
        return get("auth");
    }

    public void setAuth(String auth) {
        set("auth", auth);
    }
}