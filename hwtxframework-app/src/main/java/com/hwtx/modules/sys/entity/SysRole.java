package com.hwtx.modules.sys.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.common.collect.Lists;
import com.jfinal.ext.plugin.tablebind.TableBind;
import com.thinkgem.jeesite.common.persistence.DataEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@TableBind(tableName = "sys_role", pkName = "roleId")
public class SysRole extends DataEntity<SysRole> {

    public static final SysRole dao = new SysRole();

    private static final long serialVersionUID = 602134895312782477L;

    @JSONField(serialize = false)
    @Getter
    @Setter
    private List<SysRole> childRoles = Lists.newCopyOnWriteArrayList();

    @Getter
    @Setter
    String pname;

    public void setParent(String pid) {
        setPid(pid);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((getRoleId() == null) ? 0 : getRoleId().hashCode());
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
        SysRole other = (SysRole) obj;
        if (getRoleId() == null) {
            if (other.getRoleId() != null)
                return false;
        } else if (!getRoleId().equals(other.getRoleId()))
            return false;
        return true;
    }

    public String getRoleId() {
        return get("roleId");
    }

    public String getName() {
        return get("name");
    }

    public String getDescription() {
        return get("description");
    }

    public String getPid() {
        return get("pid");
    }

    public String getType() {
        return get("type");
    }

    public void setName(String name) {
        set("name", name);
    }

    public SysRole setDescription(String description) {
        set("description", description);
        return this;
    }

    public SysRole setRoleId(String roleId) {
        set("roleId", roleId);
        return this;
    }

    public SysRole setPid(String pid) {
        set("pid", pid);
        return this;
    }

    public SysRole setType(String type) {
        set("type", type);
        return this;
    }
}
