package com.hwtx.modules.sys.web;

import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.thinkgem.jeesite.common.web.BaseController;

/**
 * 标签Controller
 */
@ControllerBind(controllerKey = "${adminPath}/tag")
public class TagController extends BaseController {

	/**
	 * 树结构选择标签（treeselect.tag）
	 */
	@ActionKey(value = "treeselect")
	public void treeselect() {
		setAttr("url", getPara("url")); // 树结构数据URL
		setAttr("extId", getPara("extId")); // 排除的编号ID
		setAttr("checked", getPara("checked")); // 是否可复选
		setAttr("selectIds", getPara("selectIds")); // 指定默认选中的ID
		setAttr("module", getPara("module")); // 过滤栏目模型（仅针对CMS的Category树）
		setAttr("key", getPara("key"));
		render("/modules/sys/tagTreeselect.jsp");
	}

	/**
	 * 图标选择标签（iconselect.tag）
	 */
	@ActionKey(value = "iconselect")
	public void iconselect() {
		setAttr("value", getPara("value"));
		setAttr("key", getPara("key"));
		render("/modules/sys/tagIconselect.jsp");
	}

}
