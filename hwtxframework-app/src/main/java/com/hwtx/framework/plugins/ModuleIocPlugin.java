package com.hwtx.framework.plugins;

import com.hwtx.framework.interceptor.ModuleIocInterceptor;
import com.hwtxframework.ioc.ApplicationContext;
import com.hwtxframework.ioc.ApplicationContextFactory;
import com.jfinal.core.HwTx;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.IPlugin;
import com.thinkgem.jeesite.common.Constants;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;

public class ModuleIocPlugin implements IPlugin {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private String[] configurations;
    private ApplicationContext ctx;
    private volatile boolean started = false;

    public ModuleIocPlugin(String... configurations) {
        this.configurations = configurations;
    }

    public ModuleIocPlugin(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    public boolean start() {

        if (started) {
            logger.info("spring plugin has started.");
            return started;
        }
        if (ctx != null)
            ModuleIocInterceptor.root = ctx;
        else if (configurations != null)
            ModuleIocInterceptor.root = ApplicationContextFactory.createApplicationContext(configurations);
        else
            ModuleIocInterceptor.root = ApplicationContextFactory.createApplicationContext(PathKit.getWebRootPath() + "/WEB-INF/applicationContext.xml");

        JFinal.me().getServletContext().setAttribute(Constants.ENVIRONMENT_ATTRIBUTE_KEY,
                ModuleIocInterceptor.root);

        for (ModuleIdentifier moduleIdentifier : HwTx.getModuleManager().getModuleSpecs().keySet()) {
            Module module = HwTx.getModuleManager().getLoader().findModule(moduleIdentifier.getName());
            loadIt(module, moduleIdentifier.getName());
        }
        started = true;
        return true;
    }

    public void refreshComponent(String moduleName, String className, String fileName) {
        ApplicationContext applicationContext = ModuleIocInterceptor.ctxs.get(moduleName);
        applicationContext.refreshComponent(className, fileName);
    }

    public void loadModuleAppliactionContext(String moduleName) {
        Module module = HwTx.getModuleManager().getLoader().findModule(moduleName);
        loadIt(module, moduleName);
    }

    private void loadIt(Module module, String moduleName) {
        if (module != null) {
            URL url = module.getExportedResource("conf" + File.separator + "spring");
            if (url == null) {
                logger.info("There is not exist conf/spring in module " + moduleName);
                return;
            }
            File[] configFiles = new File(url.getPath()).listFiles();
            String[] configs = new String[configFiles.length];
            for (int i = 0; i < configFiles.length; i++) {
                configs[i] = configFiles[i].getAbsolutePath();
            }
            ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(module.getClassLoader());
                ApplicationContext applicationContext = ApplicationContextFactory.createApplicationContext(configs);
                ModuleIocInterceptor.ctxs.put(moduleName, applicationContext);
            } finally {
                Thread.currentThread().setContextClassLoader(oldClassLoader);
            }
        } else {
            throw new RuntimeException("reload module application context failure, can't find module of " + moduleName);
        }
    }

    public boolean stop() {
        started = false;
        return true;
    }
}
