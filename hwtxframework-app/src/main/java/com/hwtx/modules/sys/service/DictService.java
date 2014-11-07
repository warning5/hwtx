package com.hwtx.modules.sys.service;

import com.hwtx.modules.sys.dao.DictDao;
import com.hwtx.modules.sys.dao.SysSqlConstants;
import com.hwtx.modules.sys.entity.Dict;
import com.hwtx.modules.sys.vo.SearchDict;
import com.hwtxframework.ioc.annotation.Component;
import com.jfinal.plugin.activerecord.Page1;
import com.thinkgem.jeesite.common.utils.CacheUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
public class DictService {

    @Resource
    private DictDao dictDao;

    public Dict get(String id) {
        return (Dict) dictDao.findById(Dict.dao, id);
    }

    public List<Dict> findTypeList() {
        return dictDao.findTypeList();
    }

    public void save(Dict dict) {
        dict.prePersist();
        dictDao.save(dict);
        CacheUtils.remove(SysSqlConstants.CACHE_ALL_DICT);
        CacheUtils.remove(SysSqlConstants.CACHE_ALL_DICT_TYPE);
    }

    public void update(Dict dict) {
        dict.preUpdate();
        dictDao.update(dict);
        CacheUtils.remove(SysSqlConstants.CACHE_ALL_DICT);
        CacheUtils.remove(SysSqlConstants.CACHE_ALL_DICT_TYPE);
    }

    public void delete(String[] ids) {

        Object[][] param = new Object[ids.length][1];
        int i = 0;
        for (String id : ids) {
            param[i][0] = id;
            i++;
        }
        dictDao.delete(SysSqlConstants.dic_delete, param);
        CacheUtils.remove(SysSqlConstants.CACHE_ALL_DICT);
        CacheUtils.remove(SysSqlConstants.CACHE_ALL_DICT_TYPE);
    }

    public Page1<Dict> findDictByPage(SearchDict dict, int iDisplayStart, int fcount) {
        return dictDao.findDictByPage(dict, iDisplayStart, fcount);
    }
}
