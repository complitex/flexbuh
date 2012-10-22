package org.complitex.flexbuh.personnel.entity;

import com.sun.org.apache.xpath.internal.operations.Or;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 02.10.12 15:04
 */
public class ScheduleFilter extends TemporalDomainObjectFilter {

    private String name;

    private String comment;

    private Long organizationId;

    private Long sessionId;

    private boolean admin;

    private Date entryIntoForceDate;

    private Date completionDate;

    public ScheduleFilter() {
    }

    public ScheduleFilter(Organization organization, Long sessionId, boolean admin, Date currentDate, int count) {
        if (organization != null) {
            organizationId = organization.getId();
        }
        this.sessionId = sessionId;
        this.admin = admin;
        this.setCount(count);
        setCurrentDate(currentDate);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment()  {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
