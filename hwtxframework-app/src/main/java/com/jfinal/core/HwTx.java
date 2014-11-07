package com.jfinal.core;

import com.google.common.base.Preconditions;
import com.hwtxframework.ioc.ApplicationContext;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.thinkgem.jeesite.common.Constants;
import com.thinkgem.jeesite.common.utils.IocContextHolder;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleClassLoader;
import org.jboss.modules.ModuleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HwTx {

    private static Logger logger = LoggerFactory.getLogger(HwTx.class);

    public static ModuleManager getModuleManager() {
        return (ModuleManager) JFinal.me().getServletContext().getAttribute(Constants.MODULEMANAGER);
    }

    public static ApplicationContext getApplicationContext() {
        return (ApplicationContext) JFinal.me().getServletContext()
                .getAttribute(com.thinkgem.jeesite.common.Constants.ENVIRONMENT_ATTRIBUTE_KEY);
    }

    public static HwTxClassSearcher getHwTxClassSearcherWithRootClassMapping() {
        Map<ClassLoader, List<String>> mapping = new HashMap<ClassLoader, List<String>>();
        mapping.putAll(HwTx.getRootClassMapping());
        mapping.putAll(HwTx.getModuleManager().getModuleLoaderPathMapping());
        return new HwTxClassSearcher(mapping);
    }

    public static HwTxClassSearcher getHwTxClassSearcherWithModuleClassMapping(ModuleClassLoader moduleClassLoader) {
        Map<ClassLoader, List<String>> mapping = new HashMap<ClassLoader, List<String>>();
        List<String> paths = HwTx.getModuleManager().getModuleLoaderPathMapping().get(moduleClassLoader);
        mapping.put(moduleClassLoader, paths);
        return new HwTxClassSearcher(mapping);
    }

    public static <T> T getManagedBean(Class<T> clazz) {
        T result = IocContextHolder.getComponent(StrKit.firstCharToLowerCase(clazz.getSimpleName()));
        if (result == null) {
            throw new RuntimeException("can't find component by type " + clazz + ", please use by name to get.");
        }
        return result;
    }

    public static <T> T getComponent(String name) {
        return IocContextHolder.getComponent(name);
    }

    public static Map<ClassLoader, List<String>> getRootClassMapping() {

        Map<ClassLoader, List<String>> classMappping = new HashMap<ClassLoader, List<String>>();
        List<String> path = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        path.add(Thread.currentThread().getContextClassLoader().getResource("").getPath());
        classMappping.put(classLoader, path);
        return classMappping;
    }

    public static Map<ClassLoader, List<String>> getRootLibMapping() {

        String libDir = PathKit.getWebRootPath() + File.separator + "WEB-INF" + File.separator + "lib";
        File dir = new File(libDir);
        List<String> path = new ArrayList<>();
        for (File file : dir.listFiles()) {
            path.add(file.getPath());
        }
        Map<ClassLoader, List<String>> libMappping = new HashMap<ClassLoader, List<String>>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        libMappping.put(classLoader, path);
        return libMappping;
    }

    public static void configHwTxPluginsAndRoute(String moduleName) {
        HwTxActionHandler hwTxActionHandler = HwTx.getManagedBean(HwTxActionHandler.class);
        ActionMapping actionMapping = hwTxActionHandler.getActionMapping();
        Module module = HwTx.getModuleManager().getLoader().findModule(moduleName);
        List<Class<? extends Controller>> controllerClasses = getHwTxClassSearcherWithModuleClassMapping(
                module.getClassLoader()).findInClasspathAndJars(Controller.class);
        for (Class<? extends Controller> controller : controllerClasses) {
            String controllerKey = getControllerKey(moduleName, controller);
            actionMapping.reBuildActionMapping(controllerKey, controller);
            if (logger.isInfoEnabled()) {
                logger.info("routes.add(" + controllerKey + ", " + controller.getName() + ")");
            }
        }
    }

    public static String suffix = "Controller";

    public static String getControllerKey(String moduleName, Class<? extends Controller> controller) {
        ControllerBind controllerBind = (ControllerBind) controller.getAnnotation(ControllerBind.class);
        String controllerKey = null;
        if (controllerBind == null) {
            controllerKey = moduleName + "_" + HwTx.controllerKey(controller, suffix);
        } else {
            String cKey = controllerBind.controllerKey();
            if (PathKit.isVariable(cKey)) {
                cKey = PathKit.getVariableValue(cKey);
            }
            controllerKey = moduleName + "_" + cKey;
        }
        if (!controllerKey.startsWith("/"))
            controllerKey = "/" + controllerKey;

        return controllerKey;
    }

    public static String controllerKey(Class<? extends Controller> clazz, String suffix) {
        Preconditions.checkArgument(clazz.getSimpleName().endsWith(suffix),
                " does not has a @ControllerBind annotation and it's name is not end with " + suffix);
        String controllerKey = "/" + StrKit.firstCharToLowerCase(clazz.getSimpleName());
        controllerKey = controllerKey.substring(0, controllerKey.indexOf(suffix));
        return controllerKey;
    }
}
