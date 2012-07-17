package org.complitex.flexbuh.common.entity.dictionary;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.complitex.flexbuh.common.entity.RowSet;
import org.complitex.flexbuh.common.util.DateUtil;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import java.util.*;

/**
 * @author Pavel Sknar
 *         Date: 05.08.11 15:43
 */
@XmlRootElement(name = "ROW")
@XmlAccessorType(XmlAccessType.FIELD)
public class Document extends AbstractPeriodDictionary {
    @XmlRootElement(name = "ROWSET")
    @XmlSeeAlso(Document.class)
    public final static class RS extends RowSet<Document> {}

    @NotNull
    @XmlElement(name = "C_DOC")
	private String cDoc;

    @NotNull
    @XmlElement(name = "C_DOC_SUB")
	private String cDocSub;

    @NotNull
    @XmlElement(name = "C_DOC_CNT_SET")
	private Boolean cntSet; // Может подаваться в отчетном периоде более одного раза

    @XmlElement(name = "PARENT_C_DOC")
	private String parentCDoc;

    @XmlElement(name = "PARENT_C_DOC_SUB")
	private String parentCDocSub;

    @XmlTransient
    private List<DocumentVersion> documentVersions;

    public Document() {
    }

    public String getFullName(Locale locale, int periodYear, int periodMonth){
        return cDoc + cDocSub + String.format("%02d", getVersion(periodYear, periodMonth)) + " " + getName(locale);
    }

    public Integer getVersion(int periodYear, int periodMonth){    
        Date date = DateUtil.getFirstDayOfMonth(periodYear, periodMonth - 1);

        Collections.sort(documentVersions, new Comparator<DocumentVersion>() {
            @Override
            public int compare(DocumentVersion o1, DocumentVersion o2) {
                return o2.getCDocVer().compareTo(o1.getCDocVer());
            }
        });

        for (DocumentVersion dv : documentVersions){
            if (dv.getBeginDate().before(date) && (dv.getEndDate() == null || dv.getEndDate().after(date))){
                return dv.getCDocVer();
            }
        }

        return null;
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

    public List<DocumentVersion> getDocumentVersions() {
        return documentVersions;
    }

    public void setDocumentVersions(List<DocumentVersion> documentVersions) {
        this.documentVersions = documentVersions;
    }

    @Override
	public boolean validate() {
		return  super.validate() && cDoc != null && cDocSub != null && cntSet != null;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
