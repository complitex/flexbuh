package org.complitex.flexbuh.common.entity.dictionary;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.complitex.flexbuh.common.annotation.Display;

import javax.xml.bind.annotation.*;

/**
 * @author Pavel Sknar
 *         Date: 05.08.11 13:06
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TaxInspection extends AbstractPeriodDictionary {
    @XmlElement(name = "C_STI")
	private Integer cSti;

    @XmlElement(name = "C_REG")
	private Integer cReg;

    @XmlElement(name = "C_RAJ")
	private Integer cRaj;

    @XmlElement(name = "T_STI")
	private Integer tSti;

    @XmlTransient
    @Display(visible = false)
    private String nameRajRu;

    @XmlElement(name = "NAME_RAJ")
    private String nameRajUk;

    @XmlElement(name = "NAME_STI")
    @Override
    public String getNameUk() {
        return super.getNameUk();
    }

    @Override
    public void setNameUk(String nameUk) {
        super.setNameUk(nameUk);
    }

    public Integer getCSti() {
        return cSti;
    }

    public void setCSti(Integer cSti) {
        this.cSti = cSti;
    }

    public Integer getCReg() {
        return cReg;
    }

    public void setCReg(Integer cReg) {
        this.cReg = cReg;
    }

    public Integer getCRaj() {
        return cRaj;
    }

    public void setCRaj(Integer cRaj) {
        this.cRaj = cRaj;
    }

    public Integer getTSti() {
        return tSti;
    }

    public void setTSti(Integer tSti) {
        this.tSti = tSti;
    }

    public String getNameRajRu() {
        return nameRajRu;
    }

    public void setNameRajRu(String nameRajRu) {
        this.nameRajRu = nameRajRu;
    }

    public String getNameRajUk() {
        return nameRajUk;
    }

    public void setNameRajUk(String nameRajUk) {
        this.nameRajUk = nameRajUk;
    }

    @Override
	public boolean validate() {
		return super.validate() && cSti != null && cReg != null && cRaj != null
                && tSti != null;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(cSti).append(cRaj).hashCode();
	}
}
