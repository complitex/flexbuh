package org.complitex.flexbuh.entity.dictionary;

import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 05.08.11 15:43
 */
public class Document extends DictionaryOfLimitedTime {
	public static final String TABLE = "document";

	private String type;
	private String subType;
	private List<DocumentName> names;

	/**
	 * Чи може подаватись у звітному періоді більше одного разу
	 */
	private Boolean cntSet;
	private String parentDocumentType;
	private String parentDocumentSubType;
	private Boolean selected;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}

	public List<DocumentName> getNames() {
		return names;
	}

	public void setNames(List<DocumentName> names) {
		this.names = names;
	}

	public Boolean getCntSet() {
		return cntSet;
	}

	public void setCntSet(Boolean cntSet) {
		this.cntSet = cntSet;
	}

	public String getParentDocumentType() {
		return parentDocumentType;
	}

	public void setParentDocumentType(String parentDocumentType) {
		this.parentDocumentType = parentDocumentType;
	}

	public String getParentDocumentSubType() {
		return parentDocumentSubType;
	}

	public void setParentDocumentSubType(String parentDocumentSubType) {
		this.parentDocumentSubType = parentDocumentSubType;
	}

	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}

	@Override
	public boolean validate() {
		return  super.validate() &&
				type != null && subType != null && names != null && names.size() > 0 && cntSet != null &&
				selected != null;
	}

	@Override
	public String getTable() {
		return TABLE;
	}
}
