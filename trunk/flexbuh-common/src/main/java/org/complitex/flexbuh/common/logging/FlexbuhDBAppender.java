package org.complitex.flexbuh.common.logging;

import ch.qos.logback.classic.db.DBAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Pavel Sknar
 *         Date: 03.11.11 15:52
 */
public class FlexbuhDBAppender extends DBAppender {
    Map<String, String> mergePropertyMaps(ILoggingEvent event) {
        Map<String, String> mergedMap = new HashMap<>();

        Map<String, String> loggerContextMap = event.getLoggerContextVO().getPropertyMap();
        Map<String, String> mdcMap = event.getMDCPropertyMap();

        if (loggerContextMap != null) {
            mergedMap.putAll(loggerContextMap);
        }

        if (mdcMap != null) {
            mergedMap.putAll(mdcMap);
        }

        //Event
        if (event.getArgumentArray() != null) {
            for (Object argument : event.getArgumentArray()) {
                if (argument instanceof Event) {
                    mergedMap.putAll(((Event) argument).getMap());
                }
            }
        }

        return mergedMap;
    }

    @Override
    protected void secondarySubAppend(ILoggingEvent event, Connection connection, long eventId) throws Throwable {
        Map<String, String> mergedMap = mergePropertyMaps(event);
        boolean hasThrowable = event.getThrowableProxy() != null;

        if (hasThrowable) {
            mergedMap.put(EventKey.ERROR_MESSAGE.name(), event.getThrowableProxy().getMessage());
        }

        insertProperties(mergedMap, connection, eventId);

        if (hasThrowable){
            insertThrowable(event.getThrowableProxy(), connection, eventId);
        }
    }
}
