package org.complitex.flexbuh.entity.user;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.complitex.flexbuh.entity.DomainObject;

/**
 * @author Pavel Sknar
 *         Date: 31.08.11 14:27
 */
public class Session extends DomainObject {
	public static final String TABLE = "session";

	private String cookie;

	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	@Override
	public String getTable() {
		return TABLE;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
