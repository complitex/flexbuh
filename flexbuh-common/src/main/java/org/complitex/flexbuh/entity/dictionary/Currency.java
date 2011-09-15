package org.complitex.flexbuh.entity.dictionary;

import com.google.common.collect.Lists;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 05.08.11 10:57
 */
public class Currency extends AbstractPeriodDictionary {
	private Integer codeNumber;
	private String codeString;
	private List<CurrencyName> names = Lists.newArrayList();

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
	public boolean validate() {
		return super.validate() && codeNumber != null && codeString != null && names.size() > 0 &&
				getBeginDate() != null && getEndDate() != null;

	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).
				append("id", getId()).
				append("codeNumber", codeNumber).
				append("codeString", codeString).
				append("names", names).
				append("beginDate", getBeginDate()).
				append("endDate", getEndDate()).
				append("uploadDate", getUploadDate()).
				append("status", getStatus()).toString();
	}
}
