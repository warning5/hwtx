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

import com.hwtx.tool.config.XmlConfig;
import com.hwtx.tool.dbsource.ConnectionDbSource;
import com.hwtx.tool.dbsource.DbSource;
import com.hwtx.tool.exception.DbKingException;
import com.hwtx.tool.feature.DbFeature;
import com.hwtx.tool.oom.*;
import com.hwtx.tool.util.DbUtil;
import com.hwtx.tool.util.IoUtil;

import java.io.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class DbKing {

    private static final String SEQ_TABLE_NAME = "DBKING_SEQ";

    private static final String SEQ_NAME_COLUMN_NAME = "ST_SEQ_NAME";

    private static final String SEQ_VALUE_COLUMN_NAME = "NM_SEQ_VALUE";

    private DbSource dbSource;

    public DbKing() {
        DbSource dbSource = XmlConfig.getDbSource("");
        this.dbSource = dbSource;
    }

    public DbKing(String dbSourceName) {
        DbSource dbSource = XmlConfig.getDbSource(dbSourceName);
        this.dbSource = dbSource;
    }

    public DbKing(DbSource dbSource) {
        this.dbSource = dbSource;
    }

    public DbKing(Connection con) {
        this.dbSource = new ConnectionDbSource(con);
    }

    public void printDbInfo() {
        Connection con = null;
        try {
            con = dbSource.getConnection();
            DatabaseMetaData dmd = con.getMetaData();
            System.out.println();
            System.out.println("DatabaseProductName: "
                    + dmd.getDatabaseProductName());
            System.out.println("DatabaseProductVersion: "
                    + dmd.getDatabaseProductVersion());
            System.out.println("DatabaseMajorVersion: "
                    + dmd.getDatabaseMajorVersion());
            System.out.println("DatabaseMinorVersion: "
                    + dmd.getDatabaseMinorVersion());
            System.out.println("DriverName: " + dmd.getDriverName());
            System.out.println("DriverMajorVersion: "
                    + dmd.getDriverMajorVersion());
            System.out.println("DriverMinorVersion: "
                    + dmd.getDriverMinorVersion());
            System.out.println("DriverVersion: " + dmd.getDriverVersion());
            System.out
                    .println("JDBCMajorVersion: " + dmd.getJDBCMajorVersion());
            System.out
                    .println("JDBCMinorVersion: " + dmd.getJDBCMinorVersion());

            System.out.println();
        } catch (SQLException e) {
            throw new DbKingException(e);
        } finally {
            if (!(dbSource instanceof ConnectionDbSource)) {
                DbUtil.closeConnection(con);
            }
        }
    }

    private Column getColumn(ResultSet columnRs, Set<String> pkSet)
            throws SQLException {
        String columnName = columnRs.getString("COLUMN_NAME");
        short dataType = columnRs.getShort("DATA_TYPE");
        int size = columnRs.getInt("COLUMN_SIZE");
        int scale = columnRs.getInt("DECIMAL_DIGITS");
        int nullable = columnRs.getInt("NULLABLE");
        Column column = new Column(columnName, DbUtil.getColumnType(dataType));
        column.setSize(size);
        column.setPrecision(size);
        column.setScale(scale);
        column.setNullable(nullable != ResultSetMetaData.columnNoNulls);
        if (pkSet != null) {
            column.setPrimaryKey(pkSet.contains(columnName));
        }
        return column;
    }

    private int getDepth(Table table, Set<String> tableNameSet) {
        String tableName = table.getName();
        tableNameSet.add(tableName);
        List<Table> parentTableList = table.getParentTableList();
        int depth = 0;
        for (Table parentTable : parentTableList) {
            String parentTableName = parentTable.getName();
            if (tableNameSet.contains(parentTableName)) {
                continue;
            }
            depth = Math.max(depth, getDepth(parentTable, tableNameSet));
        }
        tableNameSet.remove(tableName);
        return depth + 1;
    }

    public List<Table> getTableList(boolean sortByFk) {
        Connection con = null;
        try {
            con = dbSource.getConnection();
            List<Table> tableList = new ArrayList<Table>();
            Map<String, Table> tableMap = new HashMap<String, Table>();
            ResultSet tableRs = null;
            try {
                DatabaseMetaData dmd = con.getMetaData();
                DbFeature dbFeature = DbFeature.getInstance(dmd);
                String schema = dbSource.getSchema();
                if (schema == null) {
                    schema = dbFeature.defaultSchema();
                }
                String[] types = {"TABLE"};
                tableRs = dmd.getTables(null, schema, "%", types);
                while (tableRs.next()) {
                    String tableName = tableRs.getString("TABLE_NAME");
                    if (tableName.startsWith("BIN$")) {
                        continue;
                    }
                    Set<String> pkSet = new HashSet<String>();
                    ResultSet pkRs = null;
                    try {
                        pkRs = dmd.getPrimaryKeys(null, schema, tableName);
                        while (pkRs.next()) {
                            String columnName = pkRs.getString("COLUMN_NAME");
                            pkSet.add(columnName);
                        }
                    } finally {
                        DbUtil.closeResultSet(pkRs);
                    }
                    List<Column> columnList = new ArrayList<Column>();
                    ResultSet columnRs = null;
                    try {
                        columnRs = dmd.getColumns(null, schema, tableName, "%");
                        while (columnRs.next()) {
                            Column column = getColumn(columnRs, pkSet);
                            columnList.add(column);
                        }
                    } finally {
                        DbUtil.closeResultSet(columnRs);
                    }
                    Table table = new Table(tableName);
                    table.addColumnList(columnList);
                    tableList.add(table);
                    tableMap.put(tableName, table);
                }
                if (sortByFk) {
                    for (Table table : tableList) {
                        String tableName = table.getName();
                        ResultSet fkRs = null;
                        try {
                            fkRs = dmd.getImportedKeys(null, schema, tableName);
                            while (fkRs.next()) {
                                String parentTableName = fkRs
                                        .getString("PKTABLE_NAME");
                                Table parentTable = tableMap
                                        .get(parentTableName);
                                table.addParentTable(parentTable);
                            }
                        } finally {
                            DbUtil.closeResultSet(fkRs);
                        }
                    }
                    for (Table table : tableList) {
                        int depth = getDepth(table, new HashSet<String>());
                        table.setDepth(depth);
                    }
                    Collections.sort(tableList);
                }
            } finally {
                DbUtil.closeResultSet(tableRs);
            }
            for (Table table : tableList) {
                table.setName(table.getName().toUpperCase());
                for (Column column : table.getColumnList()) {
                    column.setName(column.getName().toUpperCase());
                }
            }
            return tableList;
        } catch (SQLException e) {
            throw new DbKingException(e);
        } finally {
            if (!(dbSource instanceof ConnectionDbSource)) {
                DbUtil.closeConnection(con);
            }
        }
    }

    public List<Table> getTableList() {
        return getTableList(false);
    }

    public Table getTable(String tableName) {
        Connection con = null;
        try {
            con = dbSource.getConnection();
            DatabaseMetaData dmd = con.getMetaData();
            DbFeature dbFeature = DbFeature.getInstance(dmd);
            String schema = dbSource.getSchema();
            if (schema == null) {
                schema = dbFeature.defaultSchema();
            }
//            tableName = dbFeature.defaultCaps(tableName);
            Set<String> pkSet = new HashSet<String>();
            ResultSet pkRs = null;
            try {
                pkRs = dmd.getPrimaryKeys(null, schema, tableName);
                while (pkRs.next()) {
                    String columnName = pkRs.getString("COLUMN_NAME");
                    pkSet.add(columnName);
                }
            } finally {
                DbUtil.closeResultSet(pkRs);
            }
            List<Column> columnList = new ArrayList<Column>();
            ResultSet columnRs = null;
            try {
                columnRs = dmd.getColumns(null, schema, tableName, "%");
                while (columnRs.next()) {
                    Column column = getColumn(columnRs, pkSet);
                    columnList.add(column);
                }
            } finally {
                DbUtil.closeResultSet(columnRs);
            }
            if (columnList.size() > 0) {
                Table table = new Table(tableName);
                table.addColumnList(columnList);
                for (Column column : table.getColumnList()) {
                    column.setName(column.getName());
                }
                return table;
            } else {
                return null;
            }
        } catch (SQLException e) {
            return null;
        } finally {
            if (!(dbSource instanceof ConnectionDbSource)) {
                DbUtil.closeConnection(con);
            }
        }
    }

    public List<Table> getViewList() {
        Connection con = null;
        try {
            con = dbSource.getConnection();
            List<Table> viewList = new ArrayList<Table>();
            ResultSet viewRs = null;
            try {
                DatabaseMetaData dmd = con.getMetaData();
                DbFeature dbFeature = DbFeature.getInstance(dmd);
                String schema = dbSource.getSchema();
                if (schema == null) {
                    schema = dbFeature.defaultSchema();
                }
                String[] types = {"VIEW"};
                viewRs = dmd.getTables(null, schema, "%", types);
                while (viewRs.next()) {
                    String viewName = viewRs.getString("TABLE_NAME");
                    List<Column> columnList = new ArrayList<Column>();
                    ResultSet columnRs = null;
                    try {
                        columnRs = dmd.getColumns(null, schema, viewName, "%");
                        while (columnRs.next()) {
                            Column column = getColumn(columnRs, null);
                            columnList.add(column);
                        }
                    } finally {
                        DbUtil.closeResultSet(columnRs);
                    }
                    Table view = new Table(viewName);
                    view.setTable(false);
                    view.addColumnList(columnList);
                    viewList.add(view);
                }
            } finally {
                DbUtil.closeResultSet(viewRs);
            }
            for (Table view : viewList) {
                view.setName(view.getName().toUpperCase());
                for (Column column : view.getColumnList()) {
                    column.setName(column.getName().toUpperCase());
                }
            }
            return viewList;
        } catch (SQLException e) {
            throw new DbKingException(e);
        } finally {
            if (!(dbSource instanceof ConnectionDbSource)) {
                DbUtil.closeConnection(con);
            }
        }
    }

    public Table getView(String viewName) {
        Connection con = null;
        try {
            con = dbSource.getConnection();
            DatabaseMetaData dmd = con.getMetaData();
            DbFeature dbFeature = DbFeature.getInstance(dmd);
            String schema = dbSource.getSchema();
            if (schema == null) {
                schema = dbFeature.defaultSchema();
            }
            viewName = dbFeature.defaultCaps(viewName);
            List<Column> columnList = new ArrayList<Column>();
            ResultSet columnRs = null;
            try {
                columnRs = dmd.getColumns(null, schema, viewName, "%");
                while (columnRs.next()) {
                    Column column = getColumn(columnRs, null);
                    columnList.add(column);
                }
            } finally {
                DbUtil.closeResultSet(columnRs);
            }
            if (columnList.size() > 0) {
                Table view = new Table(viewName.toUpperCase());
                view.setTable(false);
                view.addColumnList(columnList);
                for (Column column : view.getColumnList()) {
                    column.setName(column.getName().toUpperCase());
                }
                return view;
            } else {
                return null;
            }
        } catch (SQLException e) {
            return null;
        } finally {
            if (!(dbSource instanceof ConnectionDbSource)) {
                DbUtil.closeConnection(con);
            }
        }
    }

    public void createTable(Table table) {
        Connection con = null;
        Statement statement = null;
        try {
            con = dbSource.getConnection();
            DatabaseMetaData dmd = con.getMetaData();
            DbFeature dbFeature = DbFeature.getInstance(dmd);
            StringBuilder sb = new StringBuilder();
            StringBuilder sbPk = new StringBuilder();
            sb.append("create table ").append(table.getName()).append("(");
            List<Column> columnList = table.getColumnList();
            for (int i = 0; i < columnList.size(); i++) {
                Column column = (Column) columnList.get(i);
                if (column.isPrimaryKey()) {
                    if (sbPk.length() > 0) {
                        sbPk.append(", ");
                    }
                    sbPk.append(column.getName());
                }
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append(column.getName()).append(" ");
                ColumnType type = column.getType();
                if (type == ColumnType.STRING) {
                    sb.append(dbFeature.getStringDbType(column.getSize()));
                } else if (type == ColumnType.NUMBER) {
                    sb.append(dbFeature.getNumberDbType(column.getPrecision(),
                            column.getScale()));
                } else if (type == ColumnType.TIMESTAMP) {
                    sb.append(dbFeature.getTimestampDbType());
                } else if (type == ColumnType.CLOB) {
                    sb.append(dbFeature.getClobDbType());
                } else if (type == ColumnType.BLOB) {
                    sb.append(dbFeature.getBlobDbType());
                }
                if (!column.isNullable() || column.isPrimaryKey()) {
                    sb.append(" not null");
                } else if (!dbFeature.allowNullByDefault()) {
                    sb.append(" null");
                }
            }
            if (sbPk.length() > 0) {
                sb.append(dbFeature.constraintClause(sbPk));
            }
            sb.append(")");
            String sql = sb.toString();
            DbUtil.printSql(sql, null);
            statement = con.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new DbKingException(e);
        } finally {
            DbUtil.closeStatement(statement);
            if (!(dbSource instanceof ConnectionDbSource)) {
                DbUtil.closeConnection(con);
            }
        }

    }

    public void dropTable(String tableName) {
        String sql = "drop table " + tableName;
        execute(sql);
    }

    public RowList query(String sql, Values values, boolean containsLob,
                         int pageSize, int pageNumber) {
        if (values == null) {
            values = new Values();
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = dbSource.getConnection();
            DatabaseMetaData dmd = con.getMetaData();
            DbFeature dbFeature = DbFeature.getInstance(dmd);
            // split sql into two part
            String mainSubSql = sql;
            String orderBySubSql = "";
            String upperCaseSql = sql.toUpperCase();
            int orderByPos = upperCaseSql.indexOf("ORDER BY");
            while (orderByPos >= 0) {
                String orderByStr = upperCaseSql.substring(orderByPos);
                int count = 0;
                for (int i = 0; i < orderByStr.length(); i++) {
                    if (orderByStr.charAt(i) == '\'') {
                        count++;
                    }
                }
                if (count % 2 == 0) {
                    mainSubSql = sql.substring(0, orderByPos);
                    orderBySubSql = sql.substring(orderByPos);
                    break;
                } else {
                    orderByPos = upperCaseSql.indexOf("ORDER BY",
                            orderByPos + 1);
                }
            }
            // pagination compute
            int totalRowCount = dbFeature.getTotalRowCount(con, mainSubSql,
                    values);
            int totalPageCount = (totalRowCount + pageSize - 1) / pageSize;
            pageNumber = Math.max(1, Math.min(totalPageCount, pageNumber));
            // construct pagination sql
            int startPos = pageSize * (pageNumber - 1);
            int endPos = startPos + pageSize;
            String paginationSql = dbFeature.getPaginationSql(mainSubSql,
                    orderBySubSql, startPos, endPos);
            // query
            RowList rowList = new RowList();
            rowList.setPageNumber(pageNumber);
            rowList.setPageSize(pageSize);
            rowList.setTotalRowCount(totalRowCount);
            rowList.setTotalPageCount(totalPageCount);
            if (paginationSql == null) {
                DbUtil.printSql(sql, values);
                ps = con.prepareStatement(sql,
                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);
            } else {
                DbUtil.printSql(paginationSql, values);
                ps = con.prepareStatement(paginationSql);
            }
            DbUtil.setColumnValue(ps, 1, values);
            rs = ps.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                String columnName = rsmd.getColumnLabel(i + 1);
                columnName = columnName.toUpperCase();
                rowList.addColumnName(columnName);
            }
            if (paginationSql == null) {
                if (!rs.absolute(pageSize * (pageNumber - 1) + 1)) {
                    throw new DbKingException("abnormal resultset location");
                } else {
                    rs.previous();
                }
            }
            while (rs.next()) {
                if (paginationSql == null) {
                    if (rs.getRow() > pageSize * pageNumber) {
                        break;
                    }
                }
                Row row = new Row(rowList);
                for (int i = 0; i < columnCount; i++) {
                    String columnName = rsmd.getColumnLabel(i + 1);
                    columnName = columnName.toUpperCase();
                    int dataType = rsmd.getColumnType(i + 1);
                    ColumnType type = DbUtil.getColumnType(dataType);
                    Object value = null;
                    if (type == ColumnType.STRING) {
                        value = rs.getString(i + 1);
                    } else if (type == ColumnType.NUMBER) {
                        value = rs.getBigDecimal(i + 1);
                    } else if (type == ColumnType.TIMESTAMP) {
                        value = rs.getTimestamp(i + 1);
                    } else if (type == ColumnType.CLOB) {
                        if (containsLob) {
                            Reader reader = rs.getCharacterStream(i + 1);
                            if (reader != null) {
                                try {
                                    value = IoUtil.convertStream(reader);
                                } catch (IOException e) {
                                    throw new DbKingException(e);
                                } finally {
                                    IoUtil.closeReader(reader);
                                }
                            }
                        }
                    } else if (type == ColumnType.BLOB) {
                        if (containsLob) {
                            InputStream is = rs.getBinaryStream(i + 1);
                            if (is != null) {
                                try {
                                    value = IoUtil.convertStream(is);
                                } catch (IOException e) {
                                    throw new DbKingException(e);
                                } finally {
                                    IoUtil.closeInputStream(is);
                                }
                            }
                        }
                    } else {
                        throw new DbKingException("not support data type");
                    }
                    row.addValue(columnName, value);
                }
                rowList.add(row);
            }
            return rowList;
        } catch (SQLException e) {
            throw new DbKingException(e);
        } finally {
            DbUtil.closeResultSet(rs);
            DbUtil.closeStatement(ps);
            if (!(dbSource instanceof ConnectionDbSource)) {
                DbUtil.closeConnection(con);
            }
        }
    }

    public RowList query(String sql, Values values, int pageSize,
                         int pageNumber) {
        return query(sql, values, false, pageSize, pageNumber);
    }

    public RowList query(String sql, int pageSize, int pageNumber) {
        Values values = new Values();
        return query(sql, values, false, pageSize, pageNumber);
    }

    public RowList query(String sql, Values values, boolean containsLob) {
        if (values == null) {
            values = new Values();
        }
        DbUtil.printSql(sql, values);
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            // query
            con = dbSource.getConnection();
            RowList rowList = new RowList();
            ps = con.prepareStatement(sql);
            DbUtil.setColumnValue(ps, 1, values);
            rs = ps.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                String columnName = rsmd.getColumnLabel(i + 1);
                columnName = columnName.toUpperCase();
                rowList.addColumnName(columnName);
            }
            while (rs.next()) {
                Row row = new Row(rowList);
                for (int i = 0; i < columnCount; i++) {
                    String columnName = rsmd.getColumnLabel(i + 1);
                    columnName = columnName.toUpperCase();
                    int dataType = rsmd.getColumnType(i + 1);
                    ColumnType type = DbUtil.getColumnType(dataType);
                    Object value = null;
                    if (type == ColumnType.STRING) {
                        value = rs.getString(i + 1);
                    } else if (type == ColumnType.NUMBER) {
                        value = rs.getBigDecimal(i + 1);
                    } else if (type == ColumnType.TIMESTAMP) {
                        value = rs.getTimestamp(i + 1);
                    } else if (type == ColumnType.CLOB) {
                        if (containsLob) {
                            Reader reader = rs.getCharacterStream(i + 1);
                            if (reader != null) {
                                try {
                                    value = IoUtil.convertStream(reader);
                                } catch (IOException e) {
                                    throw new DbKingException(e);
                                } finally {
                                    IoUtil.closeReader(reader);
                                }
                            }
                        }
                    } else if (type == ColumnType.BLOB) {
                        if (containsLob) {
                            InputStream is = rs.getBinaryStream(i + 1);
                            if (is != null) {
                                try {
                                    value = IoUtil.convertStream(is);
                                } catch (IOException e) {
                                    throw new DbKingException(e);
                                } finally {
                                    IoUtil.closeInputStream(is);
                                }
                            }
                        }
                    } else {
                        throw new DbKingException("not support data type");
                    }
                    row.addValue(columnName, value);
                }
                rowList.add(row);
            }
            rowList.setTotalRowCount(rowList.size());
            return rowList;
        } catch (SQLException e) {
            throw new DbKingException(e);
        } finally {
            DbUtil.closeResultSet(rs);
            DbUtil.closeStatement(ps);
            if (!(dbSource instanceof ConnectionDbSource)) {
                DbUtil.closeConnection(con);
            }
        }
    }

    public RowList query(String sql, Values values) {
        return query(sql, values, false);
    }

    public RowList query(String sql) {
        return query(sql, null);
    }

    public RowList query(SelectSql selectSql, boolean containsLob,
                         int pageSize, int pageNumber) {
        StringBuilder sb = new StringBuilder();
        Values values = new Values();
        sb.append("select ").append(selectSql.getColumns()).append(" from ")
                .append(selectSql.getTableName());
        WhereClause cc = selectSql.getWhereClause();
        if (cc != null) {
            String clause = cc.getClause();
            if (clause.length() > 0) {
                sb.append(" where ").append(clause);
                values.addValues(cc.getValues());
            }
        }
        ExtraClause ec = selectSql.getExtraClause();
        if (ec != null) {
            sb.append(" ").append(ec.getClause());
            values.addValues(ec.getValues());
        }
        OrderByClause obc = selectSql.getOrderByClause();
        if (obc != null) {
            String clause = obc.getClause();
            if (clause.length() > 0) {
                sb.append(" order by ").append(clause);
            }
        }
        String sql = sb.toString();
        return query(sql, values, containsLob, pageSize, pageNumber);
    }

    public RowList query(SelectSql selectSql, int pageSize, int pageNumber) {
        return query(selectSql, false, pageSize, pageNumber);
    }

    public RowList query(SelectSql selectSql, boolean containsLob) {
        StringBuilder sb = new StringBuilder();
        Values values = new Values();
        sb.append("select ").append(selectSql.getColumns()).append(" from ")
                .append(selectSql.getTableName());
        WhereClause cc = selectSql.getWhereClause();
        if (cc != null) {
            String clause = cc.getClause();
            if (clause.length() > 0) {
                sb.append(" where ").append(clause);
                values.addValues(cc.getValues());
            }
        }
        ExtraClause ec = selectSql.getExtraClause();
        if (ec != null) {
            sb.append(" ").append(ec.getClause());
            values.addValues(ec.getValues());
        }
        OrderByClause obc = selectSql.getOrderByClause();
        if (obc != null) {
            String clause = obc.getClause();
            if (clause.length() > 0) {
                sb.append(" order by ").append(clause);
            }
        }
        String sql = sb.toString();
        return query(sql, values, containsLob);
    }

    public RowList query(SelectSql selectSql) {
        return query(selectSql, false);
    }

    public int execute(String sql, Values values) {
        if (values == null) {
            values = new Values();
        }
        DbUtil.printSql(sql, values);
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = dbSource.getConnection();
            ps = con.prepareStatement(sql);
            DbUtil.setColumnValue(ps, 1, values);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DbKingException(e);
        } finally {
            DbUtil.closeStatement(ps);
            if (!(dbSource instanceof ConnectionDbSource)) {
                DbUtil.closeConnection(con);
            }
        }
    }

    public void execute(String sql) {
        execute(sql, null);
    }

    public int execute(InsertSql insertSql) {
        StringBuilder sb = new StringBuilder();
        Values values = new Values();
        InsertKeyValueClause ikvc = insertSql.getInsertKeyValueClause();
        values.addValues(ikvc.getValues());
        sb.append("insert into ").append(insertSql.getTableName()).append("(")
                .append(ikvc.getKeysClause()).append(") values(")
                .append(ikvc.getValuesClause()).append(")");
        String sql = sb.toString();
        return execute(sql, values);
    }

    public int execute(UpdateSql updateSql) {
        StringBuilder sb = new StringBuilder();
        Values values = new Values();
        UpdateKeyValueClause ukvc = updateSql.getUpdateKeyValueClause();
        values.addValues(ukvc.getValues());
        sb.append("update ").append(updateSql.getTableName()).append(" set ")
                .append(ukvc.getClause());
        WhereClause cc = updateSql.getWhereClause();
        if (cc != null) {
            String clause = cc.getClause();
            if (clause.length() > 0) {
                sb.append(" where ").append(clause);
                values.addValues(cc.getValues());
            }
        }
        String sql = sb.toString();
        return execute(sql, values);
    }

    public int execute(DeleteSql deleteSql) {
        StringBuilder sb = new StringBuilder();
        Values values = new Values();
        sb.append("delete from ").append(deleteSql.getTableName());
        WhereClause cc = deleteSql.getWhereClause();
        if (cc != null) {
            String clause = cc.getClause();
            if (clause.length() > 0) {
                sb.append(" where ").append(clause);
                values.addValues(cc.getValues());
            }
        }
        String sql = sb.toString();
        return execute(sql, values);
    }

    public void getSingleClob(String sql, Values values, Writer writer) {
        if (values == null) {
            values = new Values();
        }
        DbUtil.printSql(sql, values);
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = dbSource.getConnection();
            ps = con.prepareStatement(sql);
            DbUtil.setColumnValue(ps, 1, values);
            rs = ps.executeQuery();
            while (rs.next()) {
                Reader reader = rs.getCharacterStream(1);
                if (reader != null) {
                    try {
                        IoUtil.convertStream(reader, writer);
                    } catch (IOException e) {
                        throw new DbKingException(e);
                    } finally {
                        IoUtil.closeReader(reader);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DbKingException(e);
        } finally {
            DbUtil.closeResultSet(rs);
            DbUtil.closeStatement(ps);
            if (!(dbSource instanceof ConnectionDbSource)) {
                DbUtil.closeConnection(con);
            }
        }
    }

    public void setSingleClob(String sql, Values values, Reader reader, int size) {
        if (values == null) {
            values = new Values();
        }
        DbUtil.printSql(sql, values);
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = dbSource.getConnection();
            ps = con.prepareStatement(sql);
            if (reader == null) {
                ps.setCharacterStream(1, null, 0);
            } else {
                ps.setCharacterStream(1, reader, size);
            }
            DbUtil.setColumnValue(ps, 2, values);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DbKingException(e);
        } finally {
            DbUtil.closeStatement(ps);
            if (!(dbSource instanceof ConnectionDbSource)) {
                DbUtil.closeConnection(con);
            }
        }
    }

    public void getSingleBlob(String sql, Values values, OutputStream os) {
        if (values == null) {
            values = new Values();
        }
        DbUtil.printSql(sql, values);
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = dbSource.getConnection();
            ps = con.prepareStatement(sql);
            DbUtil.setColumnValue(ps, 1, values);
            rs = ps.executeQuery();
            while (rs.next()) {
                InputStream is = rs.getBinaryStream(1);
                if (is != null) {
                    try {
                        IoUtil.convertStream(is, os);
                    } catch (IOException e) {
                        throw new DbKingException(e);
                    } finally {
                        IoUtil.closeInputStream(is);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DbKingException(e);
        } finally {
            DbUtil.closeResultSet(rs);
            DbUtil.closeStatement(ps);
            if (!(dbSource instanceof ConnectionDbSource)) {
                DbUtil.closeConnection(con);
            }
        }
    }

    public void setSingleBlob(String sql, Values values, InputStream is,
                              int size) {
        if (values == null) {
            values = new Values();
        }
        DbUtil.printSql(sql, values);
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = dbSource.getConnection();
            ps = con.prepareStatement(sql);
            if (is == null) {
                ps.setBinaryStream(1, null, 0);
            } else {
                ps.setBinaryStream(1, is, size);
            }
            DbUtil.setColumnValue(ps, 2, values);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DbKingException(e);
        } finally {
            DbUtil.closeStatement(ps);
            if (!(dbSource instanceof ConnectionDbSource)) {
                DbUtil.closeConnection(con);
            }
        }
    }

    private void initSequenceTable() {
        Table table = new Table(SEQ_TABLE_NAME).addColumn(
                new Column(SEQ_NAME_COLUMN_NAME, ColumnType.STRING)
                        .setPrimaryKey(true)).addColumn(
                new Column(SEQ_VALUE_COLUMN_NAME, ColumnType.NUMBER)
                        .setScale(0));
        createTable(table);
    }

    private void initSequenceRow(String sequenceName) {
        execute(new InsertSql().setTableName(SEQ_TABLE_NAME)
                .setInsertKeyValueClause(
                        new InsertKeyValueClause().addStringClause(
                                SEQ_NAME_COLUMN_NAME, sequenceName)
                                .addNumberClause(SEQ_VALUE_COLUMN_NAME,
                                        BigDecimal.ZERO)));
    }

    public long getNextValue(String sequenceName) {
        SelectSql selectSql = new SelectSql()
                .setTableName(SEQ_TABLE_NAME)
                .setColumns(SEQ_VALUE_COLUMN_NAME)
                .setWhereClause(
                        new WhereClause(LogicalOp.AND).addStringClause(
                                SEQ_NAME_COLUMN_NAME, RelationOp.EQUAL,
                                sequenceName));
        RowList rs;
        try {
            rs = query(selectSql);
        } catch (DbKingException e) {
            try {
                initSequenceTable();
            } catch (DbKingException e1) {
            }
            try {
                initSequenceRow(sequenceName);
            } catch (DbKingException e1) {
            }
            rs = query(selectSql);
        }
        if (rs.size() == 0) {
            try {
                initSequenceRow(sequenceName);
            } catch (DbKingException e) {
            }
            rs = query(selectSql);
        }
        Row row = rs.get(0);
        BigDecimal seqValue = row.getNumber(SEQ_VALUE_COLUMN_NAME);
        BigDecimal nextSeqValue = seqValue.add(BigDecimal.ONE);
        int count = 0;
        while (count == 0) {
            count = execute(new UpdateSql()
                    .setTableName(SEQ_TABLE_NAME)
                    .setUpdateKeyValueClause(
                            new UpdateKeyValueClause().addNumberClause(
                                    SEQ_VALUE_COLUMN_NAME, nextSeqValue))
                    .setWhereClause(
                            new WhereClause(LogicalOp.AND).addStringClause(
                                    SEQ_NAME_COLUMN_NAME, RelationOp.EQUAL,
                                    sequenceName).addNumberClause(
                                    SEQ_VALUE_COLUMN_NAME, RelationOp.EQUAL,
                                    seqValue)));
            if (count == 1) {
                break;
            }
            seqValue = nextSeqValue;
            nextSeqValue = seqValue.add(BigDecimal.ONE);
        }
        return nextSeqValue.longValue();
    }

}
