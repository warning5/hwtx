package com.hwtx.modules.sys.entity;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.thinkgem.jeesite.common.persistence.DataEntity;

@TableBind(tableName = "sys_org", pkName = "orgId")
public class SysOrg extends DataEntity<SysOrg> {

    private static final long serialVersionUID = 3813684627965638254L;
    public static final SysOrg dao = new SysOrg();

    public String getPid() {
        return get("pid");
    }

    public SysOrg setPid(String pid) {
        set("pid", pid);
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

    public Integer getType() {
        return get("type");
    }

    public void setType(Integer type) {
        set("type", type);
    }

    public Long getUserCount() {
        return get("userCount");
    }

    public void setUserCount(Long userCount) {
        set("userCount", userCount);
    }

    public String getOrgId() {
        return get("orgId");
    }

    public SysOrg setOrgId(String orgId) {
        set("orgId", orgId);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SysOrg)) return false;
        SysOrg sysOrg = (SysOrg) o;
        if (getOrgId() != null ? !getOrgId().equals(sysOrg.getOrgId()) : sysOrg.getOrgId() != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getOrgId() != null ? getOrgId().hashCode() : 0);
        return result;
    }
}