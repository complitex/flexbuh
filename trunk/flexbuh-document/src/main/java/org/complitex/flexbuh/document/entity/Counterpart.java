package org.complitex.flexbuh.document.entity;

import org.complitex.flexbuh.common.entity.SessionObject;

import javax.xml.bind.annotation.*;

import static org.complitex.flexbuh.common.util.StringUtil.emptyOnNull;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 16.11.11 14:45
 */
@XmlType(name = "row",
        propOrder = {"personProfileId", "hk", "hname", "hloc", "htel", "hnspdv"})
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Counterpart extends SessionObject {
    private Long personProfileId;

    private Integer num;
    private String hk;
    private String hname;
    private String hloc;
    private String htel;
    private String hnspdv;

    public Counterpart() {
    }

    public Counterpart(Long sessionId) {
        super(sessionId);
    }

    public Counterpart(Long sessionId, Long personProfileId) {
        super(sessionId);
        this.personProfileId = personProfileId;
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

    @XmlElement(name = "HK")
    public String getHk() {
        return emptyOnNull(hk);
    }

    public void setHk(String hk) {
        this.hk = hk;
    }

    @XmlElement(name = "HNAME")
    public String getHname() {
        return hname;
    }

    public void setHname(String hname) {
        this.hname = hname;
    }

    @XmlElement(name = "HLOC")
    public String getHloc() {
        return emptyOnNull(hloc);
    }

    public void setHloc(String hloc) {
        this.hloc = hloc;
    }

    @XmlElement(name = "HTEL")
    public String getHtel() {
        return emptyOnNull(htel);
    }

    public void setHtel(String htel) {
        this.htel = htel;
    }

    @XmlElement(name = "HNSPDV")
    public String getHnspdv() {
        return emptyOnNull(hnspdv);
    }

    public void setHnspdv(String hnspdv) {
        this.hnspdv = hnspdv;
    }
}
