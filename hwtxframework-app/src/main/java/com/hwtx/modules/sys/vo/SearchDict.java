package com.hwtx.modules.sys.vo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SearchDict {

	String types;
	String label;

	public boolean isNull() {
		return types == null && label == null;
	}

}