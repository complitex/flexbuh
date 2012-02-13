package org.complitex.flexbuh.document.entity;

import org.complitex.flexbuh.common.entity.SessionObject;
import org.complitex.flexbuh.common.service.FIOBean;
import org.complitex.flexbuh.common.util.DateUtil;

import javax.xml.bind.annotation.*;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 17.11.11 15:58
 */
@XmlType(name = "row")
@XmlAccessorType(value = XmlAccessType.PROPERTY)
public class Employee extends SessionObject{
    private Long personProfileId;

    private Integer htin;

    private String lastName;

    private String firstName;

    private String middleName;

    private Date hbirthday;

    private Date hdateIn;

    private Date hdateOut;

    public Employee() {
    }

    public Employee(Long sessionId) {
        super(sessionId);
    }

    @XmlTransient
    public Long getPersonProfileId() {
        return personProfileId;
    }

    public void setPersonProfileId(Long personProfileId) {
        this.personProfileId = personProfileId;
    }

    @XmlElement(name = "HTIN")
    public Integer getHtin() {
        return htin;
    }

    public void setHtin(Integer htin) {
        this.htin = htin;
    }

    @XmlElement(name = "HNAME")
    public String getHname() {
        return FIOBean.getFIO(lastName, firstName, middleName);
    }

    public void setHname(String hname) {
        lastName = FIOBean.getLastName(hname);
        firstName = FIOBean.getFirstName(hname);
        middleName = FIOBean.getMiddleName(hname);
    }

    @XmlTransient
    public Date getHbirthday() {
        return hbirthday;
    }

    public void setHbirthday(Date hbirthday) {
        this.hbirthday = hbirthday;
    }

    @XmlTransient
    public Date getHdateIn() {
        return hdateIn;
    }

    public void setHdateIn(Date hdateIn) {
        this.hdateIn = hdateIn;
    }

    @XmlTransient
    public Date getHdateOut() {
        return hdateOut;
    }

    public void setHdateOut(Date hdateOut) {
        this.hdateOut = hdateOut;
    }

    @XmlElement(name = "HBIRTHDAY")
    public void setHbirthdayString(String hbirthdayString) {
        hbirthday = DateUtil.getDate(hbirthdayString);
    }

    @XmlElement(name = "HDATE_IN")
    public void setHdateInString(String hdateInString) {
        hdateIn = DateUtil.getDate(hdateInString);
    }

    @XmlElement(name = "HDATE_OUT")
    public void setHdateOutString(String hdateOutString) {
        hdateOut = DateUtil.getDate(hdateOutString);
    }

    @XmlTransient
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @XmlTransient
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @XmlTransient
    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }
}
