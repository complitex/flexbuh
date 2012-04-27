package org.complitex.flexbuh.personnel.entity;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.complitex.flexbuh.common.entity.AbstractFilter;

import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 13.04.12 14:15
 */
public class TemporalDomainObjectHistoryFilter extends AbstractFilter {

    private Date beginDateRange;

    private Date endDateRange;

    private String changedProperty;

    public Date getBeginDateRange() {
        return beginDateRange;
    }

    public void setBeginDateRange(Date beginDateRange) {
        this.beginDateRange = beginDateRange;
    }

    public Date getEndDateRange() {
        return endDateRange;
    }

    public void setEndDateRange(Date endDateRange) {
        this.endDateRange = endDateRange;
    }

    public String getChangedProperty() {
        return changedProperty;
    }

    public void setChangedProperty(String changedProperty) {
        this.changedProperty = changedProperty;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
