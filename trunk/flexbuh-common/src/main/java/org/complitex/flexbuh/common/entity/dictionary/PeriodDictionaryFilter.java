package org.complitex.flexbuh.common.entity.dictionary;

import org.complitex.flexbuh.common.entity.AbstractFilter;

import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 22.11.11 13:21
 */
public class PeriodDictionaryFilter extends AbstractFilter {

	private Date beginDate;
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
}
