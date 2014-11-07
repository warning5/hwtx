package com.hwtx.modules.sys.security;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import lombok.Getter;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.ByteSource;

import com.hwtx.modules.sys.entity.SysUser;
import com.hwtx.modules.sys.service.SystemService;
import com.hwtx.modules.sys.web.LoginController;
import com.hwtxframework.ioc.annotation.Component;
import com.thinkgem.jeesite.common.servlet.ValidateCodeController;
import com.thinkgem.jeesite.common.utils.Encodes;
import com.thinkgem.jeesite.common.utils.IocContextHolder;

@Component
public class SystemAuthorizingRealm extends AuthorizingRealm {

	private SystemService systemService;

	/**
	 * 认证回调函数, 登录时调用
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
		UsernamePasswordToken token = (UsernamePasswordToken) authcToken;

		if (LoginController.isValidateCodeLogin(token.getUsername(), false, false)) {
			// 判断验证码
			Session session = SecurityUtils.getSubject().getSession();
			String code = (String) session.getAttribute(ValidateCodeController.VALIDATE_CODE);
			if (token.getCaptcha() == null || !token.getCaptcha().toUpperCase().equals(code)) {
				throw new CaptchaException("验证码错误.");
			}
		}

		SysUser user = getSystemService().getUserByLoginName(token.getUsername());
		if (user != null) {
			byte[] salt = Encodes.decodeHex(user.getPwd().substring(0, 16));
			return new SimpleAuthenticationInfo(new Principal(user), user.getPwd().substring(16), ByteSource.Util.bytes(salt),
					getName());
		} else {
			return null;
		}
	}

	/**
	 * 设定密码校验的Hash算法与迭代次数
	 */
	@PostConstruct
	public void initCredentialsMatcher() {
		HashedCredentialsMatcher matcher = new HashedCredentialsMatcher(SystemService.HASH_ALGORITHM);
		matcher.setHashIterations(SystemService.HASH_INTERATIONS);
		setCredentialsMatcher(matcher);
	}

	/**
	 * 清空用户关联权限认证，待下次使用时重新加载
	 */
	public void clearCachedAuthorizationInfo(String principal) {
		SimplePrincipalCollection principals = new SimplePrincipalCollection(principal, getName());
		clearCachedAuthorizationInfo(principals);
	}

	/**
	 * 清空所有关联认证
	 */
	public void clearAllCachedAuthorizationInfo() {
		Cache<Object, AuthorizationInfo> cache = getAuthorizationCache();
		if (cache != null) {
			for (Object key : cache.keys()) {
				cache.remove(key);
			}
		}
	}

	/**
	 * 获取系统业务对象
	 */
	public SystemService getSystemService() {
		if (systemService == null) {
			systemService = IocContextHolder.getComponent(SystemService.class.getSimpleName());
		}
		return systemService;
	}

	/**
	 * 授权用户信息
	 */
	public static class Principal implements Serializable {

		private static final long serialVersionUID = 1L;
		@Getter
		private SysUser sysUser;
		private Map<String, Object> cacheMap;

		public Principal(SysUser user) {
			this.sysUser = user;
		}

		public Map<String, Object> getCacheMap() {
			if (cacheMap == null) {
				cacheMap = new HashMap<String, Object>();
			}
			return cacheMap;
		}

	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		// TODO Auto-generated method stub
		return null;
	}
}
