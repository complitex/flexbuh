package org.complitex.flexbuh.entity.dictionary;

import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 05.08.11 17:54
 */
public class DocumentTerm extends Dictionary {
	private static final String TABLE = "document_term";

	private DocumentType documentType;
	private DocumentSubType documentSubType;
	private Integer documentVersion;
	private Date dataTerm;
	private Integer periodMonth;
	private Integer periodType;
	private Integer periodYear;

	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

	public DocumentSubType getDocumentSubType() {
		return documentSubType;
	}

	public void setDocumentSubType(DocumentSubType documentSubType) {
		this.documentSubType = documentSubType;
	}

	public Integer getDocumentVersion() {
		return documentVersion;
	}

	public void setDocumentVersion(Integer documentVersion) {
		this.documentVersion = documentVersion;
	}

	public Date getDataTerm() {
		return dataTerm;
	}

	public void setDataTerm(Date dataTerm) {
		this.dataTerm = dataTerm;
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
	public String getTable() {
		return TABLE;
	}
}
