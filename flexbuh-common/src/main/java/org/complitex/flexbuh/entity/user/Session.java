package org.complitex.flexbuh.entity.user;

import org.complitex.flexbuh.entity.DomainObject;

/**
 * @author Pavel Sknar
 *         Date: 31.08.11 14:27
 */
public class Session extends DomainObject {
	private static final String TABLE = "user";

	private String cookie;

	@Override
	public String getTable() {
		return TABLE;
	}
}
