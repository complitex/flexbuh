package org.complitex.flexbuh.common.logging;

/**
 * @author Pavel Sknar
 *         Date: 10.11.11 16:41
 */
public class EventModel implements EventProperty {

	private String modelName;

	public EventModel(String modelName) {
		this.modelName = modelName;
	}

	@Override
	public String getName() {
		return "model";
	}

	@Override
	public String getValue() {
		return modelName;
	}
}
