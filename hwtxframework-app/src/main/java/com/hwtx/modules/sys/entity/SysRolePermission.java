package com.hwtx.modules.sys.entity;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.thinkgem.jeesite.common.persistence.DataEntity;

@TableBind(tableName = "sys_role_permission", pkName = "id")
public class SysRolePermission extends DataEntity<SysRolePermission> {

	private static final long serialVersionUID = 4060094925557891981L;

	public static final SysRolePermission dao = new SysRolePermission();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((getPermissionId() == null) ? 0 : getPermissionId().hashCode());
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
		SysRolePermission other = (SysRolePermission) obj;
		if (getPermissionId() == null) {
			if (other.getPermissionId() != null)
				return false;
		} else if (!getPermissionId().equals(other.getPermissionId()))
			return false;
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

	public Integer getPermissionId() {
		return get("permissionId");
	}

	public SysRolePermission setPermissionId(Integer permissionId) {
		set("permissionId", permissionId);
		return this;
	}

	public SysRolePermission setRoleId(String roleId) {
		set("roleId", roleId);
		return this;
	}
}
