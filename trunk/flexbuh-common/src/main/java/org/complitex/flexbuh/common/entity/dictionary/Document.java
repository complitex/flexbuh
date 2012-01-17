package org.complitex.flexbuh.common.entity.dictionary;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;
import java.util.Locale;

/**
 * @author Pavel Sknar
 *         Date: 05.08.11 15:43
 */
public class Document extends AbstractPeriodDictionary {
	private String cDoc;
	private String cDocSub;

	private Boolean cntSet; // Может подаваться в отчетном периоде более одного раза
	private String parentCDoc;
	private String parentCDocSub;
	private Boolean selected = false;

    private List<DocumentVersion> documentVersions;

    public Document() {
    }

    public Document(String cDoc, String cDocSub) {
        this.cDoc = cDoc;
        this.cDocSub = cDocSub;
    }
    
    public String getFullName(Locale locale){
        return documentVersions.get(0) != null
                ? cDoc + cDocSub + String.format("%02d", documentVersions.get(0).getCDocVer()) + " " + getName(locale)
                : null;
    }

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

	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}

    public List<DocumentVersion> getDocumentVersions() {
        return documentVersions;
    }

    public void setDocumentVersions(List<DocumentVersion> documentVersions) {
        this.documentVersions = documentVersions;
    }

    @Override
	public boolean validate() {
		return  super.validate() && cDoc != null && cDocSub != null && cntSet != null && selected != null;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
