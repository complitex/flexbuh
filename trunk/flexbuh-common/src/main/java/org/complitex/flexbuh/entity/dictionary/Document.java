package org.complitex.flexbuh.entity.dictionary;

import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 05.08.11 15:43
 */
public class Document extends Dictionary {
	private static final String TABLE = "document";

	private DocumentType type;
	private DocumentSubType subType;
	private List<DocumentName> names;

	/**
	 * Чи може подаватись у звітному періоді більше одного разу
	 */
	private Boolean cntSet;
	private DocumentType parentDocumentType;
	private DocumentSubType parentDocumentSubType;
	private Boolean selected;

	public DocumentType getType() {
		return type;
	}

	public void setType(DocumentType type) {
		this.type = type;
	}

	public DocumentSubType getSubType() {
		return subType;
	}

	public void setSubType(DocumentSubType subType) {
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

	public DocumentType getParentDocumentType() {
		return parentDocumentType;
	}

	public void setParentDocumentType(DocumentType parentDocumentType) {
		this.parentDocumentType = parentDocumentType;
	}

	public DocumentSubType getParentDocumentSubType() {
		return parentDocumentSubType;
	}

	public void setParentDocumentSubType(DocumentSubType parentDocumentSubType) {
		this.parentDocumentSubType = parentDocumentSubType;
	}

	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}

	@Override
	public String getTable() {
		return TABLE;
	}
}
