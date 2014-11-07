package org.jboss.modules;

import java.io.File;

public class ModuleUtils {
	private static final File[] NO_FILES = new File[0];

	public static File[] getRepoRoots(final boolean supportLayersAndAddOns) {
		return supportLayersAndAddOns ? LayeredModulePathFactory.resolveLayeredModulePath(getModulePathFiles()) : getModulePathFiles();
	}

	public static File[] getModulePathFiles() {
		return getFiles(System.getProperty("module.path", System.getenv("JAVA_MODULEPATH")), 0, 0);
	}

	private static File[] getFiles(final String modulePath, final int stringIdx, final int arrayIdx) {
		if (modulePath == null)
			return NO_FILES;
		final int i = modulePath.indexOf(File.pathSeparatorChar, stringIdx);
		final File[] files;
		if (i == -1) {
			files = new File[arrayIdx + 1];
			files[arrayIdx] = new File(modulePath.substring(stringIdx)).getAbsoluteFile();
		} else {
			files = getFiles(modulePath, i + 1, arrayIdx + 1);
			files[arrayIdx] = new File(modulePath.substring(stringIdx, i)).getAbsoluteFile();
		}
		return files;
	}

	public static String toPathString(ModuleIdentifier moduleIdentifier) {
		final StringBuilder builder = new StringBuilder(40);
		builder.append(moduleIdentifier.getName().replace('.', File.separatorChar));
		builder.append(File.separatorChar).append(moduleIdentifier.getSlot());
		builder.append(File.separatorChar);
		return builder.toString();
	}
}
