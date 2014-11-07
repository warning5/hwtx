package com.hwtxframework.ioc.impl;



public class ApplicationContextImpl extends AbstractApplicationContext {

	public ApplicationContextImpl(BaseCache readyServiceCache,
			BaseCache unresolvingServiceCache) {
		super(readyServiceCache, unresolvingServiceCache);
	}

	public Object getComponent(String name) {
		if (unresolvingComponentCache.contain(name)) {
			String cause = unresolvingCausing.get(name);
			throw new RuntimeException("can't resolve component " + name
					+ " by " + cause);
		}
		return readyComponentCache.getComponent(name);
	}
}
