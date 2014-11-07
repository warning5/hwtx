package com.hwtx.modules.sys.entity;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.thinkgem.jeesite.common.persistence.DataEntity;

@TableBind(tableName = "sys_biz_permission_param", pkName = "id")
public class SysBizPermissionParam extends DataEntity<SysBizPermissionParam> {

	private static final long serialVersionUID = -1924839397403542145L;

	public static final SysBizPermissionParam dao = new SysBizPermissionParam();

	public String getPermissionId() {
		return get("permissionId");
	}

	public void setPermissionId(String permissionId) {
		set("permissionId", permissionId);
	}

	public String getName() {
		return get("name");
	}

	public void setName(String name) {
		set("name", name);
	}

	public String getType() {
		return get("type");
	}

	public SysBizPermissionParam setType(String type) {
		set("type", type);
		return this;
	}

	public Integer getSerial() {
		return get("serial");
	}

	public void setSerial(Integer serial) {
		set("serial", serial);
	}

}
