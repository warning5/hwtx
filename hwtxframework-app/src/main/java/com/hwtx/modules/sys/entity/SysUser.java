package com.hwtx.modules.sys.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.jfinal.ext.plugin.tablebind.TableBind;
import com.thinkgem.jeesite.common.persistence.DataEntity;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@TableBind(tableName = "sys_user", pkName = "userId")
public class SysUser extends DataEntity<SysUser> {

    @Override
    public String toString() {
        return "SysUser [name=" + getName() + "]";
    }

    public static final SysUser dao = new SysUser();
    private static final long serialVersionUID = 1514002129862515308L;
    public static final String superAdmin = "19820224";

    @Setter
    @Getter
    private transient Set<Integer> functions = Sets.newHashSet();
    @Getter
    private transient List<SysFunctionPermission> menus = Lists.newArrayList();

    public void setMenus(List<SysFunctionPermission> menus) {
        this.menus = menus;
    }

    @Getter
    @Setter
    private transient List<SysOrg> orgs = Lists.newArrayList();

    @JSONField(serialize = false)
    public boolean isAdmin() {
        return superAdmin.equals(getUserId());
    }

    public static boolean isAdmin(String id) {
        return id != null && superAdmin.equals(id);
    }

    public String getPwd() {
        return get("pwd");
    }

    public void setPwd(String pwd) {
        set("pwd", pwd);
    }

    public String getName() {
        return get("name");
    }

    public String getUserId() {
        return get("userId");
    }

    public void setUserId(String userId) {
        set("userId", userId);
    }

    @JSONField(serialzeFeatures = {SerializerFeature.WriteMapNullValue})
    public String getLogin_ip() {
        return get("login_ip");
    }

    @JSONField(format = "yyyy-MM-dd HH:mm:ss", serialzeFeatures = {SerializerFeature.WriteMapNullValue})
    public Timestamp getLogin_date() {
        return get("login_date");
    }

    public String actions = "actions";

    public String getActions() {
        return actions;
    }
}