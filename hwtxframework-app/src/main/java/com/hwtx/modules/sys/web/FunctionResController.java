package com.hwtx.modules.sys.web;

import com.hwtx.framework.interceptor.ModuleIocInterceptor;
import com.hwtx.modules.sys.entity.SysFunctionPermission;
import com.hwtx.modules.sys.service.FunctionResService;
import com.hwtx.modules.sys.service.ServiceResponse;
import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.thinkgem.jeesite.common.Constants;
import com.thinkgem.jeesite.common.web.BaseController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@Before(ModuleIocInterceptor.class)
@ControllerBind(controllerKey = "${adminPath}/sys/res")
public class FunctionResController extends BaseController {

    @Resource
    private FunctionResService functionResService;

    @ActionKey(value = {"list", "/"})
    public void list() {
        render("/modules/sys/res/resList.jsp");
    }

    @ActionKey(value = {"tree"})
    public void tree() {
        renderJson(functionResService.getResourceFunctionsAsJson());
    }

    @ActionKey(value = {"showAdd"})
    public void showAdd() {
        setAttr("pText", getPara("pText"));
        setAttr("pId", getPara("pId"));
        render("/modules/sys/res/form.jsp");
    }

    @ActionKey(value = "roleAssignRes")
    public void assign() {
        render("/modules/sys/res/roleAssignRes.jsp");
    }

    @ActionKey(value = "move")
    public void move() {
        String _old_parent = getPara("_old_parent");
        String _new_parent = getPara("_new_parent");
        String _moved_id = getPara("_moved_id");
        ServiceResponse serviceResponse = functionResService.move(_moved_id, _old_parent, _new_parent);
        renderJson(serviceResponse.json());
    }

    @ActionKey(value = "delete")
    public void delete() {
        try {
            functionResService.delete(getPara("id"));
            renderJson(getRenderJson(Constants.RENDER_JSON_SUCCESS, "删除成功"));
        } catch (Exception e) {
            renderJson(getErrorRenderJson(e.toString(),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
        }
    }

    @ActionKey(value = "save")
    public void save() {
        SysFunctionPermission res = getModel(SysFunctionPermission.class);
        res.setMenu("0");
        functionResService.save(res);
        renderText("保存资源'" + res.getName() + "'成功");
    }

    @ActionKey(value = {"showEdit"})
    public void shwoEdit() {
        String id = getPara("id");
        SysFunctionPermission sysFunctionPermission = functionResService
                .getResource(id);
        setAttr("pText", getPara("pText"));
        setAttr("pId", sysFunctionPermission.getPid());
        setAttr("res", sysFunctionPermission);
        render("/modules/sys/res/form.jsp");
    }
}
