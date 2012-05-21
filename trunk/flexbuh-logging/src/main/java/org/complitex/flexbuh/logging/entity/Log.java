package org.complitex.flexbuh.logging.entity;

import org.complitex.flexbuh.common.entity.DomainObject;
import org.complitex.flexbuh.common.logging.EventKey;
import org.complitex.flexbuh.common.logging.EventProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Pavel Sknar
 *         Date: 08.11.11 12:32
 */
public class Log extends DomainObject {
	public static enum LEVEL {DEBUG, INFO, ERROR, WARN}

    private final static Pattern MODULE_PATTERN = Pattern.compile("(org\\.complitex\\.flexbuh\\.\\w+)\\.(.+)");

	private long timestmp;
    private String formattedMessage;
	private String level;
    private String loggerName;

	private Map<String, String> map;
    private List<EventProperty> eventProperties;

    public String get(EventKey key){
        if (eventProperties != null && (map == null || eventProperties.size() != map.size())){
            map = new HashMap<>();

            for (EventProperty p : eventProperties){
                map.put(p.getMappedKey(), p.getMappedValue());
            }
        }

        return map.get(key.name());
    }

    public String getModuleName(){
        Matcher matcher = MODULE_PATTERN.matcher(loggerName);

        if (matcher.matches()){
            return matcher.group(1);
        }

        return null;
    }

    public boolean containsKey(EventKey key){
        return map.containsKey(key.name());
    }

    public long getTimestmp() {
        return timestmp;
    }

    public void setTimestmp(long timestmp) {
        this.timestmp = timestmp;
    }

    public String getFormattedMessage() {
        return formattedMessage;
    }

    public void setFormattedMessage(String formattedMessage) {
        this.formattedMessage = formattedMessage;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getLoggerName() {
        return loggerName;
    }

    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public List<EventProperty> getEventProperties() {
        return eventProperties;
    }

    public void setEventProperties(List<EventProperty> eventProperties) {
        this.eventProperties = eventProperties;
    }
}
