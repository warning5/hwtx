package com.hwtx.modules.sys.entity;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.thinkgem.jeesite.common.persistence.DataEntity;

@TableBind(tableName = "sys_dict", pkName = "id")
public class Dict extends DataEntity<Dict> {

	public static final Dict dao = new Dict();

	private static final long serialVersionUID = 1L;

	public String getId() {
		return get("id");
	}

	public void setId(String id) {
		set("id", id);
	}

	public String getLabel() {
		return get("label");
	}

	public void setLabel(String label) {
		set("label", label);
	}

	public String getValue() {
		return get("value");
	}

	public void setValue(String value) {
		set("value", value);
	}

	public String getType() {
		return get("type");
	}

	public void setType(String type) {
		set("type", type);
	}

	public String getDescription() {
		return get("description");
	}

	public void setDescription(String description) {
		set("description", description);
	}

	public Integer getSort() {
		return get("sort");
	}

	public void setSort(Integer sort) {
		set("sort", sort);
	}
	
	public String actions = "actions";

	public String getActions() {
		return actions;
	}
}