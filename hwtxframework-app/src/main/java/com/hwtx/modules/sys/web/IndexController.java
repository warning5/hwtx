package com.hwtx.modules.sys.web;

import com.jfinal.core.Controller;
import com.jfinal.ext.route.ControllerBind;
import com.thinkgem.jeesite.common.config.Global;

@ControllerBind(controllerKey = "/")
public class IndexController extends Controller {
	
	public void index() {
		redirect(Global.getAdminPath());
	}
}
