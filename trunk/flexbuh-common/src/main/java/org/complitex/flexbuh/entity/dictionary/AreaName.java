package org.complitex.flexbuh.entity.dictionary;

import org.complitex.flexbuh.entity.LocalizedString;

/**
 * @author Pavel Sknar
 *         Date: 05.08.11 13:59
 */
public class AreaName extends LocalizedString {
	private final static String TABLE = "area_name";

	@Override
	public String getTable() {
		return TABLE;
	}
}
