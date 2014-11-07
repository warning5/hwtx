/**
 * Copyright (c) 2011-2013, kidzhou 周磊 (zhouleib1412@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jfinal.core;

import static java.lang.Thread.currentThread;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfinal.ext.kit.Reflect;
import com.jfinal.log.Logger;

public class HwTxClassSearcher {

	protected static final Logger LOG = Logger.getLogger(HwTxClassSearcher.class);
	private Map<ClassLoader, List<String>> classes = Maps.newHashMap();
	private Map<ClassLoader, List<String>> libs = Maps.newHashMap();

	@SuppressWarnings("unchecked")
	private static <T> List<Class<? extends T>> extraction(Class<T> clazz, Map<ClassLoader, List<String>> classFileList) {
		List<Class<? extends T>> classList = Lists.newArrayList();
		ClassLoader old = null;
		for (Entry<ClassLoader, List<String>> entry : classFileList.entrySet()) {
			old = currentThread().getContextClassLoader();
			currentThread().setContextClassLoader(entry.getKey());
			try {
				for (String classFile : entry.getValue()) {
					Class<?> classInFile = Reflect.on(classFile).get();
					if (clazz.isAssignableFrom(classInFile) && clazz != classInFile) {
						classList.add((Class<? extends T>) classInFile);
					}
				}
			} finally {
				if (old != null) {
					currentThread().setContextClassLoader(old);
				}
			}
		}

		return classList;
	}

	public HwTxClassSearcher(Map<ClassLoader, List<String>> mapping) {
		for (Entry<ClassLoader, List<String>> entry : mapping.entrySet()) {

			for (String path : entry.getValue()) {
				if (path.endsWith(".jar")) {
					List<String> paths = libs.get(entry.getKey());
					if (paths == null) {
						paths = Lists.newArrayList();
					}
					paths.add(path);
					libs.put(entry.getKey(), paths);
				} else {
					List<String> paths = classes.get(entry.getKey());
					if (paths == null) {
						paths = Lists.newArrayList();
					}
					paths.add(path);
					classes.put(entry.getKey(), paths);
				}
			}
		}
	}

	/**
	 * 递归查找文件
	 * 
	 * @param baseDirName
	 *            查找的文件夹路径
	 * @param targetFileName
	 *            需要查找的文件名
	 */
	private static List<String> findFiles(String baseDirName, String targetFileName) {
		/**
		 * 算法简述： 从某个给定的需查找的文件夹出发，搜索该文件夹的所有子文件夹及文件，
		 * 若为文件，则进行匹配，匹配成功则加入结果集，若为子文件夹，则进队列。 队列不空，重复上述操作，队列为空，程序结束，返回结果。
		 */
		List<String> classFiles = Lists.newArrayList();
		String tempName = null;
		// 判断目录是否存在
		File baseDir = new File(baseDirName);
		if (!baseDir.exists() || !baseDir.isDirectory()) {
			LOG.error("search error：" + baseDirName + "is not a dir！");
		} else {
			String[] filelist = baseDir.list();
			for (int i = 0; i < filelist.length; i++) {
				File readfile = new File(baseDirName + File.separator + filelist[i]);
				if (readfile.isDirectory()) {
					classFiles.addAll(findFiles(baseDirName + File.separator + filelist[i], targetFileName));
				} else {
					tempName = readfile.getName();
					if (HwTxClassSearcher.wildcardMatch(targetFileName, tempName)) {
						String classname;
						String tem = readfile.getAbsoluteFile().toString().replaceAll("\\\\", "/");
						classname = tem.substring(tem.indexOf("/classes") + "/classes".length() + 1, tem.indexOf(".class"));
						classFiles.add(classname.replaceAll("/", "."));
					}
				}
			}
		}
		return classFiles;
	}

	/**
	 * 通配符匹配
	 * 
	 * @param pattern
	 *            通配符模式
	 * @param str
	 *            待匹配的字符串 <a href="http://my.oschina.net/u/556800"
	 *            target="_blank" rel="nofollow">@return</a>
	 *            匹配成功则返回true，否则返回false
	 */
	private static boolean wildcardMatch(String pattern, String str) {
		int patternLength = pattern.length();
		int strLength = str.length();
		int strIndex = 0;
		char ch;
		for (int patternIndex = 0; patternIndex < patternLength; patternIndex++) {
			ch = pattern.charAt(patternIndex);
			if (ch == '*') {
				// 通配符星号*表示可以匹配任意多个字符
				while (strIndex < strLength) {
					if (wildcardMatch(pattern.substring(patternIndex + 1), str.substring(strIndex))) {
						return true;
					}
					strIndex++;
				}
			} else if (ch == '?') {
				// 通配符问号?表示匹配任意一个字符
				strIndex++;
				if (strIndex > strLength) {
					// 表示str中已经没有字符匹配?了。
					return false;
				}
			} else {
				if ((strIndex >= strLength) || (ch != str.charAt(strIndex))) {
					return false;
				}
				strIndex++;
			}
		}
		return strIndex == strLength;
	}

	private boolean includeAllJarsInLib = false;

	private List<String> includeJars = Lists.newArrayList();

	public HwTxClassSearcher injars(List<String> jars) {
		if (jars != null) {
			includeJars.addAll(jars);
		}
		return this;
	}

	public HwTxClassSearcher inJars(String... jars) {
		if (jars != null) {
			for (String jar : jars) {
				includeJars.add(jar);
			}
		}
		return this;
	}

	private static Map<ClassLoader, List<String>> findFiles(Map<ClassLoader, List<String>> baseDirNames, String targetFileName) {
		Map<ClassLoader, List<String>> classFiles = Maps.newHashMap();
		for (Entry<ClassLoader, List<String>> entry : baseDirNames.entrySet()) {
			for (String path : entry.getValue()) {
				List<String> files = classFiles.get(entry.getKey());
				if (files == null) {
					files = Lists.newArrayList();
				}
				files.addAll(findFiles(path, targetFileName));
				classFiles.put(entry.getKey(), files);
			}
		}
		return classFiles;
	}

	private Map<ClassLoader, List<String>> findjarFiles(Map<ClassLoader, List<String>> baseDirNames,
			final List<String> includeJars) {
		Map<ClassLoader, List<String>> classFiles = Maps.newHashMap();
		for (Entry<ClassLoader, List<String>> entry : baseDirNames.entrySet()) {
			for (String jar : entry.getValue()) {
				List<String> files = classFiles.get(entry.getKey());
				if (files == null) {
					files = Lists.newArrayList();
				}
				files.addAll(findjarFiles(jar, includeJars));
				classFiles.put(entry.getKey(), files);
			}
		}
		return classFiles;
	}

	public <T> List<Class<? extends T>> findInClasspathAndJars(Class<T> clazz) {
		Map<ClassLoader, List<String>> classFileList = findjarFiles(libs, includeJars);
		for (Entry<ClassLoader, List<String>> entry : findFiles(classes, "*.class").entrySet()) {
			if (classFileList.containsKey(entry.getKey())) {
				classFileList.get(entry.getKey()).addAll(entry.getValue());
			} else {
				classFileList.put(entry.getKey(), entry.getValue());
			}
		}
		return extraction(clazz, classFileList);
	}

	/**
	 * 查找jar包中的class
	 * 
	 * @param jarUrl
	 *            jar路径
	 * @param includeJars
	 */
	private List<String> findjarFiles(String jarUrl, final List<String> includeJars) {
		List<String> classFiles = Lists.newArrayList();
		try {

			File baseDir = new File(jarUrl);
			if (!baseDir.exists() || baseDir.isDirectory()) {
				LOG.error("file serach error：" + jarUrl + " is a dir！");
			} else {
				if (includeAllJarsInLib || includeJars.contains(jarUrl)) {
					return null;
				}
				JarFile localJarFile = new JarFile(new File(jarUrl));
				Enumeration<JarEntry> entries = localJarFile.entries();
				while (entries.hasMoreElements()) {
					JarEntry jarEntry = entries.nextElement();
					String entryName = jarEntry.getName();
					if (!jarEntry.isDirectory() && entryName.endsWith(".class")) {
						String className = entryName.replaceAll("/", ".").substring(0, entryName.length() - 6);
						classFiles.add(className);
					}
				}
				localJarFile.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return classFiles;

	}

	public HwTxClassSearcher includeAllJarsInLib(boolean includeAllJarsInLib) {
		this.includeAllJarsInLib = includeAllJarsInLib;
		return this;
	}
}
