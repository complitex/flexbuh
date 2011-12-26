package org.complitex.flexbuh.document.entity;

import org.complitex.flexbuh.common.entity.AbstractFilter;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 16.11.11 14:54
 */
public class CounterpartFilter extends AbstractFilter{
    private Long personProfileId;
    private String hk;
    private String hname;
    private String hloc;
    private String htel;
    private String hnspdv;

    public CounterpartFilter() {
    }

    public CounterpartFilter(Long sessionId, Long personProfileId) {
        super(sessionId);
        this.personProfileId = personProfileId;
    }

    public void clear(){
        hk = null;
        hname = null;
        hloc = null;
        htel = null;
        hnspdv = null;
    }

    public Long getPersonProfileId() {
        return personProfileId;
    }

    public void setPersonProfileId(Long personProfileId) {
        this.personProfileId = personProfileId;
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
