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
    @XmlAttribute(name = "ROWNUM") private Integer rowNum;
    @XmlTransient private String name;
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

    public DeclarationValue(Integer rowNum, String name, String value) {
        this.rowNum = rowNum;
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRowNum() {
        return rowNum;
    }

    public void setRowNum(Integer rowNum) {
        this.rowNum = rowNum;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
