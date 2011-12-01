package org.complitex.flexbuh.common.entity.dictionary;

import com.google.common.collect.Sets;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 18.11.11 14:07
 */
@XmlRootElement(name = "zdoc")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class FieldCode implements Serializable{
    public final static String COUNTERPART_SPR_NAME = "spr_contragents";
    public final static String EMPLOYEE_SPR_NAME = "spr_works";
    
    public final static Set<String> IMPLEMENTED = Sets.newHashSet(COUNTERPART_SPR_NAME, EMPLOYEE_SPR_NAME);

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
