package com.hwtx.config;

import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;

@XmlRootElement(name = "config")
@XmlAccessorType(XmlAccessType.FIELD)
public class HwTxXmlConfig {

	@Getter
	@XmlElementWrapper(name = "base")
	@XmlElement(name = "property")
	List<Property> properties;

	@Getter
	@XmlElement(name = "file_monitor")
	FileMonitor fileMonitor;

	public static class Property {

		@Getter
		@XmlAttribute
		private String name;
		@Getter
		@XmlAttribute
		private String value;
	}

	public static class FileMonitor {

		@Getter
		@XmlAttribute
		boolean enable;

		@Getter
		@XmlElement(name = "item")
		Set<Item> items;
	}

	public static class Item {

		@Getter
		@XmlAttribute
		String module;

		@Getter
		@XmlElement(name = "source")
		String source;

		@Getter
		@XmlElement(name = "target")
		String target;

		@Getter
		@XmlElement(name = "exclude")
		ExcludeSet excludeSet;
	}

	public static class ExcludeSet {

		@Getter
		@XmlElement(name = "value")
		Set<String> values;
	}
}
