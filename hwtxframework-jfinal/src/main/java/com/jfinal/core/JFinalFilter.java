/**
 * Copyright (c) 2011-2013, James Zhan 詹波 (jfinal@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jfinal.core;

import com.jfinal.config.Constants;
import com.jfinal.config.JFinalConfig;
import com.jfinal.handler.Handler;
import com.jfinal.log.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JFinal framework filter
 */
public final class JFinalFilter implements Filter {

    private Handler handler;
    private String encoding;
    private JFinalConfig jfinalConfig;
    private Constants constants;
    private static final JFinal jfinal = JFinal.me();
    private static Logger log;
    private int contextPathLength;

    public void init(FilterConfig filterConfig) throws ServletException {
        createJFinalConfig(filterConfig.getInitParameter("configClass"));
        jfinalConfig.beforeJFinalStart();

        if (jfinal.init(jfinalConfig, filterConfig.getServletContext()) == false)
            throw new RuntimeException("JFinal init error!");

        handler = jfinal.getHandler();
        constants = Config.getConstants();
        encoding = constants.getEncoding();
        jfinalConfig.afterJFinalStart();

        String contextPath = filterConfig.getServletContext().getContextPath();
        contextPathLength = (contextPath == null || "/".equals(contextPath) ? 0 : contextPath.length());
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        request.setCharacterEncoding(encoding);

        String target = request.getRequestURI();
        if (contextPathLength != 0) {
            target = target.substring(contextPathLength);
        }

        int index = target.indexOf(";");
        if (index > 0) {
            target = target.substring(0, index);
        }

        boolean[] isHandled = {false};
        try {
            handler.handle(target, request, response, isHandled);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                String qs = request.getQueryString();
                log.error(qs == null ? target : target + "?" + qs, e);
            }
        }

        if (isHandled[0] == false)
            chain.doFilter(request, response);
    }

    public void destroy() {
        jfinalConfig.beforeJFinalStop();
        jfinal.stopPlugins();
        jfinalConfig.afterJFinalStop();
    }

    private void createJFinalConfig(String configClass) {
        if (configClass == null)
            throw new RuntimeException("Please set configClass parameter of JFinalFilter in web.xml");

        try {
            Object temp = Class.forName(configClass).newInstance();
            if (temp instanceof JFinalConfig)
                jfinalConfig = (JFinalConfig) temp;
            else
                throw new RuntimeException("Can not create instance of class: " + configClass
                        + ". Please check the config in web.xml");
        } catch (InstantiationException e) {
            throw new RuntimeException("Can not create instance of class: " + configClass, e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Can not create instance of class: " + configClass, e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class not found: " + configClass + ". Please config it in web.xml", e);
        }
    }

    static void initLogger() {
        log = Logger.getLogger(JFinalFilter.class);
    }
}
