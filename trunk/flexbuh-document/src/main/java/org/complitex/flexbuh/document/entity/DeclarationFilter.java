package org.complitex.flexbuh.document.entity;

import org.complitex.flexbuh.common.entity.AbstractFilter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 09.09.11 18:09
 */
public class DeclarationFilter extends AbstractFilter{
    private Long personProfileId;
    private Long parentId;
    private String cDoc;
    private String cDocSub;
    private String name;
    private Integer periodType;
    private Integer periodMonth;
    private Integer periodYear;
    private Date date;

    private Set<Period> periods = new HashSet<>();

    public DeclarationFilter() {
    }

    public DeclarationFilter(Long sessionId) {
        super(sessionId);

        //add period for sql empty select
        periods.add(new Period(0,0,0));
    }

    public DeclarationFilter(Long sessionId, Long parentId) {
        super(sessionId);
        this.parentId = parentId;
    }

    public void clear(){
        name = null;
        date = null;
    }

    public Long getPersonProfileId() {
        return personProfileId;
    }

    public void setPersonProfileId(Long personProfileId) {
        this.personProfileId = personProfileId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getCDoc() {
        return cDoc;
    }

    public void setCDoc(String cDoc) {
        this.cDoc = cDoc;
    }

    public String getCDocSub() {
        return cDocSub;
    }

    public void setCDocSub(String cDocSub) {
        this.cDocSub = cDocSub;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPeriodType() {
        return periodType;
    }

    public void setPeriodType(Integer periodType) {
        this.periodType = periodType;
    }

    public Integer getPeriodMonth() {
        return periodMonth;
    }

    public void setPeriodMonth(Integer periodMonth) {
        this.periodMonth = periodMonth;
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

    public Set<Period> getPeriods() {
        return periods;
    }

    public void setPeriods(Set<Period> periods) {
        this.periods = periods;
    }
}
