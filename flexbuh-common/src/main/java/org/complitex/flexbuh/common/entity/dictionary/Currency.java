package org.complitex.flexbuh.common.entity.dictionary;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.complitex.flexbuh.common.entity.RowSet;

import javax.xml.bind.annotation.*;

/**
 * @author Pavel Sknar
 *         Date: 05.08.11 10:57
 */
@XmlRootElement(name = "row")
@XmlAccessorType(XmlAccessType.FIELD)
public class Currency extends AbstractPeriodDictionary {
    @XmlRootElement(name = "rowset")
    @XmlSeeAlso(Currency.class)
    public static final class RS extends RowSet<Currency> {}

    @XmlElement(name = "C_CURRN_N")
    private Integer codeNumber;

    @XmlElement(name = "C_CURRN_C")
    private String codeString;

    @XmlElement(name = "NAME_CUR")
    @Override
    public String getNameUk() {
        return super.getNameUk();
    }

    @Override
    public void setNameUk(String nameUk) {
        super.setNameUk(nameUk);
    }

    @XmlElement(name = "NAME_RUS")
    @Override
    public String getNameRu() {
        return super.getNameRu();
    }

    @Override
    public void setNameRu(String nameRu) {
        super.setNameRu(nameRu);
    }

    public Integer getCodeNumber() {
        return codeNumber;
    }

    public void setCodeNumber(Integer codeNumber) {
        this.codeNumber = codeNumber;
    }

    public String getCodeString() {
        return codeString;
    }

    public void setCodeString(String codeString) {
        this.codeString = codeString;
    }

    @Override
    public boolean validate() {
        return super.validate() && codeNumber != null && codeString != null;

    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return codeNumber;
    }
}
