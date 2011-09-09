package org.complitex.flexbuh.entity.dictionary;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.complitex.flexbuh.entity.DomainObject;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 04.08.11 21:54
 */
public abstract class Dictionary extends DomainObject {

	public static final Integer ENABLE = 0;
	public static final Integer DISABLE = 1;

	private Integer status = DISABLE;
	private Date uploadDate;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(@NotNull Integer status) {
		if (status != 0 && status != 1) {
			throw new RuntimeException("Failed status: " + status +
					". Required: 0 - disable, 1 - enable");
		}
		this.status = status;
	}

	public void enable() {
		status = ENABLE;
	}

	public void disable() {
		status = DISABLE;
	}

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
