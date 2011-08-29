package org.complitex.flexbuh.entity.dictionary;

import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 08.08.11 15:21
 */
public abstract class DictionaryOfLimitedTime extends Dictionary {
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

	@Override
	public boolean validate() {
		return super.validate() && (beginDate == null || endDate == null || beginDate.before(endDate));
	}
}
