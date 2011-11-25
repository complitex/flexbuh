package org.complitex.flexbuh.common.entity.dictionary;

import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 24.11.11 10:24
 */
public class DocumentTermFilter extends PeriodDictionaryFilter {

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
}
