package com.hwtxframework.ioc;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.hwtxframework.ioc.DependencyInfo.DependPath;
import com.hwtxframework.ioc.impl.BaseCache;
import com.hwtxframework.ioc.value.Value;

@Setter
@Getter
@ToString
@EqualsAndHashCode(of = "name")
public class Property {
	private String name;
	private Value<?> value;

	public <T extends Value<?>> T getPropertyValue(Class<T> type) {
		return type.cast(value);
	}

	public boolean containRef() {
		return value.containRef();
	}

	public void setVariable(Properties properties) {
		value.setVariable(properties);
	}

	public String getUnResolved(BaseCache readyServiceCache) {
		return value.getUnResolved(readyServiceCache);
	}

	public Map<String, List<DependPath>> getDenpendRefs() {
		return value.getDenpendRefs();
	}

	public void refresh(ComponentBundle source, ComponentBundle target, String propertyName,
			List<? extends DependPath> dependPaths) {
		value.refresh(source, target, propertyName, dependPaths);
	}
}
