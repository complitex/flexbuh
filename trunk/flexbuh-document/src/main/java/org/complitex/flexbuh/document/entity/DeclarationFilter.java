package org.complitex.flexbuh.document.entity;

import org.complitex.flexbuh.common.entity.AbstractFilter;

import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 09.09.11 18:09
 */
public class DeclarationFilter extends AbstractFilter{
    private String name;
    private Integer periodMonth;
    private Integer periodType;
    private Integer periodYear;
    private Date date;
    private Long personProfileId;

    public DeclarationFilter() {
    }

    public DeclarationFilter(Long sessionId) {
        super(sessionId);
    }

    public void clear(){
        name = null;
        periodMonth = null;
        periodType = null;
        periodYear = null;
        date = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPeriodMonth() {
        return periodMonth;
    }

    public void setPeriodMonth(Integer periodMonth) {
        this.periodMonth = periodMonth;
    }

    public Integer getPeriodType() {
        return periodType;
    }

    public void setPeriodType(Integer periodType) {
        this.periodType = periodType;
    }

    public Integer getPeriodYear() {
        return periodYear;
    }

    public void setPeriodYear(Integer periodYear) {
        this.periodYear = periodYear;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getPersonProfileId() {
        return personProfileId;
    }

    public void setPersonProfileId(Long personProfileId) {
        this.personProfileId = personProfileId;
    }
}
