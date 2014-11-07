package com.hwtx.modules.sys.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchSysUser {

	private String userName = null;
	private String startDate = null;
	private String finishDate = null;
	private String orgId = null;
	private String includeUser = null;

	public boolean isNull() {
		return userName == null && startDate == null && finishDate == null && orgId == null;
	}
}
