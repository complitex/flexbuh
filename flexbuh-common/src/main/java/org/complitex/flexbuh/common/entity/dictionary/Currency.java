package org.complitex.flexbuh.common.entity.dictionary;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author Pavel Sknar
 *         Date: 05.08.11 10:57
 */
public class Currency extends AbstractPeriodDictionary {
	private Integer codeNumber;
	private String codeString;

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
