package com.hwtxframework.ioc.impl;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hwtxframework.io.Resource;
import com.hwtxframework.io.support.PropertiesLoaderSupport;
import com.hwtxframework.ioc.ApplicationContext;
import com.hwtxframework.ioc.ApplicationContextAware;
import com.hwtxframework.ioc.ComponentBundle;
import com.hwtxframework.ioc.ComponentChangeListener;
import com.hwtxframework.ioc.Constants;
import com.hwtxframework.ioc.DependencyInfo;
import com.hwtxframework.ioc.FactoryBean;
import com.hwtxframework.ioc.exceptions.ComponentDefinitionException;

public abstract class AbstractApplicationContext implements ApplicationContext {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	@Getter
	protected BaseCache readyComponentCache;
	@Getter
	protected BaseCache unresolvingComponentCache;

	final Map<String, List<DependencyInfo>> iDependcencies = new HashMap<String, List<DependencyInfo>>();
	final Map<String, List<DependencyInfo>> dependcenciesI = new HashMap<String, List<DependencyInfo>>();
	@Getter
	final PropertiesLoaderSupport propertiesLoaderSupport = new PropertiesLoaderSupport();

	@Getter
	final Map<String, String> unresolvingCausing = new HashMap<String, String>();

	@Override
	public Collection<ComponentBundle> getDependencyI(String name) {
		List<ComponentBundle> bundles = new ArrayList<ComponentBundle>();
		List<DependencyInfo> infos = dependcenciesI.get(name);
		if (infos != null) {
			for (DependencyInfo depencyInfo : infos) {
				bundles.add(depencyInfo.getComponentBundle());
			}
		}
		return bundles;
	}

	public AbstractApplicationContext(BaseCache readyServiceCache, BaseCache unresolvingServiceCache) {
		this.readyComponentCache = readyServiceCache;
		this.unresolvingComponentCache = unresolvingServiceCache;
	}

	public void addBundles(Map<String, ComponentBundle> bundles) {
		for (ComponentBundle bundle : bundles.values()) {
			resolve(bundle);
		}
	}

	public void loadBundlesFromFilePath(String... locations) {
		for (String location : locations) {
			try {
				loadComponentDefinitions(location);
			} catch (ComponentDefinitionException e) {
				logger.error("{}", e);
			}
		}
	}

	private int loadComponentDefinitions(String location) throws ComponentDefinitionException {
		try {
			Resource[] resources = ResourceReader.getResource(location);
			int loadCount = loadComponentDefinitions(resources);
			if (logger.isDebugEnabled()) {
				logger.debug("Loaded " + loadCount + " component definitions from location pattern [" + location + "]");
			}
			return loadCount;
		} catch (IOException ex) {
			throw new ComponentDefinitionException("Could not resolve component definition resource pattern ["
					+ location + "]", ex);
		}
	}

	public int loadComponentDefinitions(Resource... resources) {
		ConfigParser parser = new ConfigParser();
		parser.setPropertiesLoaderSupport(propertiesLoaderSupport);
		Map<String, ComponentBundle> bundles = new HashMap<String, ComponentBundle>();
		for (Resource resource : resources) {
			try {
				parser.validate(resource.getInputStream());
				parser.loadConfig(resource.getInputStream());
				bundles.putAll(parser.getBundles());
				dependcenciesI.putAll(parser.getDependcenciesI());
			} catch (Exception e) {
				logger.error("load file " + resource.getFilename() + " failure.", e);
			}
		}
		addBundles(bundles);
		afterLoadBundle();
		return readyComponentCache.cache.size();
	}

	@Override
	public void refreshComponent(String className, String fileName) {
		ComponentBundle bundle = null;
		try {
			bundle = BundleUtil.getAnnotationBundle(className, fileName);
			if (bundle == null) {
				return;
			}
			resolve(bundle);
			injectDependi(bundle);
		} catch (Exception e) {
			logger.error("{}", e);
		}
	}

	private void resolve(ComponentBundle bundle) {
		String _available_field = BundleUtil.isAllReferenceAvailable(bundle, readyComponentCache);
		if (Constants.AVAILABLE.equals(_available_field)) {
			try {
				BundleUtil.injectReference(bundle, readyComponentCache,propertiesLoaderSupport);
				readyComponentCache.add(bundle);
				unresolvingComponentCache.remove(bundle);
				unresolvingCausing.remove(bundle.getName());
			} catch (Exception e) {
				logger.error("inject " + bundle + "failure.", e);
			}
		} else {
			unresolvingComponentCache.add(bundle);
			unresolvingCausing.put(bundle.getName(), _available_field);
		}
	}

	private void injectDependi(ComponentBundle bundle) {
		List<DependencyInfo> infos = dependcenciesI.get(bundle.getName());
		if (infos == null || infos.size() == 0) {
			return;
		}
		for (DependencyInfo dependencyInfo : infos) {
			dependencyInfo.refresh(bundle);
		}
	}

	private void afterLoadBundle() {

		for (ComponentBundle bundle : readyComponentCache.getServiceBundles()) {

			Object instance = bundle.getInstance();
			if (ApplicationContextAware.class.isAssignableFrom(instance.getClass())) {
				((ApplicationContextAware) instance).setApplicationContext(this);
			}
		}
	}

	public void registerServiceChangeListener(ComponentChangeListener listener) {
		readyComponentCache.registerComponentChangeListener(listener);
	}

	public Collection<ComponentBundle> getUnResolvingComponentBundles() {
		return unresolvingComponentCache.getServiceBundles();
	}

	public Collection<ComponentBundle> getReadyComponentBundles() {
		return readyComponentCache.getServiceBundles();
	}

	public void removeReadyComponentBundle(String name) {
		readyComponentCache.removeBundleByName(name);
	}

	@Override
	public void close() {
		dependcenciesI.clear();
		unresolvingCausing.clear();
		unresolvingComponentCache.close();
		for (ComponentBundle bundle : readyComponentCache.cache.values()) {
			if (bundle.getPreDestroy() != null) {
				try {
					Method method = bundle.getInstance().getClass().getMethod(bundle.getPreDestroy(), new Class[] {});
					method.invoke(bundle.getInstance(), new Object[] {});
				} catch (Exception e) {
					logger.error("{}", e);
				}
			}
		}
		readyComponentCache.close();
	}
}