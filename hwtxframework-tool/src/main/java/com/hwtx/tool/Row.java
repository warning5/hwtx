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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;

import com.hwtx.tool.exception.DbKingException;
import com.hwtx.tool.util.StringUtil;

public class Row {

	private Map<String, Object> valueMap = new HashMap<String, Object>();

	private RowList rowList;

	public Row(RowList rowList) {
		this.rowList = rowList;
	}

	public Map<String, Object> getValueMap() {
		return this.valueMap;
	}

	public void addValue(String columnName, Object value) {
		this.valueMap.put(columnName, value);
	}

	public Object get(int i) {
		List<String> columnNameList = rowList.getColumnNameList();
		String columnName = columnNameList.get(i - 1);
		return valueMap.get(columnName);
	}

	public Object get(String columnName) {
		columnName = columnName.toUpperCase();
		return valueMap.get(columnName);
	}

	public String getString(int i) {
		return (String) this.get(i);
	}

	public String getString(String columnName) {
		return (String) this.get(columnName);
	}

	public BigDecimal getNumber(int i) {
		return (BigDecimal) this.get(i);
	}

	public BigDecimal getNumber(String columnName) {
		return (BigDecimal) this.get(columnName);
	}

	public Timestamp getTimestamp(int i) {
		return (Timestamp) this.get(i);
	}

	public Timestamp getTimestamp(String columnName) {
		return (Timestamp) this.get(columnName);
	}

	public String getClob(int i) {
		return (String) this.get(i);
	}

	public String getClob(String columnName) {
		return (String) this.get(columnName);
	}

	public byte[] getBlob(int i) {
		return (byte[]) this.get(i);
	}

	public byte[] getBlob(String columnName) {
		return (byte[]) this.get(columnName);
	}

	public Object toBean(Class<?> clazz) {
		try {
			Object bean = clazz.newInstance();
			Field[] fields = clazz.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				Column column = (Column) fields[i].getAnnotation(Column.class);
				String fieldName = column.name();
				Class<?> fieldTypeClass = fields[i].getType();
				Object fieldValue;
				if (String.class.equals(fieldTypeClass)) {
					fieldValue = getString(fieldName);
				} else if (BigDecimal.class.equals(fieldTypeClass)) {
					fieldValue = getNumber(fieldName);
				} else if (Timestamp.class.equals(fieldTypeClass)) {
					fieldValue = getTimestamp(fieldName);
				} else {
					continue;
				}
				String setMethodName = "set"
						+ StringUtil.getDefinationName(fieldName);
				Method setMethod = clazz.getMethod(setMethodName,
						fieldTypeClass);
				setMethod.invoke(bean, fieldValue);
			}
			return bean;
		} catch (SecurityException e) {
			throw new DbKingException(e);
		} catch (IllegalArgumentException e) {
			throw new DbKingException(e);
		} catch (InstantiationException e) {
			throw new DbKingException(e);
		} catch (IllegalAccessException e) {
			throw new DbKingException(e);
		} catch (NoSuchMethodException e) {
			throw new DbKingException(e);
		} catch (InvocationTargetException e) {
			throw new DbKingException(e);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String columnName : valueMap.keySet()) {
			sb.append("[").append(columnName).append(" = ");
			Object value = valueMap.get(columnName);
			if (value instanceof NullValue) {
				sb.append("<NULL>");
			} else if (value instanceof String) {
				sb.append(value);
			} else if (value instanceof BigDecimal) {
				sb.append(value);
			} else if (value instanceof Timestamp) {
				sb.append(value);
			} else if (value instanceof char[]) {
				sb.append("<CLOB>");
			} else if (value instanceof byte[]) {
				sb.append("<BLOB>");
			}
			sb.append("]");
		}
		sb.append("\n");
		return sb.toString();
	}

}
