package org.complitex.flexbuh.personnel.entity;

import org.complitex.flexbuh.common.entity.AbstractFilter;

import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 31.08.12 18:48
 */
public abstract class TemporalDomainObjectFilter extends AbstractFilter {

    private Date currentDate;

    private Long id;

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
