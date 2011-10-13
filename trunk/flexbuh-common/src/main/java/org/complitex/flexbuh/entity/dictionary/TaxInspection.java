package org.complitex.flexbuh.entity.dictionary;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Locale;

/**
 * @author Pavel Sknar
 *         Date: 05.08.11 13:06
 */
public class TaxInspection extends AbstractPeriodDictionary {
	private Integer code;	
	private Integer regionCode;
	private Integer codeArea;	
	private Integer codeTaxInspectionType;
    
    private String areaNameRu;
    private String areaNameUk;
    
    public String getAreaName(Locale locale){
        switch (locale.getLanguage()){
            case "ru":
                return areaNameRu != null ? areaNameRu : areaNameUk;
            default:
                return areaNameUk;
        }
    }

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

    public String getAreaNameRu() {
        return areaNameRu;
    }

    public void setAreaNameRu(String areaNameRu) {
        this.areaNameRu = areaNameRu;
    }

    public String getAreaNameUk() {
        return areaNameUk;
    }

    public void setAreaNameUk(String areaNameUk) {
        this.areaNameUk = areaNameUk;
    }

    public Integer getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(Integer regionCode) {
		this.regionCode = regionCode;
	}

	public Integer getCodeArea() {
		return codeArea;
	}

	public void setCodeArea(Integer codeArea) {
		this.codeArea = codeArea;
	}

	public Integer getCodeTaxInspectionType() {
		return codeTaxInspectionType;
	}

	public void setCodeTaxInspectionType(Integer codeTaxInspectionType) {
		this.codeTaxInspectionType = codeTaxInspectionType;
	}

	@Override
	public boolean validate() {
		return super.validate() && code != null && regionCode != null && codeArea != null 
                && codeTaxInspectionType != null;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(code).append(codeArea).hashCode();
	}
}
