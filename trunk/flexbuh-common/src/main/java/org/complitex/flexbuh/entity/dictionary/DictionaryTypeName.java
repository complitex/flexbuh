package org.complitex.flexbuh.entity.dictionary;

import org.complitex.flexbuh.entity.LocalizedString;

/**
 * @author Pavel Sknar
 *         Date: 09.08.11 11:01
 */
public class DictionaryTypeName extends LocalizedString {
	private static final String TABLE = "dictionary_type_name";

	@Override
	public String getTable() {
		return TABLE;
	}
}
