package org.complitex.flexbuh.document.entity;

import org.complitex.flexbuh.common.entity.SessionObject;
import org.complitex.flexbuh.common.util.DateUtil;
import org.complitex.flexbuh.common.util.FIOUtil;

import javax.xml.bind.annotation.*;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 17.11.11 15:58
 */
@XmlType(name = "row",
        propOrder = {"personProfileId", "htin", "hname", "hbirthdayString", "hdateInString", "hdateOutString"})
@XmlAccessorType(value = XmlAccessType.PROPERTY)
public class Employee extends SessionObject{
    private Long personProfileId;
    private Integer num;
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

    @XmlElement(name = "FB_PERSON_PROFILE_ID")
    public Long getPersonProfileId() {
        return personProfileId;
    }

    public void setPersonProfileId(Long personProfileId) {
        this.personProfileId = personProfileId;
    }

    @XmlAttribute(name = "num")
    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
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
        return FIOUtil.getFIO(lastName, firstName, middleName);
    }

    public void setHname(String hname) {
        lastName = FIOUtil.getLastName(hname);
        firstName = FIOUtil.getFirstName(hname);
        middleName = FIOUtil.getMiddleName(hname);
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
    public String getHbirthdayString(){
        return DateUtil.getString(hbirthday);
    }

    public void setHbirthdayString(String hbirthdayString) {
        hbirthday = DateUtil.getDate(hbirthdayString);
    }

    @XmlElement(name = "HDATE_IN")
    public String getHdateInString(){
        return DateUtil.getString(hdateIn);
    }

    public void setHdateInString(String hdateInString) {
        hdateIn = DateUtil.getDate(hdateInString);
    }

    @XmlElement(name = "HDATE_OUT")
    public String getHdateOutString(){
        return DateUtil.getString(hdateOut);
    }

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
