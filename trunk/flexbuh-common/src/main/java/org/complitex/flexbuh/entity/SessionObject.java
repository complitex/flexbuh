package org.complitex.flexbuh.entity;

import javax.xml.bind.annotation.XmlTransient;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 15.09.11 16:01
 */
public abstract class SessionObject extends DomainObject{
    private Long sessionId;

    @XmlTransient
    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }
}
