package org.complitex.flexbuh.document.entity;

import org.complitex.flexbuh.common.entity.AbstractFilter;

import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 17.11.11 16:39
 */
public class EmployeeFilter extends AbstractFilter{
    private Long personProfileId;
    private Integer htin;
    private String hname;
    private Date hbirthday;
    private Date hdateIn;
    private Date hdateOut;

    public EmployeeFilter() {
    }

    public EmployeeFilter(Long sessionId, Long personProfileId) {
        super(sessionId);
        this.personProfileId = personProfileId;
    }

    public void clear(){
        htin = null;
        hname = null;
        hbirthday = null;
        hdateIn = null;
        hdateOut = null;
    }

    public Long getPersonProfileId() {
        return personProfileId;
    }

    public void setPersonProfileId(Long personProfileId) {
        this.personProfileId = personProfileId;
    }

    public Integer getHtin() {
        return htin;
    }

    public void setHtin(Integer htin) {
        this.htin = htin;
    }

    public String getHname() {
        return hname;
    }

    public void setHname(String hname) {
        this.hname = hname;
    }

    public Date getHbirthday() {
        return hbirthday;
    }

    public void setHbirthday(Date hbirthday) {
        this.hbirthday = hbirthday;
    }

    public Date getHdateIn() {
        return hdateIn;
    }

    public void setHdateIn(Date hdateIn) {
        this.hdateIn = hdateIn;
    }

    public Date getHdateOut() {
        return hdateOut;
    }

    public void setHdateOut(Date hdateOut) {
        this.hdateOut = hdateOut;
    }
}
