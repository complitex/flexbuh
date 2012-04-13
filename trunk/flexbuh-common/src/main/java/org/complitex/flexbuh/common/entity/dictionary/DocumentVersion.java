package org.complitex.flexbuh.common.entity.dictionary;

import org.complitex.flexbuh.common.entity.RowSet;

import javax.xml.bind.annotation.*;

/**
 * @author Pavel Sknar
 *         Date: 08.08.11 14:52
 */
@XmlRootElement(name = "ROW")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class DocumentVersion extends AbstractPeriodDictionary {
    @XmlRootElement(name = "ROWSET")
    @XmlSeeAlso(DocumentVersion.class)
    public static final class RS extends RowSet<DocumentVersion>{}

    @XmlElement(name = "C_DOC")
	private String cDoc;

    @XmlElement(name = "C_DOC_SUB")
	private String cDocSub;

    @XmlElement(name = "C_DOC_VER")
	private Integer cDocVer;

    @Override
    @XmlElement(name = "NORM_DOC")
    public String getNameUk() {
        return super.getNameUk();
    }

    @Override
    public void setNameUk(String nameUk) {
        super.setNameUk(nameUk);
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
