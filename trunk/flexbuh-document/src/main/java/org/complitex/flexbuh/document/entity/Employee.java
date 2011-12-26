package org.complitex.flexbuh.document.entity;

import org.complitex.flexbuh.common.entity.SessionObject;
import org.complitex.flexbuh.common.util.DateUtil;

import javax.xml.bind.annotation.*;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 17.11.11 15:58
 */
@XmlType(name = "row")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class Employee extends SessionObject{
    @XmlTransient
    private Long personProfileId;

    @XmlElement(name = "HTIN")
    private Integer htin;

    @XmlElement(name = "HNAME")
    private String hname;

    @XmlTransient
    private Date hbirthday;

    @XmlTransient
    private Date hdateIn;

    @XmlTransient
    private Date hdateOut;

    @XmlElement(name = "HBIRTHDAY")
    private String hbirthdayString;

    @XmlElement(name = "HDATE_IN")
    private String hdateInString;

    @XmlElement(name = "HDATE_OUT")
    private String hdateOutString;

    public Employee() {
    }

    public Employee(Long sessionId) {
        super(sessionId);
    }

    public void updateDates(){
        hdateOut = DateUtil.getDate(hdateOutString);
        hdateIn = DateUtil.getDate(hdateInString);
        hbirthday = DateUtil.getDate(hbirthdayString);
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

    public String getHbirthdayString() {
        return hbirthdayString;
    }

    public void setHbirthdayString(String hbirthdayString) {
        this.hbirthdayString = hbirthdayString;
    }

    public String getHdateInString() {
        return hdateInString;
    }

    public void setHdateInString(String hdateInString) {
        this.hdateInString = hdateInString;
    }

    public String getHdateOutString() {
        return hdateOutString;
    }

    public void setHdateOutString(String hdateOutString) {
        this.hdateOutString = hdateOutString;
    }
}
