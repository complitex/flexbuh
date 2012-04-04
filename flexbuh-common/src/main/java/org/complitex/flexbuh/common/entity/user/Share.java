package org.complitex.flexbuh.common.entity.user;

import org.complitex.flexbuh.common.entity.DomainObject;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 04.04.12 14:49
 */
public class Share extends DomainObject{
    private Long sessionId;
    private Long sharedSessionId;

    public Share() {
    }

    public Share(Long sessionId, Long sharedSessionId) {
        this.sessionId = sessionId;
        this.sharedSessionId = sharedSessionId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getSharedSessionId() {
        return sharedSessionId;
    }

    public void setSharedSessionId(Long sharedSessionId) {
        this.sharedSessionId = sharedSessionId;
    }
}
