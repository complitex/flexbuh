package org.complitex.flexbuh.entity.dictionary;

import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 08.08.11 14:52
 */
public class DocumentVersion extends AbstractPeriodDictionary {
	private String documentType;
	private String documentSubType;
	private Integer version;
	private List<NormativeDocumentName> normativeDocumentNames;

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
	public boolean validate() {
		return super.validate() && documentType != null && documentSubType != null && version != null &&
				normativeDocumentNames != null && normativeDocumentNames.size() > 0;
	}
}
