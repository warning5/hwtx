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

import com.hwtx.utils.HandlerUtils;
import com.hwtxframework.ioc.annotation.Component;
import com.jfinal.config.Constants;
import com.jfinal.config.JFinalConfig;
import com.jfinal.handler.Handler;
import com.jfinal.log.Logger;
import com.jfinal.render.Render;
import com.jfinal.render.RenderException;
import com.jfinal.render.RenderFactory;
import lombok.Getter;
import lombok.Setter;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * ActionHandler
 */
@Component
public final class HwTxActionHandler extends Handler {

    @Setter
    private boolean devMode;
    @Setter
    @Getter
    private ActionMapping actionMapping;
    private static final RenderFactory renderFactory = RenderFactory.me();
    private static final Logger log = Logger.getLogger(HwTxActionHandler.class);
    private String _module_name_start_with = null;
    private Map<Action, Controller> controllerMapping = new HashMap<>();
    @Setter
    private ModuleManager moduleManager;

    public HwTxActionHandler(ActionMapping actionMapping, Constants constants) {
        this.actionMapping = actionMapping;
        this.devMode = constants.getDevMode();
        this.moduleManager = HwTx.getModuleManager();
        _module_name_start_with = JFinalConfig.getProperty("default.module.name", "_m_");
    }

    public HwTxActionHandler() {
    }

    public void setModulePrefix(String profix) {
        this._module_name_start_with = profix;
    }

    /**
     * handle 1: Action action = actionMapping.getAction(target) 2: new
     * ActionIindexnvocation(...).invoke() 3: render(...)
     */
    public final void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {

        String moduleName = null;

        int _module_start = target.indexOf(_module_name_start_with);
        int end = target.indexOf("/", _module_start);

        if (_module_start >= 0 && end > 0) {
            moduleName = target.substring(_module_start + _module_name_start_with.length(), end);
        }

        if (moduleName == null) {

            if (target.indexOf(".") != -1) {
                return;
            }

            if (log.isInfoEnabled()) {
                log.info("handle " + target + " with local action.");
            }
            handlelocal(target, null, request, response, isHandled);
            return;
        }

        String resource = target.substring(end);

        long startInvoke = 0;
        if (log.isDebugEnabled()) {
            startInvoke = System.currentTimeMillis();
        }

        Module module = moduleManager.getLoader().findModule(moduleName);

        if (module == null) {
            log.error("can't find any module [" + moduleName + "]");
            renderFactory.getErrorRender(500).setContext(request, response).render();
            return;
        }

        if (resource.indexOf(".") != -1) {

            URL url = module.getExportedResource(resource);

            if (url == null) {
                log.error("[" + resource + "] dose not exist.");
                renderFactory.getErrorRender(500).setContext(request, response).render();
                return;
            }
            renderFactory.getFileRender(new File(url.getPath())).setContext(request, response).render();
            isHandled[0] = true;
            return;
        }
        handleModule(resource, module, moduleName, request, response, isHandled);

        if (log.isDebugEnabled()) {
            log.debug("handle module [" + moduleName + "] request spend " + (System.currentTimeMillis() - startInvoke) + "ms");
        }
    }

    private void handleModule(String target, Module module, String moduleName, HttpServletRequest request,
                              HttpServletResponse response, boolean[] isHandled) {
        if (module != null) {
            target = HandlerUtils.getModulePath(target);
        }
        handlelocal(target, moduleName, request, response, isHandled);
    }

    private void handlelocal(String target, String moduleName, HttpServletRequest request, HttpServletResponse response,
                             boolean[] isHandled) {

        isHandled[0] = true;
        String[] urlPara = {null};
        Action action = actionMapping.getAction(moduleName != null ? moduleName + "_" + target : target, urlPara);
        if (action == null) {
            String method = request.getMethod();
            action = actionMapping.getAction(moduleName != null ? moduleName + "_" + target + "_" + method.toLowerCase() : target
                    + "_" + method.toLowerCase(), urlPara);
        }
        if (action == null) {
            if (log.isWarnEnabled()) {
                String qs = request.getQueryString();
                log.warn("404 Action Not Found: " + (qs == null ? target : target + "?" + qs));
            }
            renderFactory.getErrorRender(404).setContext(request, response).render();
            return;
        }

        Controller controller = null;

        try {
            controller = controllerMapping.get(action);
            if (controller == null) {
                controller = action.getControllerClass().newInstance();
                controllerMapping.put(action, controller);
            }
            controller.init(request, response, urlPara[0]);

            if (controller instanceof ModuleController) {
                ((ModuleController) controller).setModuleName(moduleName);
            }

            if (devMode) {
                boolean isMultipartRequest = ActionReporter.reportCommonRequest(controller, action);
                new ActionInvocation(action, controller).invoke();
                if (isMultipartRequest)
                    ActionReporter.reportMultipartRequest(controller, action);
            } else {
                new ActionInvocation(action, controller).invoke();
            }

            Render render = controller.getRender();
            if (render instanceof ActionRender) {
                String actionUrl = ((ActionRender) render).getActionUrl();
                if (target.equals(actionUrl))
                    throw new RuntimeException("The forward action url is the same as before.");
                else
                    handle(actionUrl, request, response, isHandled);
                return;
            }

            String view_path = action.getViewPath();
            String base_view = action.getBaseViewPath();
            if (moduleName != null) {
                view_path = getResourcePath(moduleName);
                base_view = null;
            }

            if (render == null)
                render = renderFactory.getDefaultRender(view_path);

            render.setContext(request, response, view_path, base_view).render();
        } catch (RenderException e) {
            if (log.isErrorEnabled()) {
                String qs = request.getQueryString();
                log.error(qs == null ? target : target + "?" + qs, e);
            }
        } catch (ActionException e) {
            int errorCode = e.getErrorCode();
            if (errorCode == 404 && log.isWarnEnabled()) {
                String qs = request.getQueryString();
                log.warn("404 Not Found: " + (qs == null ? target : target + "?" + qs));
            } else if (errorCode == 401 && log.isWarnEnabled()) {
                String qs = request.getQueryString();
                log.warn("401 Unauthorized: " + (qs == null ? target : target + "?" + qs));
            } else if (errorCode == 403 && log.isWarnEnabled()) {
                String qs = request.getQueryString();
                log.warn("403 Forbidden: " + (qs == null ? target : target + "?" + qs));
            } else if (log.isErrorEnabled()) {
                String qs = request.getQueryString();
                log.error(qs == null ? target : target + "?" + qs, e);
            }
            e.getErrorRender().setContext(request, response).render();
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                String qs = request.getQueryString();
                log.error(qs == null ? target : target + "?" + qs, e);
            }
            if (controller != null) {
                controller.getRender().setContext(request, response).render();
            }
            renderFactory.getErrorRender(500).setContext(request, response).render();
        }
    }

    private String getResourcePath(String moduleName) {
        return HandlerUtils.getModulePath(moduleManager.getModulePath(moduleName)) + "/resources/";
    }
}