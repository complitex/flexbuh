package org.complitex.flexbuh.common.entity.dictionary;

/**
 * @author Pavel Sknar
 *         Date: 23.11.11 10:20
 */
public class DocumentFilter extends PeriodDictionaryFilter {

	private String cDoc;
	private String cDocSub;

	private Boolean cntSet;
	private String parentCDoc;
	private String parentCDocSub;

	private String nameUk;

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

	public Boolean getCntSet() {
		return cntSet;
	}

	public void setCntSet(Boolean cntSet) {
		this.cntSet = cntSet;
	}

	public String getParentCDoc() {
		return parentCDoc;
	}

	public void setParentCDoc(String parentCDoc) {
		this.parentCDoc = parentCDoc;
	}

	public String getParentCDocSub() {
		return parentCDocSub;
	}

	public void setParentCDocSub(String parentCDocSub) {
		this.parentCDocSub = parentCDocSub;
	}

	public String getNameUk() {
		return nameUk;
	}

	public void setNameUk(String nameUk) {
		this.nameUk = nameUk;
	}
}
