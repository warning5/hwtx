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

package com.hwtx.tool.dbsource;

import java.sql.Connection;

public class ConnectionDbSource implements DbSource {

	private Connection con;

	private String schema;

	public ConnectionDbSource(Connection con, String schema) {
		this.con = con;
		this.schema = schema;
	}

	public ConnectionDbSource(Connection con) {
		this.con = con;
	}

	@Override
	public Connection getConnection() {
		return con;
	}
	
	@Override
	public String getSchema() {
		return schema;
	}

}
