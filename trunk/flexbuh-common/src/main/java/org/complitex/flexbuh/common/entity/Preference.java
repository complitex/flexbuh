package org.complitex.flexbuh.common.entity;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 05.04.12 14:45
 */
public class Preference extends DomainObject{
    private Long sessionId;
    private String key;
    private String value;

    public Preference() {
    }

    public Preference(Long sessionId, String key) {
        this.sessionId = sessionId;
        this.key = key;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
