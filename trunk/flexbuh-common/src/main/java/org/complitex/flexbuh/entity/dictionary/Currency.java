package org.complitex.flexbuh.entity.dictionary;

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
		return super.validate() && codeNumber != null && codeString != null &&
                getBeginDate() != null && getEndDate() != null;

	}

    @Override
    public String toString() {
        return "Currency{" +
                "codeNumber=" + codeNumber +
                ", codeString='" + codeString + '\'' +
                '}';
    }
}
