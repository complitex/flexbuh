package org.complitex.flexbuh.common.entity.dictionary;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Locale;

/**
 * @author Pavel Sknar
 *         Date: 05.08.11 13:06
 */
public class TaxInspection extends AbstractPeriodDictionary {
	private Integer cSti;
	private Integer cReg;
	private Integer cRaj;
	private Integer tSti;
    
    private String nameRajRu;
    private String nameRajUk;
    
    public String getAreaName(Locale locale){
        switch (locale.getLanguage()){
            case "ru":
                return nameRajRu != null ? nameRajRu : nameRajUk;
            default:
                return nameRajUk;
        }
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
