package com.hwtx.modules.sys.web;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.hwtx.framework.interceptor.ModuleIocInterceptor;
import com.hwtx.modules.sys.entity.Dict;
import com.hwtx.modules.sys.service.DictService;
import com.hwtx.modules.sys.vo.SearchDict;
import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page1;
import com.thinkgem.jeesite.common.Constants;
import com.thinkgem.jeesite.common.utils.IdGen;
import com.thinkgem.jeesite.common.web.BaseController;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Before(ModuleIocInterceptor.class)
@ControllerBind(controllerKey = "${adminPath}/sys/dict")
public class DictController extends BaseController {

    static Map<String, String> sortMapping = Maps.newHashMap();

    @Resource
    private DictService dictService;

    static {
        sortMapping.put("2", "label");
        sortMapping.put("3", "value");
        sortMapping.put("4", "type");
    }

    @Override
    protected String getSortName(String sort) {
        return sortMapping.get(sort);
    }

    @ActionKey(value = {"detail"})
    public void get() {
        String id = getPara("id");
        if (StringUtils.isNotEmpty(id)) {
            Dict dict = dictService.get(id);
            render(JSON.toJSONString(dict));
        }
    }

    @ActionKey(value = {"list", "/"})
    public void list() {
        List<Dict> typeList = dictService.findTypeList();
        setAttr("typeList", typeList);
        render("/modules/sys/dictList.jsp");
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Page1<Dict> getPageData(int iDisplayStart, int fcount) {
        SearchDict dict = getModel(SearchDict.class);
        Page1<Dict> page = dictService.findDictByPage(dict, iDisplayStart, fcount);
        return page;
    }

    @ActionKey(value = "form")
    public void form() {
        String id = getPara("id");
        if (StringUtils.isNotEmpty(id)) {
            setAttr("dict", dictService.get(id));
        }
        render("/modules/sys/dictForm.jsp");
    }

    @ActionKey(value = "save")
    public void save() {
        Dict dict = getModel(Dict.class);
        String message;
        if (dict.getId() != null) {
            dictService.update(dict);
            message = "更新字典'" + dict.getLabel() + "'成功";
        } else {
            dict.setId(IdGen.uuid());
            dictService.save(dict);
            message = "保存字典'" + dict.getLabel() + "'成功";
        }
        renderJson(getRenderJson(Constants.RENDER_JSON_SUCCESS, message));
    }

    @ActionKey(value = "delete")
    public void delete() {
        String ids = getPara("ids");
        if (StringUtils.isEmpty(ids)) {
            renderJson(getRenderJson(Constants.RENDER_JSON_ERROR, "请选择删除项"));
        } else {
            dictService.delete(ids.split(","));
            renderJson(getRenderJson(Constants.RENDER_JSON_SUCCESS, "删除字典成功"));
        }
    }
}
