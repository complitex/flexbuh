package org.complitex.flexbuh.common.logging;

/**
 * @author Pavel Sknar
 *         Date: 10.11.11 16:44
 */
public class EventObjectId implements EventProperty {

	private Long objectId;

	public EventObjectId(Long objectId) {
		this.objectId = objectId;
	}

	@Override
	public String getName() {
		return "objectId";
	}

	@Override
	public String getValue() {
		return Long.toString(objectId);
	}
}
