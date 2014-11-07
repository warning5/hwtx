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

package com.hwtx.tool.oom;

public class DeleteSql {

	private String tableName;

	private WhereClause whereClause;

	public String getTableName() {
		return tableName;
	}

	public DeleteSql setTableName(String tableName) {
		this.tableName = tableName;
		return this;
	}

	public WhereClause getWhereClause() {
		return whereClause;
	}

	public DeleteSql setWhereClause(WhereClause whereClause) {
		this.whereClause = whereClause;
		return this;
	}

}
