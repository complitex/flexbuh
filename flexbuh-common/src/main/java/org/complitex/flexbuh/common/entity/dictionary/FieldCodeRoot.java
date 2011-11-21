package org.complitex.flexbuh.common.entity.dictionary;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 18.11.11 14:51
 */
@XmlRootElement(name = "root")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class FieldCodeRoot {
    @XmlElement(name = "zdoc")
    private List<FieldCode> fieldCodes;

    public List<FieldCode> getFieldCodes() {
        return fieldCodes;
    }

    public void setFieldCodes(List<FieldCode> fieldCodes) {
        this.fieldCodes = fieldCodes;
    }

    @Override
    public String toString() {
        return "FieldCodeRoot{" +
                "fieldCodes=" + fieldCodes +
                '}';
    }
}
