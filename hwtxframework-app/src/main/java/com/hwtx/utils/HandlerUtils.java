package com.hwtx.utils;

public class HandlerUtils {

	public static String getModulePath(String raw) {
		int index = raw.indexOf("WEB-INF");
		if (index > 0) {
			return raw.substring(index - 1).replace("\\", "/");
		}
		raw = raw.replace("\\", "/");
		return raw;
	}

}
