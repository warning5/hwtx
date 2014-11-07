package org.jboss.modules;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;

@XmlRootElement(name = "config")
@XmlAccessorType(XmlAccessType.FIELD)
public class ModuleConfig {

	@Getter
	@XmlElementWrapper(name = "extensions")
	@XmlElement(name = "extension")
	List<Extension> extensions;

	static class Extension {

		@Getter
		@XmlAttribute
		String module;

		@Getter
		@XmlAttribute
		boolean lazy = false;
	}
}
