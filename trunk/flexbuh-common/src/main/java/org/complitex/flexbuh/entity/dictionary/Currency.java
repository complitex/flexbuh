package org.complitex.flexbuh.entity.dictionary;

import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 05.08.11 10:57
 */
public class Currency extends DictionaryOfLimitedTime {
	private static final String TABLE = "currency";

	private Integer codeNumber;
	private String codeString;
	private List<CurrencyName> names;

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

	public List<CurrencyName> getNames() {
		return names;
	}

	public void setNames(List<CurrencyName> names) {
		this.names = names;
	}

	@Override
	public String getTable() {
		return TABLE;
	}
}
