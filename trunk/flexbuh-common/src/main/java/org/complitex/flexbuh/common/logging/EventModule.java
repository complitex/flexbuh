package org.complitex.flexbuh.common.logging;

/**
 * @author Pavel Sknar
 *         Date: 03.11.11 17:21
 */
public class EventModule implements EventProperty {

	private String moduleName;

	private EventModule() {
	}

	public EventModule(String moduleName) {
		this.moduleName = moduleName;
	}

	@Override
	public String getName() {
		return "module";
	}

	@Override
	public String getValue() {
		return moduleName;
	}
}
