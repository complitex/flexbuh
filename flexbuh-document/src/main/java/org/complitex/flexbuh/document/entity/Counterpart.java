package org.complitex.flexbuh.document.entity;

import org.complitex.flexbuh.common.entity.SessionObject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 16.11.11 14:45
 */
@XmlType(name = "row")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class Counterpart extends SessionObject {
    @XmlElement(name = "HK")
    private String hk;

    @XmlElement(name = "HNAME")
    private String hname;

    @XmlElement(name = "HLOC")
    private String hloc;

    @XmlElement(name = "HTEL")
    private String htel;

    @XmlElement(name = "HNSPDV")
    private String hnspdv;

    public Counterpart() {
    }

    public Counterpart(Long sessionId) {
        super(sessionId);
    }

    public String getHk() {
        return hk;
    }

    public void setHk(String hk) {
        this.hk = hk;
    }

    public String getHname() {
        return hname;
    }

    public void setHname(String hname) {
        this.hname = hname;
    }

    public String getHloc() {
        return hloc;
    }

    public void setHloc(String hloc) {
        this.hloc = hloc;
    }

    public String getHtel() {
        return htel;
    }

    public void setHtel(String htel) {
        this.htel = htel;
    }

    public String getHnspdv() {
        return hnspdv;
    }

    public void setHnspdv(String hnspdv) {
        this.hnspdv = hnspdv;
    }
}
