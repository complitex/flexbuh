package org.complitex.flexbuh.common.entity;

import org.complitex.flexbuh.common.annotation.Display;

import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 08.12.11 17:42
 */
public abstract class AbstractTemporalEntity extends AbstractEntity{
    @Display(visible = false)
    private Long sessionId;

    @Display(order = 0)
    private Long objectId;

    @Display(visible = false)
    private Boolean current;

    private Date dateStart;

    private Date updated;

    @Display(visible = false)
    private String comment;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public Boolean isCurrent() {
        return current;
    }

    public void setCurrent(Boolean current) {
        this.current = current;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
