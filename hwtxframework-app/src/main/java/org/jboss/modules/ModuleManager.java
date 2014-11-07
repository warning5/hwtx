package org.jboss.modules;

import static org.jboss.modules.ModuleUtils.getRepoRoots;
import static org.jboss.modules.ModuleUtils.toPathString;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.LogManager;

import lombok.Getter;

import org.jboss.modules.ModuleConfig.Extension;
import org.jboss.modules.filter.PathFilter;
import org.jboss.modules.log.JDKModuleLogger;

import com.hwtx.framework.HwTxModuleLoader;
import com.jfinal.ext.kit.JaxbKit;
import com.jfinal.log.Logger;

public final class ModuleManager {

	static {
		// Force initialization at the earliest possible point
		@SuppressWarnings("unused")
		long start = StartTimeHolder.START_TIME;
	}

	private static final String[] NO_STRINGS = new String[0];
	private Logger logger = Logger.getLogger(getClass());

	@Getter
	HwTxModuleLoader loader;
	// TODO：impl
	private boolean _addindex;
	private ModuleConfig moduleConfig;
	@Getter
	private Map<ClassLoader, List<String>> moduleLoaderPathMapping = new HashMap<>();
	private Map<String, String> modulePathMapping = new HashMap<>();
	@Getter
	private Map<ModuleIdentifier, ModuleSpec> moduleSpecs = new HashMap<>();
	private PathFilter pathFilter;

	public ModuleManager(String _module_path, boolean _addindex, final PathFilter pathFilter, ModuleIdentifier jaxpModuleIdentifier) {
		this._addindex = _addindex;
		this.pathFilter = pathFilter;
		System.setProperty("module.path", _module_path);
		String file = Thread.currentThread().getContextClassLoader().getResource("config" + File.separator + "module-config.xml").getPath();
		moduleConfig = JaxbKit.unmarshal(new File(file), ModuleConfig.class);
	}

	public void depTree(String nameArgument) {
		DependencyTreeViewer.print(new PrintWriter(System.out), ModuleIdentifier.fromString(nameArgument), getRepoRoots(true));
	}

	public String getModulePath(String moduleName) {
		return modulePathMapping.get(moduleName);
	}

	public void start() throws Exception {

		String[] moduleArgs = NO_STRINGS;
		loader = new HwTxModuleLoader();
		for (Extension extension : moduleConfig.getExtensions()) {

			ModuleIdentifier moduleIdentifier = ModuleIdentifier.create(extension.module);

			if (extension.lazy)
				continue;

			final String child = toPathString(moduleIdentifier);

			Module module = null;
			for (File root : ModuleUtils.getModulePathFiles()) {
				final File file = new File(root, child);
				if (pathFilter.accept(child)) {
					final File moduleXml = new File(file, "module.xml");
					if (moduleXml.exists()) {
						final ModuleSpec spec = ModuleXmlParser.parseModuleXml(loader, moduleIdentifier, file, moduleXml,
								AccessController.getContext());
						if (spec != null) {
							moduleSpecs.put(moduleIdentifier, spec);
						}

						try {
							module = loader.loadModule(moduleIdentifier);
						} catch (ModuleNotFoundException e) {
							logger.error(e.getMessage(), e);
							module = null;
							continue;
						}

						ModularURLStreamHandlerFactory.addHandlerModule(module);
						ModularContentHandlerFactory.addHandlerModule(module);
						try {
							module.run(moduleArgs);
						} catch (InvocationTargetException e) {
							throw e;
						}

						if (module != null) {
							loadModule(module);
							modulePathMapping.put(moduleIdentifier.getName(), file.getPath());
						}
					}
				}
			}
		}

		Module.initBootModuleLoader(loader);

		final ClassLoader bootClassLoader = Thread.currentThread().getContextClassLoader();

		final String logManagerName = getServiceName(bootClassLoader, "java.util.logging.LogManager");
		if (logManagerName != null) {
			System.setProperty("java.util.logging.manager", logManagerName);
			if (LogManager.getLogManager().getClass() == LogManager.class) {
				System.err.println("WARNING: Failed to load the specified log manager class " + logManagerName);
			} else {
				Module.setModuleLogger(new JDKModuleLogger());
			}
		}

		final String mbeanServerBuilderName = getServiceName(bootClassLoader, "javax.management.MBeanServerBuilder");
		if (mbeanServerBuilderName != null) {
			System.setProperty("javax.management.builder.initial", mbeanServerBuilderName);
			// Initialize the platform mbean server
			ManagementFactory.getPlatformMBeanServer();
		}

		ModuleLoader.installMBeanServer();
	}

	private void loadModule(Module module) {
		for (ResourceLoader resourceLoader : module.getClassLoaderPrivate().getResourceLoaders()) {

			List<String> path = moduleLoaderPathMapping.get(module.getClassLoader());
			if (path == null) {
				path = new ArrayList<>();
			}
			if (new File(resourceLoader.getResourcePath()).exists()) {
				path.add(resourceLoader.getResourcePath());
			}
			moduleLoaderPathMapping.put(module.getClassLoader(), path);
		}
	}

	public void reloadModule(String moduleName) {
		try {
			logger.info("reload module of " + moduleName);
			Module module = loader.findModule(moduleName);
			loader.unloadModuleLocal(module);
			Module nm = loader.loadModule(module.getIdentifier());
			if (nm != null) {
				loadModule(nm);
			}
		} catch (ModuleLoadException e) {
			logger.error("{}", e);
		}
	}

	private static String getServiceName(ClassLoader classLoader, String className) throws IOException {
		final InputStream stream = classLoader.getResourceAsStream("META-INF/services/" + className);
		if (stream == null) {
			return null;
		}
		try {
			final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			String line;
			while ((line = reader.readLine()) != null) {
				final int i = line.indexOf('#');
				if (i != -1) {
					line = line.substring(0, i);
				}
				line = line.trim();
				if (line.length() == 0)
					continue;
				return line;
			}
			return null;
		} finally {
			StreamUtil.safeClose(stream);
		}
	}
}
