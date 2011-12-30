package org.complitex.flexbuh.document.entity;

import java.io.Serializable;

/**
* @author Anatoly A. Ivanov java@inheaven.ru
*         Date: 30.12.11 14:27
*/
public class Period implements Serializable {
    private Integer type;
    private Integer month;
    private String label;

    public Period() {
    }

    public Period(int type, int month, String label) {
        this.type = type;
        this.month = month;
        this.label = label;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
