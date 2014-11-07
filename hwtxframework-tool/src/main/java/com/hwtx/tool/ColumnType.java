/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 YU YUE, SOSO STUDIO, wuyuetiger@gmail.com
 *
 * License: GNU Lesser General Public License (LGPL)
 * 
 * Source code availability:
 *  https://github.com/wuyuetiger/dbking
 *  https://code.csdn.net/tigeryu/dbking
 *  https://git.oschina.net/db-unifier/dbking
 */

package com.hwtx.tool;

public enum ColumnType {

	STRING("String", "java.lang.String"),
    INTEGER("Integer", "java.lang.Integer"),
    LONG("LONG","java.lang.Long"),
    FLOAT("FLOAT","java.lang.Float"),
    DOUBLE("DOUBLE","java.lang.Double"),
    BOOLEAN("BOOLEAN","java.math.BigDecimal"),
    BIGDECIMAL("BigDecimal","java.lang.Boolean"),
    NUMBER("BigDecimal","java.lang.NUMBER"),
    TIMESTAMP("Timestamp", "java.sql.Timestamp"),
    DATE("Date", "java.util.Date"),
    CLOB("Clob", "char[]"),
    BLOB("Blob","byte[]"),
    UNKNOWN("", "");

	public static final int MAX_STRING_SIZE = 2000;

	private final String name;

	private final String type;

	private ColumnType(String name, String type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

}
