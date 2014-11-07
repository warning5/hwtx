package com.hwtx.modules.sys.security;

import com.hwtx.modules.sys.entity.SysUser;
import com.hwtx.modules.sys.security.SystemAuthorizingRealm.Principal;
import com.hwtx.modules.sys.service.SysUserService;
import com.hwtx.modules.sys.utils.UserUtils;
import com.hwtxframework.ioc.annotation.Component;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

@Component("formAuthenticationFilter")
public class HwTxFormAuthenticationFilter extends FormAuthenticationFilter {

    public static final String DEFAULT_CAPTCHA_PARAM = "validateCode";
    private Logger logger = LoggerFactory.getLogger(getClass());

    private String captchaParam = DEFAULT_CAPTCHA_PARAM;

    @Resource
    private SysUserService userService;

    public String getCaptchaParam() {
        return captchaParam;
    }

    protected String getCaptcha(ServletRequest request) {
        return WebUtils.getCleanParam(request, getCaptchaParam());
    }

    @Override
    protected boolean onLoginSuccess(AuthenticationToken token,
                                     Subject subject, ServletRequest request, ServletResponse response)
            throws Exception {

        Principal principal = (Principal) subject.getPrincipals()
                .getPrimaryPrincipal();
        SysUser sysUser = principal.getSysUser();
        try {
            userService.doAuthorization(sysUser);
            userService.updateUserLoginInfo(sysUser.getUserId());
            UserUtils.putCache(UserUtils.CACHE_USER, sysUser);
            if (logger.isInfoEnabled()) {
                logger.info("user " + sysUser + " login success.");
            }
        } catch (Exception e) {
            subject.logout();
        }
        return super.onLoginSuccess(token, subject, request, response);
    }

    protected AuthenticationToken createToken(ServletRequest request,
                                              ServletResponse response) {
        String username = getUsername(request);
        String password = getPassword(request);
        if (password == null) {
            password = "";
        }
        boolean rememberMe = isRememberMe(request);
        String host = getHost(request);
        String captcha = getCaptcha(request);
        return new UsernamePasswordToken(username, password.toCharArray(),
                rememberMe, host, captcha);
    }

}