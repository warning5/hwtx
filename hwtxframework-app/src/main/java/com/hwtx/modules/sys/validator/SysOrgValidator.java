package com.hwtx.modules.sys.validator;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import com.thinkgem.jeesite.common.Constants;
import com.thinkgem.jeesite.common.web.BaseController;

public class SysOrgValidator extends Validator {

	@Override
	protected void validate(Controller c) {
		validateString("sysOrg.name", true, 0, 50, "nameMsg", "name 不能为空");
	}

	@Override
	protected void handleError(Controller c) {
		String message = c.getAttrForStr("nameMsg");
		c.renderJson(((BaseController) c).getRenderJson(Constants.RENDER_JSON_ERROR, message));
	}
}
