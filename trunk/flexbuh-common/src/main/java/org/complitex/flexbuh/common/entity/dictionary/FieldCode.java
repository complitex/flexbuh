package org.complitex.flexbuh.common.entity.dictionary;

import com.google.common.collect.Sets;
import org.complitex.flexbuh.common.entity.RowSet;

import javax.xml.bind.annotation.*;
import java.util.List;
import java.util.Set;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 18.11.11 14:07
 */
@XmlRootElement(name = "zdoc")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class FieldCode extends AbstractDictionary{
    @XmlRootElement(name = "root")
    @XmlSeeAlso(FieldCode.class)
    public final static class RS extends RowSet<FieldCode>{}

    public final static String COUNTERPART_SPR_NAME = "spr_contragents";
    public final static String EMPLOYEE_SPR_NAME = "spr_works";
    
    public final static Set<String> IMPLEMENTED = Sets.newHashSet(COUNTERPART_SPR_NAME, EMPLOYEE_SPR_NAME);

    @XmlElementWrapper(name = "codes")
    @XmlElement(name = "code")
    private List<String> codes;

    @XmlElement(name = "field")
    private List<Field> fields;

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
