package com.hwtx.modules.sys.entity;

import com.jfinal.ext.plugin.tablebind.TableBind;

@TableBind(tableName = "dict_province", pkName = "id")
public class Province extends com.hwtx.modules.sys.entity.Region<Province> {

	private static final long serialVersionUID = -5917633810255007670L;
	
	public static final Province dao = new Province();

}
