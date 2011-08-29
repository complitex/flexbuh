package org.complitex.flexbuh.document.entity;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 28.07.11 18:09
 */
@XmlType
@XmlAccessorType(value = XmlAccessType.FIELD)
public class DeclarationValue implements Serializable{
    @XmlTransient private String name;
    @XmlAttribute(name = "ROWNUM") private String rowNum;
    @XmlValue private String value;

    public DeclarationValue() {
    }

    public DeclarationValue(String name) {
        this.name = name;
    }

    public DeclarationValue(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public DeclarationValue(String name, String rowNum, String value) {
        this.name = name;
        this.rowNum = rowNum;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRowNum() {
        return rowNum;
    }

    public void setRowNum(String rowNum) {
        this.rowNum = rowNum;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
