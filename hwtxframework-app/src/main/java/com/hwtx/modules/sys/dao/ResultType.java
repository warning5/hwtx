package com.hwtx.modules.sys.dao;

import com.thinkgem.jeesite.common.Constants;

public enum ResultType {
	UPDATE(Constants.RESULT_UPDATE), INSERT(Constants.RESULT_INSERT), ERROR("error"), SUCCESS("success"), EXIST("exist");

	private String name;

	private ResultType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}