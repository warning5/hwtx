package com.hwtx.modules.sys.web;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hwtx.framework.interceptor.ModuleIocInterceptor;
import com.hwtx.modules.sys.dao.ResultType;
import com.hwtx.modules.sys.entity.SysUser;
import com.hwtx.modules.sys.service.SysUserService;
import com.hwtx.modules.sys.service.SystemService;
import com.hwtx.modules.sys.utils.UserUtils;
import com.hwtx.modules.sys.vo.SearchSysUser;
import com.jfinal.aop.Before;
import com.jfinal.config.JFinalConfig;
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
@ControllerBind(controllerKey = "${adminPath}/sys/user")
public class UserController extends BaseController {

	static Map<String, String> sortMapping = Maps.newHashMap();

	static {
		sortMapping.put("1", "userId");
		sortMapping.put("2", "name");
		sortMapping.put("3", "login_ip");
		sortMapping.put("4", "login_date");
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Page1<SysUser> getPageData(int iDisplayStart, int fcount) {
		SearchSysUser searchSysUser = getModel(SearchSysUser.class);
		return userService.findUserByPage(searchSysUser, iDisplayStart, fcount);
	}

	@Resource
	private SysUserService userService;

	@Override
	protected String getSortName(String sort) {
		return sortMapping.get(sort);
	}

	@ActionKey(value = { "list", "/" })
	public void list() {
		Integer cacheNumber = JFinalConfig.getPropertyToInt(Constants.PAGE_CACHENUMBER);
		setAttr("cacheNumber", cacheNumber.intValue());
		render("/modules/sys/userList.jsp");
	}

	@ActionKey(value = { "lookup" })
	public void lookup() {
		setAttr("orgId", getPara("orgId"));
		setAttr("includeUser", getPara("includeUser"));
		render("/modules/sys/userLookup.jsp");
	}

	@ActionKey(value = "showAdd")
	public void showAdd() {
		render("/modules/sys/userForm.jsp");
	}

	@ActionKey(value = "showAssignRole")
	public void showAssignRole() {
		String id = getPara("id");
		setAttr("userId", id);
		Map<String, String> roles = userService.getUserRoles(id);
		setAttr("roles", roles);
		setAttr("roleIds", Joiner.on(",").join(roles.keySet()));
		render("/modules/sys/userAssignRole.jsp");
	}

	@ActionKey(value = "assignRole")
	public void assignRole() {
		String userId = getPara("userId");
		String roles = getPara("roles");
		userService.assignRoles(userId, roles);
		renderJson(getRenderJson(Constants.RENDER_JSON_SUCCESS, "保存成功!"));
	}

	@ActionKey(value = "save")
	public void save() {
		SysUser sysUser = getModel(SysUser.class);
		if (sysUser.getUserId() != null) {
			if (sysUser.getPwd() != null) {
				sysUser.setPwd(SystemService.entryptPassword(sysUser.getPwd()));
			}
			userService.edit(sysUser);
		} else {
			sysUser.setUserId(IdGen.uuid());
			ResultType resultType = userService.save(sysUser);
			if (resultType == ResultType.EXIST) {
				renderJson(getRenderJson(Constants.RENDER_JSON_ERROR, "用户名称已存在"));
				return;
			}
		}
		renderJson(getRenderJson(Constants.RENDER_JSON_SUCCESS, "保存成功"));
	}

	@ActionKey(value = "showEdit")
	public void showEdit() {
		String userId = getPara("id");
		SysUser sysUser = userService.getSysUser(userId);
		setAttr("sysUser", sysUser);
		render("/modules/sys/userForm.jsp");
	}

	@ActionKey(value = "d")
	public void delete() {
		String ids = getPara("ids");
		String message = "删除用户成功";
		String code = Constants.RENDER_JSON_SUCCESS;
		List<String[]> it = Lists.newArrayList();
		if (StringUtils.isNotEmpty(ids)) {
			for (String id : ids.split(",")) {
				if (UserUtils.getUser().getUserId().equals(id)) {
					message = "删除[" + id + "]失败, 不允许删除当前用户";
					code = Constants.RENDER_JSON_ERROR;
				} else if (SysUser.isAdmin(id)) {
					message = "删除" + id + "失败, 不允许删除超级管理员用户";
					code = Constants.RENDER_JSON_ERROR;
				} else {
					it.add(new String[] { id });
				}
			}
		}
		if (it.size() != 0) {
			userService.deleteUser(it.toArray(new String[0][]));
		}
		renderJson(getRenderJson(code, message));
	}
}
