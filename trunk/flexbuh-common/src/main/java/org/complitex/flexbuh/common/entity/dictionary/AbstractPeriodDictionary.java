package org.complitex.flexbuh.common.entity.dictionary;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 08.08.11 15:21
 */
@XmlType
@XmlAccessorType(value = XmlAccessType.FIELD)
public abstract class AbstractPeriodDictionary extends AbstractDictionary {
    @XmlElement(name = "D_BEGIN")
	private Date beginDate;

    @XmlElement(name = "D_END")
	private Date endDate;

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public boolean validate() {
		return super.validate() && (beginDate == null || endDate == null || beginDate.before(endDate));
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
