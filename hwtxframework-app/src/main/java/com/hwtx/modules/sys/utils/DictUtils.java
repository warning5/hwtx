/**
 * Copyright &copy; 2012-2013 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.hwtx.modules.sys.utils;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hwtx.modules.sys.dao.DictDao;
import com.hwtx.modules.sys.dao.SysSqlConstants;
import com.hwtx.modules.sys.entity.Dict;
import com.thinkgem.jeesite.common.utils.CacheUtils;
import com.thinkgem.jeesite.common.utils.IocContextHolder;

public class DictUtils {

	public static String getDictLabel(String value, String type, String defaultValue) {
		if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(value)) {
			for (Dict dict : getDictList(type)) {
				if (type.equals(dict.getType()) && value.equals(dict.getValue())) {
					return dict.getLabel();
				}
			}
		}
		return defaultValue;
	}

	public static String getDictValue(String label, String type, String defaultLabel) {
		if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(label)) {
			for (Dict dict : getDictList(type)) {
				if (type.equals(dict.getType()) && label.equals(dict.getLabel())) {
					return dict.getValue();
				}
			}
		}
		return defaultLabel;
	}

	public static List<Dict> getDictList(String type) {
		@SuppressWarnings("unchecked")
		Map<String, List<Dict>> dictMap = (Map<String, List<Dict>>) CacheUtils.get(SysSqlConstants.CACHE_ALL_DICT);
		if (dictMap == null) {
			dictMap = Maps.newHashMap();
			DictDao dictDao = IocContextHolder.getComponent(DictDao.class.getSimpleName());
			for (Dict dict : dictDao.findAllList()) {
				List<Dict> dictList = dictMap.get(dict.getType());
				if (dictList != null) {
					dictList.add(dict);
				} else {
					dictMap.put(dict.getType(), Lists.newArrayList(dict));
				}
			}
			CacheUtils.put(SysSqlConstants.CACHE_ALL_DICT, dictMap);
		}
		List<Dict> dictList = dictMap.get(type);
		if (dictList == null) {
			dictList = Lists.newArrayList();
		}
		return dictList;
	}

}
