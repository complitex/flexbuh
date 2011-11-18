package org.complitex.flexbuh.common.logging;

import org.complitex.flexbuh.common.service.Marshaller;

import javax.ejb.EJB;
import javax.ejb.Singleton;

/**
 * @author Pavel Sknar
 *         Date: 17.11.11 14:44
 */
@Singleton(name = "EventObjectFactory")
public class EventObjectFactory {

	@EJB
	private Marshaller marshaller;

	public EventProperty getEventNewObject(final Object obj) {
		return new EventProperty() {
			@Override
			public String getName() {
				return "newObject";
			}

			@Override
			public String getValue() {
				return marshaller.marshal(obj);
			}
		};
	}

	public EventProperty getEventOldObject(final Object obj) {
		return new EventProperty() {
			@Override
			public String getName() {
				return "oldObject";
			}

			@Override
			public String getValue() {
				return marshaller.marshal(obj);
			}
		};
	}
}
