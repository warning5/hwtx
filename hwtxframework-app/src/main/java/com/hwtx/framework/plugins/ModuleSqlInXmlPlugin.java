package com.hwtx.framework.plugins;

import com.jfinal.core.HwTx;
import com.jfinal.ext.plugin.sqlinxml.SqlKit;
import com.jfinal.plugin.IPlugin;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;

public class ModuleSqlInXmlPlugin implements IPlugin {

	private Logger logger = LoggerFactory.getLogger(getClass());

	public ModuleSqlInXmlPlugin() {
	}

	@Override
	public boolean start() {
        
		for (ModuleIdentifier moduleIdentifier : HwTx.getModuleManager().getModuleSpecs().keySet()) {
			Module module = HwTx.getModuleManager().getLoader().findModule(moduleIdentifier.getName());
			if (module != null) {
				URL url = module.getExportedResource("conf" + File.separator + "sql");
				if (url == null) {
					logger.warn("There is not exist conf/sql in module " + moduleIdentifier.getName());
					continue;
				}
				File[] configFiles = new File(url.getPath()).listFiles();
				ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
				try {
					Thread.currentThread().setContextClassLoader(module.getClassLoader());
					for (int i = 0; i < configFiles.length; i++) {
						SqlKit.parse(configFiles[i]);
					}
				} finally {
					Thread.currentThread().setContextClassLoader(oldClassLoader);
				}
			}
		}
		return true;
	}

	@Override
	public boolean stop() {
		SqlKit.clearSqlMap();
		return true;
	}

}
