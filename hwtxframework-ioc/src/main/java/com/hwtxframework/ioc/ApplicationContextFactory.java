package com.hwtxframework.ioc;

import com.hwtxframework.ioc.impl.ApplicationContextImpl;
import com.hwtxframework.ioc.impl.BaseCache;
import com.hwtxframework.ioc.impl.DependencyManager;

public class ApplicationContextFactory {

	public static ApplicationContext createApplicationContext(String... configFiles) {
		ApplicationContextImpl applicationContext = new ApplicationContextImpl(new BaseCache(), new BaseCache());
		applicationContext.registerServiceChangeListener(new DependencyManager(applicationContext));
		applicationContext.loadBundlesFromFilePath(configFiles);
		return applicationContext;
	}
}
