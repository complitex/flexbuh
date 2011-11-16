package org.complitex.flexbuh.logging.entity;

import org.complitex.flexbuh.common.logging.EventProperty;

/**
 * @author Pavel Sknar
 *         Date: 09.11.11 14:49
 */
public class GeneralEventProperty implements EventProperty {

	private String name;
	private String value;

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getValue() {
		return value;
	}
}
