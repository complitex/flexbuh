package org.complitex.flexbuh.document.entity;

import org.complitex.flexbuh.common.entity.SessionObject;

import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 17.11.11 15:58
 */
public class Employee extends SessionObject{
    private Integer htin;
    private String hname;
    private Date hbirthday;
    private Date hdateIn;
    private Date hdateOut;

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
