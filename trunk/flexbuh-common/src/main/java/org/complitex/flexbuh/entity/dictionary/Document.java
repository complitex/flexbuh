package org.complitex.flexbuh.entity.dictionary;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 05.08.11 15:43
 */
public class Document extends AbstractPeriodDictionary {
	private String cDoc;
	private String cDocSub;
	private List<DocumentName> names;

	// Может подаваться в отчетном периоде более одного раза
	private Boolean cntSet;
	private String parentCDoc;
	private String parentCDocSub;
	private Boolean selected = false;

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

	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}

	@Override
	public boolean validate() {
		return  super.validate() &&
				cDoc != null && cDocSub != null && names != null && names.size() > 0 && cntSet != null && selected != null;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
