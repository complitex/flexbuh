package org.complitex.flexbuh.common.entity.dictionary;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.complitex.flexbuh.common.entity.RowSet;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 05.08.11 17:54
 */
@XmlRootElement(name = "ROW")
@XmlAccessorType(XmlAccessType.FIELD)
public class DocumentTerm extends AbstractPeriodDictionary {
    @XmlRootElement(name = "ROWSET")
    @XmlSeeAlso(DocumentTerm.class)
    public final static class RS extends RowSet<DocumentTerm>{}

    @NotNull
    @XmlElement(name = "C_DOC")
	private String cDoc;

    @NotNull
    @XmlElement(name = "C_DOC_SUB")
	private String cDocSub;

    @NotNull
    @XmlElement(name = "C_DOC_VER")
	private Integer cDocVer;

    @NotNull
    @XmlJavaTypeAdapter(DateAdapter.class)
    @XmlElement(name = "D_TERM")
	private Date dateTerm;

    @NotNull
    @XmlElement(name = "PERIOD_MONTH")
	private Integer periodMonth;

    @NotNull
    @XmlElement(name = "PERIOD_TYPE")
	private Integer periodType;

    @NotNull
    @XmlElement(name = "PERIOD_YEAR")
	private Integer periodYear;

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

	public Date getDateTerm() {
		return dateTerm;
	}

	public void setDateTerm(Date dateTerm) {
		this.dateTerm = dateTerm;
	}

	public Integer getPeriodMonth() {
		return periodMonth;
	}

	public void setPeriodMonth(Integer periodMonth) {
		this.periodMonth = periodMonth;
	}

	public Integer getPeriodType() {
		return periodType;
	}

	public void setPeriodType(Integer periodType) {
		this.periodType = periodType;
	}

	public Integer getPeriodYear() {
		return periodYear;
	}

	public void setPeriodYear(Integer periodYear) {
		this.periodYear = periodYear;
	}

	@Override
	public boolean validate() {
		return super.validate() &&
				StringUtils.isNotEmpty(cDoc)
				&& StringUtils.isNotEmpty(cDocSub)
				&& cDocVer != null && cDocVer > 0
				&& dateTerm != null
				&& periodMonth != null && periodMonth > 0
				&& periodType != null && periodType > 0
				&& periodYear != null && periodYear > 0;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).
				append("documentType", cDoc).
				append("documentSubType", cDocSub).
				append("documentVersion", cDocVer).
				append("dateTerm", dateTerm).
				append("periodMonth", periodMonth).
				append("periodType", periodType).
				append("periodYear", periodYear).
				append("beginDate", getBeginDate()).
				append("endDate", getEndDate()).
				append("uploadDate", getUploadDate())
                .toString();
	}
}
