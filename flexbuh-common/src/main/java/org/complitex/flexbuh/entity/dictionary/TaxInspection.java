package org.complitex.flexbuh.entity.dictionary;

import java.util.List;
import java.util.Locale;

/**
 * @author Pavel Sknar
 *         Date: 05.08.11 13:06
 */
public class TaxInspection extends DictionaryOfLimitedTime {
	public static final String TABLE = "tax_inspection";

	private Integer code;
	private List<TaxInspectionName> names;
	private Integer regionCode;
	private Integer codeArea;
	private List<AreaName> areaNames;
	private Integer codeTaxInspectionType;

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public List<TaxInspectionName> getNames() {
		return names;
	}

	public void setNames(List<TaxInspectionName> names) {
		this.names = names;
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

	public List<AreaName> getAreaNames() {
		return areaNames;
	}

	public void setAreaNames(List<AreaName> areaNames) {
		this.areaNames = areaNames;
	}

	public Integer getCodeTaxInspectionType() {
		return codeTaxInspectionType;
	}

	public void setCodeTaxInspectionType(Integer codeTaxInspectionType) {
		this.codeTaxInspectionType = codeTaxInspectionType;
	}

	public String getName(Locale locale) {
		if (names == null) {
			return null;
		}
		for (TaxInspectionName name : names) {
			if (name.getLanguage().getLocale().equals(locale)) {
				return name.getValue();
			}
		}
		return getDefaultName();
	}

	public String getDefaultName() {
		if (names == null) {
			return null;
		}
		for (TaxInspectionName name : names) {
			if (name.getLanguage().isDefaultLang()) {
				return name.getValue();
			}
		}
		return null;
	}

	@Override
	public String getTable() {
		return TABLE;
	}

	@Override
	public boolean validate() {
		return super.validate() &&
				code != null && names != null && names.size() > 0 && regionCode != null &&
				codeArea != null && areaNames != null && areaNames.size() > 0 && codeTaxInspectionType != null;
	}
}
