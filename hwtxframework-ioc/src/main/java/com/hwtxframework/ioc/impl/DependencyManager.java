package com.hwtxframework.ioc.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hwtxframework.ioc.ApplicationContext;
import com.hwtxframework.ioc.ComponentBundle;
import com.hwtxframework.ioc.ComponentChangeListener;
import com.hwtxframework.ioc.Constants;

public class DependencyManager implements ComponentChangeListener {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	private ApplicationContext context;

	public DependencyManager(ApplicationContext context) {
		this.context = context;
	}

	public void onComponentReady(ComponentBundle instance) {
		Set<ComponentBundle> bundles = new HashSet<ComponentBundle>();
		BaseCache unResolvedCache = context.getUnresolvingComponentCache();
		BaseCache readyCache = context.getReadyComponentCache();
		Collection<ComponentBundle> dependencyI = context.getDependencyI(instance.getName());

		for (ComponentBundle bundle : unResolvedCache.getServiceBundles()) {
			if (dependencyI.contains(bundle)) {
				bundles.add(unResolvedCache.findBundleByReference(bundle.getName()));
			}
		}
		for (ComponentBundle bundle : bundles) {
			String _available_field = BundleUtil.isAllReferenceAvailable(bundle, readyCache);
			if (Constants.AVAILABLE.equals(_available_field)) {
				try {
					BundleUtil.injectReference(bundle, readyCache, context.getPropertiesLoaderSupport());
					readyCache.add(bundle);
					unResolvedCache.remove(bundle);
					context.getUnresolvingCausing().remove(bundle.getName());
				} catch (Exception e) {
					logger.error("inject " + bundle + "failure.", e);
				}
			}
		}
	}
}
