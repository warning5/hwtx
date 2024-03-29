/**
 * Copyright (c) 2011-2014, James Zhan 詹波 (jfinal@126.com).
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

package com.jfinal.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.jfinal.kit.StrKit;

/**
 * JFinalConfig.
 * <p>
 * Config order: configConstant(), configRoute(), configPlugin(),
 * configInterceptor(), configHandler()
 */
public abstract class JFinalConfig {

	/**
	 * Config constant
	 */
	public abstract void configConstant(Constants me);

	/**
	 * Config route
	 */
	public abstract void configRoute(Routes me);

	/**
	 * Config plugin
	 */
	public abstract void configPlugin(Plugins me);

	/**
	 * Config interceptor applied to all actions.
	 */
	public abstract void configInterceptor(Interceptors me);

	/**
	 * Config handler
	 */
	public abstract void configHandler(Handlers me);

	/**
	 * Call back after JFinal start
	 */
	public void afterJFinalStart() {
	};

	public void beforeJFinalStart() {
	};

	/**
	 * Call back before JFinal stop
	 */
	public void beforeJFinalStop() {
	};

	public void afterJFinalStop() {
	};

	private static Properties properties;

	/**
	 * Load property file Example: loadPropertyFile("db_username_pass.txt");
	 * 
	 * @param file
	 *            the file in WEB-INF directory
	 */
	public Properties loadPropertyFile(String file) {
		if (StrKit.isBlank(file))
			throw new IllegalArgumentException("Parameter of file can not be blank");
		if (file.contains(".."))
			throw new IllegalArgumentException("Parameter of file can not contains \"..\"");

		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(file);
		try {
			Properties p = new Properties();
			p.load(inputStream);
			properties = p;
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("Properties file not found: " + file);
		} catch (IOException e) {
			throw new IllegalArgumentException("Properties file can not be loading: " + file);
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (properties == null)
			throw new RuntimeException("Properties file loading failed: " + file);
		return properties;
	}

	public static String getProperty(String key) {
		checkPropertyLoading();
		return properties.getProperty(key);
	}

	public static String getProperty(String key, String defaultValue) {
		checkPropertyLoading();
		return properties.getProperty(key, defaultValue);
	}

	public static Integer getPropertyToInt(String key) {
		checkPropertyLoading();
		Integer resultInt = null;
		String resultStr = properties.getProperty(key);
		if (resultStr != null)
			resultInt = Integer.parseInt(resultStr);
		return resultInt;
	}

	public Integer getPropertyToInt(String key, Integer defaultValue) {
		Integer result = getPropertyToInt(key);
		return result != null ? result : defaultValue;
	}

	public static Boolean getPropertyToBoolean(String key) {
		checkPropertyLoading();
		String resultStr = properties.getProperty(key);
		Boolean resultBool = null;
		if (resultStr != null) {
			if (resultStr.trim().equalsIgnoreCase("true"))
				resultBool = true;
			else if (resultStr.trim().equalsIgnoreCase("false"))
				resultBool = false;
		}
		return resultBool;
	}

	public Boolean getPropertyToBoolean(String key, boolean defaultValue) {
		Boolean result = getPropertyToBoolean(key);
		return result != null ? result : defaultValue;
	}

	private static void checkPropertyLoading() {
		if (properties == null)
			throw new RuntimeException(
					"You must load properties file by invoking loadPropertyFile(String) method in configConstant(Constants) method before.");
	}
}