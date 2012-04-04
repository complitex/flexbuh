package org.complitex.flexbuh.common.entity.user;

import org.complitex.flexbuh.common.entity.DomainObject;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 04.04.12 14:49
 */
public class Share extends DomainObject{
    private Long sessionId;
    private Long sharedToSessionId;

    public Share() {
    }

    public Share(Long sessionId, Long sharedToSessionId) {
        this.sessionId = sessionId;
        this.sharedToSessionId = sharedToSessionId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getSharedToSessionId() {
        return sharedToSessionId;
    }

    public void setSharedToSessionId(Long sharedToSessionId) {
        this.sharedToSessionId = sharedToSessionId;
    }
}
