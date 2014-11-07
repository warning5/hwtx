package com.hwtx.modules.sys.web;

import com.hwtx.modules.sys.service.RegionService;
import com.hwtx.framework.interceptor.ModuleIocInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.thinkgem.jeesite.common.web.BaseController;

import javax.annotation.Resource;

@Before(ModuleIocInterceptor.class)
@ControllerBind(controllerKey = "${adminPath}/region")
public class RegionController extends BaseController {

    @Resource
    RegionService regionService;

    @ActionKey(value = {"/"})
    public void list() {
        render("/modules/sys/region.jsp");
    }

    @ActionKey(value = {"province"})
    public void province() {
        renderJson(regionService.getProvinces());
    }

    @ActionKey(value = {"city"})
    public void city() {
        String id = getPara("id");
        renderJson(regionService.getCities(Integer.parseInt(id)));
    }

    @ActionKey(value = {"area"})
    public void area() {
        String id = getPara("id");
        renderJson(regionService.getAreas(Integer.parseInt(id)));
    }
}