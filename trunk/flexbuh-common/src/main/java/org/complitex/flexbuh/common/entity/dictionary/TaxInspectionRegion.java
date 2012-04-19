package org.complitex.flexbuh.common.entity.dictionary;

import org.complitex.flexbuh.common.entity.RowSet;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
* @author Anatoly A. Ivanov java@inheaven.ru
*         Date: 17.04.12 19:04
*/
@XmlRootElement(name = "ROW")
@XmlAccessorType(XmlAccessType.FIELD)
public class TaxInspectionRegion extends AbstractDictionary{
    @XmlRootElement(name = "ROWSET")
    @XmlSeeAlso(TaxInspectionRegion.class)
    public final static class RS extends RowSet<TaxInspectionRegion> {}

    @XmlElement(name = "C_REG")
    private Integer cReg;

    @XmlElementWrapper(name = "SET_STI")
    @XmlElement(name = "ROW_STI")
    private List<TaxInspection> taxInspections;

    @XmlElement(name = "NAME_REG")
    @Override
    public String getNameUk() {
        return super.getNameUk();
    }

    @Override
    public void setNameUk(String nameUk) {
        super.setNameUk(nameUk);
    }

    public Integer getCReg() {
        return cReg;
    }

    public void setCReg(Integer cReg) {
        this.cReg = cReg;
    }

    public List<TaxInspection> getTaxInspections() {
        return taxInspections;
    }

    public void setTaxInspections(List<TaxInspection> taxInspections) {
        this.taxInspections = taxInspections;
    }
}
