package com.hwtx.framework.plugins;

import com.hwtx.config.HwTxXmlConfig;
import com.hwtx.framework.MConstants;
import com.hwtx.utils.DirWatcher;
import com.hwtx.utils.KeyState;
import com.hwtx.utils.OperationType;
import com.jfinal.core.*;
import com.jfinal.ext.kit.JaxbKit;
import com.jfinal.ext.plugin.sqlinxml.SqlKit;
import com.jfinal.plugin.IPlugin;
import com.thinkgem.jeesite.common.Constants;
import com.thinkgem.jeesite.common.utils.FileUtils;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleManager;
import org.jboss.modules.filter.PathFilters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static java.lang.Thread.currentThread;

public class ModuleManagerPlugin implements IPlugin {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public boolean start() {

        String root = ModuleManagerPlugin.class.getClassLoader().getResource("/").getPath();
        File path = new File(new File(root).getParent(), MConstants._module_root);
        final ModuleManager moduleManager = new ModuleManager(path.getPath(), false,
                PathFilters.acceptAll(), null);

        try {
            moduleManager.start();
        } catch (Exception e1) {
            logger.error("{}", e1);
        }

        JFinal.me().getServletContext().setAttribute(Constants.MODULEMANAGER, moduleManager);

        String _config_path = currentThread().getContextClassLoader().getResource("config" + File.separator + "config.xml")
                .getPath();
        HwTxXmlConfig hwTxConfig = JaxbKit.unmarshal(new File(_config_path), HwTxXmlConfig.class);

        HwTxXmlConfig.FileMonitor fileMonitor = hwTxConfig.getFileMonitor();

        if (fileMonitor != null) {

            boolean monitor = fileMonitor.isEnable();

            if (monitor) {

                for (HwTxXmlConfig.Item item : fileMonitor.getItems()) {

                    final String target = item.getTarget();
                    final String source = item.getSource();
                    final String moduleName = item.getModule();

                    DirWatcher dirWatcher = new DirWatcher(Paths.get(source), true, new DirWatcher.WatchEventHandler() {
                        @Override
                        public void handleEvent(KeyState event) {
                            try {
                                File file = event.path.toFile();
                                String path = file.getPath().substring(source.length());
                                switch (event.opType) {
                                    case Delete:
                                        if (file.getName().endsWith("sql.xml")) {
                                            SqlKit.removeSqlWithPrefix(file);
                                        } else if (!file.getName().endsWith(".class")) {
                                            if (file.isDirectory()) {
                                                FileUtils.deleteDirectory(new File(target, path));
                                            } else {
                                                FileUtils.deleteQuietly(new File(target, path));
                                            }
                                        } else {
                                            moduleManager.reloadModule(moduleName);
                                            if (file.getName().endsWith("spring.xml")) {
                                                reloadApplicationContext(moduleName);
                                            } else {
                                                reloadActionMapping(moduleManager, moduleName, path);
                                                reloadApplicationContext(moduleName);
                                            }
                                        }
                                        break;
                                    default:
                                        if (file.getName().endsWith("sql.xml")) {
                                            SqlKit.reload(file, event.opType == OperationType.Create);
                                        } else if (!file.getName().endsWith(".class")) {
                                            if (file.isDirectory()) {
                                                FileUtils.copyDirectory(file, new File(target, path));
                                            } else {
                                                if (file.exists())
                                                    FileUtils.copyFile(file, new File(target, path));
                                            }
                                        } else {
                                            moduleManager.reloadModule(moduleName);
                                            if (file.getName().endsWith("ioc.xml") || event.opType == OperationType.Create) {
                                                reloadApplicationContext(moduleName);
                                            } else {
                                                HwTx.configHwTxPluginsAndRoute(moduleName);
                                                String fileName = file.getName();
                                                if (fileName.lastIndexOf(".") > 0) {
                                                    fileName = fileName.substring(0, fileName.lastIndexOf("."));
                                                }
                                                refreshIoc(moduleManager, moduleName, path, fileName);
                                            }
                                        }
                                        break;
                                }
                            } catch (IOException e) {
                                logger.error("{}", e);
                            }
                        }
                    });
                    if (item.getExcludeSet() != null) {
                        dirWatcher.setExcludeSet(item.getExcludeSet().getValues());
                    }
                    dirWatcher.startMonitorDir();
                }
            }
        }
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }

    private void reloadApplicationContext(String moduleName) {
        for (IPlugin plugin : Config.getPlugins().getPluginList()) {
            if (plugin instanceof ModuleIocPlugin) {
                ((ModuleIocPlugin) plugin).loadModuleAppliactionContext(moduleName);
                break;
            }
        }
    }

    public void refreshIoc(ModuleManager moduleManager, String moduleName, String path, String fileName) {
        String className = getClassName(path);
        for (IPlugin plugin : Config.getPlugins().getPluginList()) {
            if (plugin instanceof ModuleIocPlugin) {
                ((ModuleIocPlugin) plugin).refreshComponent(moduleName, className, fileName);
                break;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void reloadActionMapping(ModuleManager moduleManager, String moduleName, String path) {

        String className = getClassName(path);
        Module module = moduleManager.getLoader().findModule(moduleName);
        Class<Controller> controller;
        try {
            controller = (Class<Controller>) Class.forName(className, true, module.getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (!controller.getSuperclass().getName().equals(ModuleController.class.getName())) {
            return;
        }
        HwTxActionHandler hwTxActionHandler = HwTx.getManagedBean(HwTxActionHandler.class);
        String controllerKey = HwTx.getControllerKey(moduleName, controller);
        hwTxActionHandler.getActionMapping().removeActionMapping(controllerKey);
        if (logger.isInfoEnabled()) {
            logger.info("routes.remove(" + controllerKey + ", " + controller.getName() + ")");
        }
    }

    private String getClassName(String path) {
        String className = path.replace(File.separator, ".");
        int dotClass = className.lastIndexOf(".");
        className = className.substring(0, dotClass);
        if (className.startsWith(".")) {
            className = className.substring(1);
        }
        return className;
    }
}
