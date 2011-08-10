package org.complitex.flexbuh.entity.dictionary;

import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 08.08.11 14:52
 */
public class DocumentVersion extends DictionaryOfLimitedTime {
	private static final String TABLE = "document_version";

	private DocumentType documentType;
	private DocumentSubType documentSubType;
	private Integer version;
	private List<NormativeDocumentName> normativeDocumentNames;

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

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public List<NormativeDocumentName> getNormativeDocumentNames() {
		return normativeDocumentNames;
	}

	public void setNormativeDocumentNames(List<NormativeDocumentName> normativeDocumentNames) {
		this.normativeDocumentNames = normativeDocumentNames;
	}

	@Override
	public String getTable() {
		return TABLE;
	}
}
