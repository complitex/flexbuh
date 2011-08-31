package org.complitex.flexbuh.entity.user;

import org.complitex.flexbuh.entity.LocalizedString;

/**
 * @author Pavel Sknar
 *         Date: 31.08.11 18:13
 */
public class PersonTypeName extends LocalizedString {
	private static final String TABLE = "person_type_name";

	@Override
	public String getTable() {
		return TABLE;
	}
}
