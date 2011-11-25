package org.complitex.flexbuh.common.entity.dictionary;

/**
 * @author Pavel Sknar
 *         Date: 22.11.11 13:06
 */
public class CurrencyFilter extends PeriodDictionaryFilter {

	private String nameUk;
    private String nameRu;
	private Integer codeNumber;
	private String codeString;

	public String getNameUk() {
		return nameUk;
	}

	public void setNameUk(String nameUk) {
		this.nameUk = nameUk;
	}

	public String getNameRu() {
		return nameRu;
	}

	public void setNameRu(String nameRu) {
		this.nameRu = nameRu;
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
}
