package org.complitex.flexbuh.personnel.entity;

import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 18.10.12 15:29
 */
public class AllowanceFilter extends TemporalDomainObjectFilter {

    private String type;

    private Long organizationId;

    private Long sessionId;

    private Date entryIntoForceDate;

    private Date completionDate;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Date getEntryIntoForceDate() {
        return entryIntoForceDate;
    }

    public void setEntryIntoForceDate(Date entryIntoForceDate) {
        this.entryIntoForceDate = entryIntoForceDate;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }
}
