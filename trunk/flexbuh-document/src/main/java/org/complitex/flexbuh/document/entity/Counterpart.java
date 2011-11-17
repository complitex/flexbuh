package org.complitex.flexbuh.document.entity;

import org.complitex.flexbuh.common.entity.SessionObject;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 16.11.11 14:45
 */
public class Counterpart extends SessionObject {
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
