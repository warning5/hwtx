package com.hwtx.modules.sys.security;

import com.hwtx.modules.sys.utils.UserUtils;
import com.hwtx.modules.sys.utils.Utils;
import com.hwtxframework.ioc.annotation.Component;
import com.jfinal.plugin.ehcache.CacheKit;
import com.thinkgem.jeesite.common.Constants;
import com.thinkgem.jeesite.common.config.Global;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.UserFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * Filter that allows access to resources if the accessor is a known user, which
 * is defined as having a known principal. This means that any user who is
 * authenticated or remembered via a 'remember me' feature will be allowed
 * access from this filter.
 * <p/>
 * If the accessor is not a known user, then they will be redirected to the
 * {@link #setLoginUrl(String) loginUrl}
 * </p>
 *
 * @since 0.9
 */
@Component
public class ResourceAuthFilter extends UserFilter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Returns <code>true</code> if the request is a
     * {@link #isLoginRequest(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
     * loginRequest} or if the current
     * {@link #getSubject(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
     * subject} is not <code>null</code>, <code>false</code> otherwise.
     *
     * @return <code>true</code> if the request is a
     * {@link #isLoginRequest(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
     * loginRequest} or if the current
     * {@link #getSubject(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
     * subject} is not <code>null</code>, <code>false</code> otherwise.
     */
    protected boolean isAccessAllowed(ServletRequest request,
                                      ServletResponse response, Object mappedValue) {

        if (isLoginRequest(request, response)) {
            return true;
        } else if (isLoginRedirect(request)) {
            Subject subject = getSubject(request, response);
            boolean result = subject.getPrincipal() != null && subject.isAuthenticated();
            if (result) {
                try {
                    redirectToIndex(request, response);
                } catch (IOException e) {
                    logger.error("{}", e);
                }
                return false;
            }
            return result;
        } else if (UserUtils.getUser() != null && UserUtils.getUser().isAdmin()) {
            return true;
        } else {
            if (!getSubject(request, response).isAuthenticated()) {
                return false;
            }
            String url = Utils.getUrl((HttpServletRequest) request);

            Map<String, Integer> _no_auth = CacheKit.get(Constants.SYSCACHE,
                    Constants.CACHE_ALL_RES_NO_AUTH_MAP);

            if (_no_auth.containsKey(url)) {
                return true;
            }

            @SuppressWarnings("unchecked")
            Map<String, String> mapping = (Map<String, String>) UserUtils
                    .getCache(Constants.CACHE_USER_URL_MAPPING);

            if (mapping == null) {
                return super.isAccessAllowed(request, response, mappedValue);
            }

            if (!mapping.containsKey(url)) {
                logger.error("user" + UserUtils.getCache(UserUtils.CACHE_USER)
                        + " can't auth " + url);
                return false;
            }
        }
        return true;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {

        if (!getSubject(request, response).isAuthenticated()) {
            saveRequestAndRedirectToLogin(request, response);
        } else {
            PrintWriter writer = response.getWriter();
            writer.write("{\"code\":\"error\",\"message\":\"noauth\"}");
            writer.flush();
        }
        return false;
    }

    private boolean isLoginRedirect(ServletRequest request) {
        return pathsMatch(Global.getAdminPath(), request);
    }

    private void redirectToIndex(ServletRequest request, ServletResponse response) throws IOException {
        WebUtils.issueRedirect(request, response, Global.getAdminPath() + "/");
    }
}
