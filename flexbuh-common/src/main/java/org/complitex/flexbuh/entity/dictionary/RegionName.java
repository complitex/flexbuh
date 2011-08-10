package org.complitex.flexbuh.entity.dictionary;

import org.complitex.flexbuh.entity.LocalizedString;

/**
 * @author Pavel Sknar
 *         Date: 05.08.11 12:27
 */
public class RegionName extends LocalizedString {
	private static final String TABLE = "region_name";

	@Override
	public String getTable() {
		return TABLE;
	}
}
