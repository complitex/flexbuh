package org.complitex.flexbuh.entity.dictionary;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 05.08.11 17:54
 */
public class DocumentTerm extends AbstractPeriodDictionary {
	private String documentType;
	private String documentSubType;
	private Integer documentVersion;
	private Date dateTerm;
	private Integer periodMonth;
	private Integer periodType;
	private Integer periodYear;

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getDocumentSubType() {
		return documentSubType;
	}

	public void setDocumentSubType(String documentSubType) {
		this.documentSubType = documentSubType;
	}

	public Integer getDocumentVersion() {
		return documentVersion;
	}

	public void setDocumentVersion(Integer documentVersion) {
		this.documentVersion = documentVersion;
	}

	public Date getDateTerm() {
		return dateTerm;
	}

	public void setDateTerm(Date dateTerm) {
		this.dateTerm = dateTerm;
	}

	public Integer getPeriodMonth() {
		return periodMonth;
	}

	public void setPeriodMonth(Integer periodMonth) {
		this.periodMonth = periodMonth;
	}

	public Integer getPeriodType() {
		return periodType;
	}

	public void setPeriodType(Integer periodType) {
		this.periodType = periodType;
	}

	public Integer getPeriodYear() {
		return periodYear;
	}

	public void setPeriodYear(Integer periodYear) {
		this.periodYear = periodYear;
	}

	@Override
	public boolean validate() {
		return super.validate() &&
				StringUtils.isNotEmpty(documentType)
				&& StringUtils.isNotEmpty(documentSubType)
				&& documentVersion != null && documentVersion > 0
				&& dateTerm != null
				&& periodMonth != null && periodMonth > 0
				&& periodType != null && periodType > 0
				&& periodYear != null && periodYear > 0;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).
				append("documentType", documentType).
				append("documentSubType", documentSubType).
				append("documentVersion", documentVersion).
				append("dateTerm", dateTerm).
				append("periodMonth", periodMonth).
				append("periodType", periodType).
				append("periodYear", periodYear).
				append("beginDate", getBeginDate()).
				append("endDate", getEndDate()).
				append("uploadDate", getUploadDate()).
				append("status", getStatus()).toString();
	}
}
