package ch.qos.logback.classic.db;

import ch.qos.logback.classic.spi.ILoggingEvent;
import org.complitex.flexbuh.common.logging.Event;

import java.util.Map;

/**
 * @author Pavel Sknar
 *         Date: 03.11.11 15:52
 */
public class FlexbuhDBAppender extends DBAppender {
	@Override
	Map<String, String> mergePropertyMaps(ILoggingEvent event) {
		Map<String, String> result = super.mergePropertyMaps(event);

		if (event.getArgumentArray() != null) {
			for (Object argument : event.getArgumentArray()) {
				if (argument instanceof Event) {
                    result.putAll(((Event) argument).getMap());
				}
			}
		}

		return result;
	}
}
