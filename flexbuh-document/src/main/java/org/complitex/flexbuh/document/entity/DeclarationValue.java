package org.complitex.flexbuh.document.entity;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 28.07.11 18:09
 */
@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
public class DeclarationValue implements Serializable, Comparable{
    @XmlTransient
    private Long id;

    @XmlTransient
    private Long declarationId;

    @XmlAttribute(name = "ROWNUM")
    private Integer rowNum;

    @XmlTransient
    private String name;

    @XmlValue
    private String value;

    @XmlTransient
    private String type;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDeclarationId() {
        return declarationId;
    }

    public void setDeclarationId(Long declarationId) {
        this.declarationId = declarationId;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int compareTo(Object o) {
        if (rowNum != null && o instanceof DeclarationValue && ((DeclarationValue) o).getRowNum() != null){
            return rowNum.compareTo(((DeclarationValue) o).getRowNum());
        }

        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeclarationValue that = (DeclarationValue) o;

        if (declarationId != null ? !declarationId.equals(that.declarationId) : that.declarationId != null)
            return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (rowNum != null ? !rowNum.equals(that.rowNum) : that.rowNum != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;

        result = 31 * result + (declarationId != null ? declarationId.hashCode() : 0);
        result = 31 * result + (rowNum != null ? rowNum.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);

        return result;
    }
}
