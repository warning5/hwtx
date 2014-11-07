package com.hwtx.modules.sys.dao;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.hwtx.modules.sys.entity.Dict;
import com.hwtx.modules.sys.vo.SearchDict;
import com.hwtxframework.ioc.annotation.Component;
import com.jfinal.ext.plugin.sqlinxml.SqlKit;
import com.jfinal.plugin.activerecord.Page1;
import com.thinkgem.jeesite.common.persistence.AbstractBaseDao;
import com.thinkgem.jeesite.common.persistence.CollectBuilder;

@Component
public class DictDao extends AbstractBaseDao<Dict> {

	public List<Dict> findAllList() {
		return find(Dict.dao, SysSqlConstants.dic_findAllList);
	}

	public List<Dict> findTypeList() {
		return Dict.dao.findByCache(SysSqlConstants.SYS_CACHE, SysSqlConstants.CACHE_ALL_DICT_TYPE,
				SqlKit.sql(SysSqlConstants.dic_findAllType));
	}

	public Page1<Dict> findDictByPage(SearchDict dict, int offset, int number) {

		if (dict.isNull()) {
			return Dict.dao.paginateOverOffset(offset, number, SqlKit.sqlSelect(SysSqlConstants.dic_findListByPage).getExpress(),
					SqlKit.sqlExceptSelect(SysSqlConstants.dic_findListByPage).getExpress(), offset);
		}

		String sqlExceptSelect = SqlKit.sqlExceptSelect(SysSqlConstants.dic_findListByPage).getExpress();
		CollectBuilder collectBuilder = new CollectBuilder();
		if (dict.getLabel() != null) {
			collectBuilder.like("label", "%" + dict.getLabel() + "%");
		}

		if (!StringUtils.isEmpty(dict.getTypes())) {
			collectBuilder.in("type", Arrays.asList(dict.getTypes().split(",")));
		}

		if (!collectBuilder.isEmpty()) {
			sqlExceptSelect += CollectBuilder.whereClause() + collectBuilder.build(false);
		}

		return Dict.dao.paginateOverOffset(offset, number, SqlKit.sqlSelect(SysSqlConstants.dic_findListByPage).getExpress(),
				sqlExceptSelect, offset);
	}
}
