package com.hwtx.modules.sys.entity;

import com.jfinal.ext.plugin.tablebind.TableBind;

@TableBind(tableName = "sys_biz_permission", pkName = "permissionId")
public class SysBizPermission extends SysPermission<SysBizPermission> {

	private static final long serialVersionUID = -1924839397403542145L;

	public static final SysBizPermission dao = new SysBizPermission();
	
	public String getPermission_def() {
		return get("permission_def");
	}

	public void setPermission_def(String permission_def) {
		set("permission_def", permission_def);
	}

	public String getType() {
		return get("type");
	}

	public SysBizPermission setType(String type) {
		set("type", type);
		return this;
	}
}
