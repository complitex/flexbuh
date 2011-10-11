package org.complitex.flexbuh.entity.dictionary;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.complitex.flexbuh.entity.LocalizedDomainObject;

import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 04.08.11 21:54
 */
public abstract class AbstractDictionary extends LocalizedDomainObject {
	private Date uploadDate;

	public Date getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}

	public boolean validate() {
		return uploadDate != null;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
