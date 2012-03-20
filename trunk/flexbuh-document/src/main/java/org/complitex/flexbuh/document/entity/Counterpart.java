package org.complitex.flexbuh.document.entity;

import org.complitex.flexbuh.common.entity.SessionObject;
import org.complitex.flexbuh.common.util.FIOUtil;

import javax.xml.bind.annotation.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 16.11.11 14:45
 */
@XmlType(name = "row")
@XmlAccessorType(value = XmlAccessType.PROPERTY)
public class Counterpart extends SessionObject {
    @XmlTransient
    private Long personProfileId;

    private String hk;

    private String lastName;

    private String firstName;

    private String middleName;

    private String hloc;

    private String htel;

    private String hnspdv;

    public Counterpart() {
    }

    public Counterpart(Long sessionId) {
        super(sessionId);
    }

    public Long getPersonProfileId() {
        return personProfileId;
    }

    public void setPersonProfileId(Long personProfileId) {
        this.personProfileId = personProfileId;
    }

    @XmlElement(name = "HK")
    public String getHk() {
        return hk;
    }

    public void setHk(String hk) {
        this.hk = hk;
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

    @XmlElement(name = "HLOC")
    public String getHloc() {
        return hloc;
    }

    public void setHloc(String hloc) {
        this.hloc = hloc;
    }

    @XmlElement(name = "HTEL")
    public String getHtel() {
        return htel;
    }

    public void setHtel(String htel) {
        this.htel = htel;
    }

    @XmlElement(name = "HNSPDV")
    public String getHnspdv() {
        return hnspdv;
    }

    public void setHnspdv(String hnspdv) {
        this.hnspdv = hnspdv;
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
