package org.complitex.flexbuh.personnel.entity;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.complitex.flexbuh.common.entity.AbstractFilter;

import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 19.03.12 17:32
 */
public class DepartmentFilter extends AbstractFilter {

    private String name;

    private String code;

    private Long organizationId;

    private Date entryIntoForceDate;

    private Date completionDate;

    private boolean byCurrentDate;

    private Long masterId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getMasterId() {
        return masterId;
    }

    public void setMasterId(Long masterId) {
        this.masterId = masterId;
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

    public boolean isByCurrentDate() {
        return byCurrentDate;
    }

    public void setByCurrentDate(boolean byCurrentDate) {
        this.byCurrentDate = byCurrentDate;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
