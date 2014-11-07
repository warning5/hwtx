package com.hwtx.framework;

import java.io.File;

import org.jboss.modules.HwTxModuleFinder;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleFinder;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleLoadError;
import org.jboss.modules.ModuleLoader;
import org.jboss.modules.filter.PathFilter;
import org.jboss.modules.filter.PathFilters;

public final class HwTxModuleLoader extends ModuleLoader {

	/**         
	 * Construct a new instance.
	 * 
	 * @param repoRoots
	 *            the array of repository roots to look for modules
	 */
	public HwTxModuleLoader(final File[] repoRoots) {
		this(repoRoots, PathFilters.acceptAll());
	}

	/**
	 * Construct a new instance.
	 * 
	 * @param repoRoots
	 *            the array of repository roots to look for modules
	 * @param pathFilter
	 *            the path filter to apply to roots
	 */
	public HwTxModuleLoader(final File[] repoRoots, final PathFilter pathFilter) {
		super(new ModuleFinder[] { new HwTxModuleFinder(repoRoots, pathFilter) });
	}

	/**
	 * Construct a new instance, using the {@code module.path} system property
	 * or the {@code JAVA_MODULEPATH} environment variable to get the list of
	 * module repository roots.
	 */
	public HwTxModuleLoader() {
		super(new ModuleFinder[] { new HwTxModuleFinder() });
	}

	public Module findModule(String name) {
		return findLoadedModuleLocal(ModuleIdentifier.create(name));
	}

	public void refreshResourceLoaders(final String name) {
		try {
			final Module module = findLoadedModuleLocal(ModuleIdentifier.fromString(name));
			if (module == null) {
				throw new IllegalArgumentException("Module " + name + " not found");
			}
			refreshResourceLoaders(module);
		} catch (ModuleLoadError e) {
			throw new IllegalArgumentException("Error loading module " + name + ": " + e.toString());
		}
	}

	public String toString() {
		final StringBuilder b = new StringBuilder();
		b.append("local module loader @").append(Integer.toHexString(hashCode())).append(" (finder: ").append(getFinders()[0]).append(')');
		return b.toString();
	}
}