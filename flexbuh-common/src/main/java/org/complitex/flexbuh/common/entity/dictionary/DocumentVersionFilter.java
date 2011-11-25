package org.complitex.flexbuh.common.entity.dictionary;

/**
 * @author Pavel Sknar
 *         Date: 24.11.11 13:09
 */
public class DocumentVersionFilter extends PeriodDictionaryFilter {

	private String cDoc;
	private String cDocSub;
	private Integer cDocVer;
	private String nameUk;

	public String getcDoc() {
		return cDoc;
	}

	public void setcDoc(String cDoc) {
		this.cDoc = cDoc;
	}

	public String getcDocSub() {
		return cDocSub;
	}

	public void setcDocSub(String cDocSub) {
		this.cDocSub = cDocSub;
	}

	public Integer getcDocVer() {
		return cDocVer;
	}

	public void setcDocVer(Integer cDocVer) {
		this.cDocVer = cDocVer;
	}

	public String getNameUk() {
		return nameUk;
	}

	public void setNameUk(String nameUk) {
		this.nameUk = nameUk;
	}
}
