package org.complitex.flexbuh.common.entity.dictionary;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 18.11.11 14:07
 */
@XmlRootElement(name = "zdoc")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class FieldCode implements Serializable{
    @XmlTransient
    private Long id;

    @XmlElementWrapper(name = "codes")
    @XmlElement(name = "code")
    private List<String> codes;

    @XmlElement(name = "field")
    private List<Field> fields;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getCodes() {
        return codes;
    }

    public void setCodes(List<String> codes) {
        this.codes = codes;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return "FieldCode{" +
                "codes=" + codes +
                ", fields=" + fields +
                '}';
    }
}
