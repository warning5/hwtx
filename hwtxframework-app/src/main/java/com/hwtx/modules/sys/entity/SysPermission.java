package com.hwtx.modules.sys.entity;

import com.jfinal.ext.plugin.tablebind.NoEntity;
import com.jfinal.plugin.activerecord.Model;
import com.thinkgem.jeesite.common.Constants;
import com.thinkgem.jeesite.common.persistence.DataEntity;
import lombok.Getter;
import lombok.Setter;

@NoEntity
public abstract class SysPermission<T extends Model<?>> extends DataEntity<T> {

    private static final long serialVersionUID = -8731477493587871150L;

    public Integer getPermissionId() {
        return get("permissionId");
    }

    public SysPermission<?> setPermissionId(Integer permissionId) {
        set("permissionId", permissionId);
        return this;
    }

    public String getName() {
        return get("name");
    }

    public void setName(String name) {
        set("name", name);
    }

    public String getDescription() {
        return get("description");
    }

    public void setDescription(String description) {
        set("description", description);
    }

    public String getEnable() {
        return get("enable");
    }

    public void setEnable(String enable) {
        set("enable", enable);
    }

    public Integer getPid() {
        return get("pid");
    }

    public SysPermission<?> setPid(Integer pid) {
        set("pid", pid);
        return this;
    }

    public Integer getSequence() {
        return get("sequence");
    }

    public void setSequence(Integer sequence) {
        set("sequence", sequence);
    }

    public Integer getId() {
        return getPermissionId();
    }

    public boolean isRootMenu() {
        return isRootMenu(getPermissionId());
    }

    public static boolean isRootMenu(Integer id) {
        return id != null && id.equals(Constants.TOPFUNCTIONID);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime
                * result
                + ((getPermissionId() == null) ? 0 : getPermissionId()
                .hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        SysPermission<?> other = (SysPermission<?>) obj;
        if (getPermissionId() == null) {
            if (other.getPermissionId() != null)
                return false;
        } else if (!getPermissionId().equals(other.getPermissionId()))
            return false;
        return true;
    }

    @Setter
    @Getter
    SysPermission<?> parent;
}
