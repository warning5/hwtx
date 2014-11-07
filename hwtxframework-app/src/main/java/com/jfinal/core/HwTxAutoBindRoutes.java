package com.jfinal.core;

import com.google.common.collect.Lists;
import com.hwtxframework.ioc.annotation.Component;
import com.jfinal.config.Routes;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Logger;
import org.jboss.modules.ModuleClassLoader;

import java.util.List;

@Component
public class HwTxAutoBindRoutes extends Routes {

	private boolean autoScan = true;
	private HwTxClassSearcher classSearcher;

	private List<Class<? extends Controller>> excludeClasses = Lists.newArrayList();

	private boolean includeAllJarsInLib;

	private List<String> includeJars = Lists.newArrayList();

	protected final Logger logger = Logger.getLogger(getClass());

	public void init() {
		classSearcher = com.jfinal.core.HwTx.getHwTxClassSearcherWithRootClassMapping();
	}

	public HwTxAutoBindRoutes autoScan(boolean autoScan) {
		this.autoScan = autoScan;
		return this;
	}

	public HwTxAutoBindRoutes addExcludeClasses(@SuppressWarnings("unchecked") Class<? extends Controller>... clazzes) {
		if (clazzes != null) {
			for (Class<? extends Controller> clazz : clazzes) {
				excludeClasses.add(clazz);
			}
		}
		return this;
	}

	public HwTxAutoBindRoutes addExcludeClasses(List<Class<? extends Controller>> clazzes) {
		excludeClasses.addAll(clazzes);
		return this;
	}

	public HwTxAutoBindRoutes addJars(String... jars) {
		if (jars != null) {
			for (String jar : jars) {
				includeJars.add(jar);
			}
		}
		return this;
	}

	private List<Class<? extends Controller>> getControllerClasses() {
		return classSearcher.includeAllJarsInLib(includeAllJarsInLib).injars(includeJars).findInClasspathAndJars(Controller.class);
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void config() {
		List<Class<? extends Controller>> controllerClasses = getControllerClasses();
		ControllerBind controllerBind = null;
		for (Class controller : controllerClasses) {
			if (excludeClasses.contains(controller)) {
				continue;
			}

			ClassLoader classLoader = controller.getClassLoader();
			String _key_prefix = null;
			if (classLoader instanceof ModuleClassLoader) {
				_key_prefix = ((ModuleClassLoader) classLoader).getModule().getIdentifier().getName();
			}

			controllerBind = (ControllerBind) controller.getAnnotation(ControllerBind.class);
			if (controllerBind == null) {
				if (!autoScan) {
					continue;
				}
				String kk = _key_prefix != null ? _key_prefix + "_" + HwTx.controllerKey(controller, suffix) : HwTx.controllerKey(controller, suffix);
				this.add(kk, controller);
				if (logger.isDebugEnabled()) {
					logger.debug("routes.add(" + kk + ", " + controller.getName() + ")");
				}
			} else {
				String cKey = controllerBind.controllerKey();
				if (PathKit.isVariable(cKey)) {
					cKey = PathKit.getVariableValue(cKey);
				}
				String kk = _key_prefix != null ? _key_prefix + "_" + cKey : cKey;
				if (StrKit.isBlank(controllerBind.viewPath())) {
					this.add(kk, controller);
					logger.debug("routes.add(" + kk + ", " + controller.getName() + ")");
				} else {
					this.add(kk, controller, controllerBind.viewPath());
					logger.debug("routes.add(" + kk + ", " + controller + "," + controllerBind.viewPath() + ")");
				}
			}
		}
	}

	private String suffix = "Controller";

	public HwTxAutoBindRoutes includeAllJarsInLib(boolean includeAllJarsInLib) {
		this.includeAllJarsInLib = includeAllJarsInLib;
		return this;
	}

	public HwTxAutoBindRoutes suffix(String suffix) {
		this.suffix = suffix;
		return this;
	}
}
