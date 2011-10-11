package org.complitex.flexbuh.entity.dictionary;

/**
 * @author Pavel Sknar
 *         Date: 08.08.11 14:52
 */
public class DocumentVersion extends AbstractPeriodDictionary {
	private String cDoc;
	private String cDocSub;
	private Integer cDocVer;

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

	@Override
	public boolean validate() {
		return super.validate() && cDoc != null && cDocSub != null && cDocVer != null;
	}
}
