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

package com.hwtx.tool.pipe;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import com.hwtx.tool.DbKing;
import com.hwtx.tool.Table;
import com.hwtx.tool.config.XmlConfig;
import com.hwtx.tool.dbsource.DbSource;
import com.hwtx.tool.exception.DbKingException;
import com.hwtx.tool.util.DbUtil;

public class EmptyTableRows {

	private static final String CONFIG_NAME = "importer";

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		Connection con = null;
		try {
			DbSource dbSource = XmlConfig.getDbSource(CONFIG_NAME);
			System.out.println("you will operate the following database:");
			System.out.println(dbSource);
			System.out
					.println("Please confirm the database you really want to operate, (Y)es for going on or (N)o for breaking?");
			while (true) {
				int ch = System.in.read();
				if (ch == 'Y' || ch == 'y') {
					break;
				} else if (ch == 'N' || ch == 'n') {
					return;
				}
			}
			con = dbSource.getConnection();
			DbKing dbKing = new DbKing(con);
			List<Table> tableList = dbKing.getTableList(true);
			for (int i = tableList.size() - 1; i >= 0; i--) {
				Table table = tableList.get(i);
				String tableName = table.getName();
				String sql = "delete from " + tableName;
				dbKing.execute(sql);
				System.out.println(tableName + "'s rows have been deleted ");
			}
		} catch (IOException e) {
			throw new DbKingException(e);
		} finally {
			DbUtil.closeConnection(con);
		}
		long end = System.currentTimeMillis();
		System.out
				.println("it took up " + ((end - start) / 1000) + " minutes");
	}
}
