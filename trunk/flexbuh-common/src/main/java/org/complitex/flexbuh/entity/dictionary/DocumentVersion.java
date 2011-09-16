package org.complitex.flexbuh.entity.dictionary;

import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 08.08.11 14:52
 */
public class DocumentVersion extends AbstractPeriodDictionary {
	private String cDoc;
	private String cDocSub;
	private Integer cDocVer;
	private List<NormativeDocumentName> normativeDocumentNames;

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

	public List<NormativeDocumentName> getNormativeDocumentNames() {
		return normativeDocumentNames;
	}

	public void setNormativeDocumentNames(List<NormativeDocumentName> normativeDocumentNames) {
		this.normativeDocumentNames = normativeDocumentNames;
	}

	@Override
	public boolean validate() {
		return super.validate() && cDoc != null && cDocSub != null && cDocVer != null &&
				normativeDocumentNames != null && normativeDocumentNames.size() > 0;
	}
}
