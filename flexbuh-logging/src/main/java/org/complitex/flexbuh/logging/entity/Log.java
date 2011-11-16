package org.complitex.flexbuh.logging.entity;

import com.google.common.collect.Lists;
import org.complitex.flexbuh.common.entity.DomainObject;
import org.complitex.flexbuh.common.logging.EventProperty;

import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 08.11.11 12:32
 */
public class Log extends DomainObject {

	public static enum LEVEL {INFO, ERROR, WARN}

	private long time;
    private String controller;
    private String description;
	private String level;

	private List<EventProperty> properties = Lists.newArrayList();

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getController() {
		return controller;
	}

	public void setController(String controller) {
		this.controller = controller;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public List<EventProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<EventProperty> properties) {
		this.properties = properties;
	}
	/*
	public String getObjectId() {
		return getPropertyValue("objectId");
	}

	public String getLogin() {
		return getPropertyValue("login");
	}

	public String getModule() {
		return getPropertyValue("module");
	}

	public String getModel() {
		return getPropertyValue("model");
	}

	public String getCategory() {
		return getPropertyValue("category");
	}

	private String getPropertyValue(String name) {
		for (EventProperty property : properties) {
			if (StringUtils.equals(property.getName(), name)) {
				return property.getValue();
			}
		}
		return null;
	}
	*/
}
