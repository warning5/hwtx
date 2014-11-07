package com.hwtx.modules.sys.web;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

import com.google.common.collect.Maps;
import com.hwtx.annotation.RequestMethod;
import com.hwtx.modules.sys.entity.SysUser;
import com.hwtx.modules.sys.utils.UserUtils;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.utils.CacheUtils;
import com.thinkgem.jeesite.common.utils.CookieUtils;
import com.thinkgem.jeesite.common.web.BaseController;

/**
 * 登录Controller
 * 
 */
@ControllerBind(controllerKey = "${adminPath}")
public class LoginController extends BaseController {

	/**
	 * 管理登录
	 */
	@ActionKey(value = "login", method = RequestMethod.GET)
	public void login_get() {
		SysUser user = UserUtils.getUser();
		// 如果已经登录，则跳转到管理首页
		if (user.getUserId() != null) {
			redirect(Global.getAdminPath());
		}
		render("/modules/sys/sysLogin.jsp");
	}

	/**
	 * 登录失败，真正登录的POST请求由Filter完成
	 */
	@ActionKey(value = "login", method = RequestMethod.POST)
	public void login_post() {
		SysUser user = UserUtils.getUser();
		// 如果已经登录，则跳转到管理首页
		if (user.getUserId() != null) {
			redirect(Global.getAdminPath());
		}
		setAttr(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM, getPara("username"));
		setAttr("isValidateCodeLogin", isValidateCodeLogin(getPara("username"), true, false));
		render("/modules/sys/sysLogin.jsp");
	}

	/**
	 * 登录成功，进入管理首页
	 */
	@RequiresUser
	@ActionKey(value = "/")
	public void index() {
		SysUser user = UserUtils.getUser();
		// 未登录，则跳转到登录页
		if (user.getUserId() == null) {
			redirect(Global.getAdminPath() + "/login");
		}
		// 登录成功后，验证码计算器清零
		isValidateCodeLogin(user.getName(), false, true);
		// 登录成功后，获取上次登录的当前站点ID
		UserUtils.putCache("siteId", CookieUtils.getCookie(getRequest(), "siteId"));
		setAttr("userName", user.getName());
		render("/modules/sys/sysIndex.jsp");
	}

	/**
	 * 获取主题方案
	 */
	@ActionKey(value = "/theme/{theme}", self = true)
	public void getThemeInCookie(String theme, HttpServletRequest request, HttpServletResponse response) {
		if (StringUtils.isNotBlank(theme)) {
			CookieUtils.setCookie(response, "theme", theme);
		} else {
			theme = CookieUtils.getCookie(request, "theme");
		}
		redirect(request.getParameter("url"));
	}

	/**
	 * 是否是验证码登录
	 * 
	 * @param useruame
	 *            用户名
	 * @param isFail
	 *            计数加1
	 * @param clean
	 *            计数清零
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean isValidateCodeLogin(String useruame, boolean isFail, boolean clean) {
		Map<String, Integer> loginFailMap = (Map<String, Integer>) CacheUtils.get("loginFailMap");
		if (loginFailMap == null) {
			loginFailMap = Maps.newHashMap();
			CacheUtils.put("loginFailMap", loginFailMap);
		}
		Integer loginFailNum = loginFailMap.get(useruame);
		if (loginFailNum == null) {
			loginFailNum = 0;
		}
		if (isFail) {
			loginFailNum++;
			loginFailMap.put(useruame, loginFailNum);
		}
		if (clean) {
			loginFailMap.remove(useruame);
		}
		return loginFailNum >= 3;
	}

	@ActionKey("download")
	public String download(String filePath, HttpServletResponse response) {
		File file = new File(filePath);
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			inputStream = new FileInputStream(filePath);
			response.reset();
			response.setContentType("application/octet-stream;charset=UTF-8");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
			outputStream = new BufferedOutputStream(response.getOutputStream());
			byte data[] = new byte[1024];
			while (inputStream.read(data, 0, 1024) >= 0) {
				outputStream.write(data);
			}
			outputStream.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (outputStream != null)
					outputStream.close();
				if (inputStream != null)
					inputStream.close();
			} catch (IOException e) {
			}

		}
		return null;
	}
}
