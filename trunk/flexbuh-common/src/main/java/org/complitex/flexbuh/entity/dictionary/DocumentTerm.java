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
	private String cDoc;
	private String cDocSub;
	private Integer cDocVer;
	private Date dateTerm;
	private Integer periodMonth;
	private Integer periodType;
	private Integer periodYear;

	public String getCDoc() {
		return cDoc;
	}

	public void setCDoc(String cDoc) {
		this.cDoc = cDoc;
	}

	public String getCDocSub() {
		return cDocSub;
	}

	public void setCDocSub(String cDocSub) {
		this.cDocSub = cDocSub;
	}

	public Integer getCDocVer() {
		return cDocVer;
	}

	public void setCDocVer(Integer cDocVer) {
		this.cDocVer = cDocVer;
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
				StringUtils.isNotEmpty(cDoc)
				&& StringUtils.isNotEmpty(cDocSub)
				&& cDocVer != null && cDocVer > 0
				&& dateTerm != null
				&& periodMonth != null && periodMonth > 0
				&& periodType != null && periodType > 0
				&& periodYear != null && periodYear > 0;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).
				append("documentType", cDoc).
				append("documentSubType", cDocSub).
				append("documentVersion", cDocVer).
				append("dateTerm", dateTerm).
				append("periodMonth", periodMonth).
				append("periodType", periodType).
				append("periodYear", periodYear).
				append("beginDate", getBeginDate()).
				append("endDate", getEndDate()).
				append("uploadDate", getUploadDate())
                .toString();
	}
}
