package org.complitex.flexbuh.common.entity.dictionary;

import org.complitex.flexbuh.common.entity.DomainObject;

import javax.xml.bind.annotation.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 18.11.11 13:56
 */
@XmlType
@XmlAccessorType(value = XmlAccessType.FIELD)
public class Field extends DomainObject{
    @XmlTransient
    private Long fieldCodeId;
    
    @XmlAttribute(name = "name")
    private String name;

    @XmlAttribute(name = "spr_name")
    private String sprName;

    @XmlAttribute(name = "preffix")
    private String prefix;

    @XmlAttribute(name = "alias")
    private String alias;

    public Field() {
    }

    public Field(String name, String sprName, String prefix, String alias) {
        this.name = name;
        this.sprName = sprName;
        this.prefix = prefix;
        this.alias = alias;
    }

    public Long getFieldCodeId() {
        return fieldCodeId;
    }

    public void setFieldCodeId(Long fieldCodeId) {
        this.fieldCodeId = fieldCodeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSprName() {
        return sprName;
    }

    public void setSprName(String sprName) {
        this.sprName = sprName;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public String toString() {
        return "Field{" +
                "name='" + name + '\'' +
                ", sprName='" + sprName + '\'' +
                ", prefix='" + prefix + '\'' +
                ", alias='" + alias + '\'' +
                '}';
    }
}
