package org.complitex.flexbuh.entity.dictionary;

import org.complitex.flexbuh.entity.LocalizedString;

/**
 * @author Pavel Sknar
 *         Date: 05.08.11 15:42
 */
public class DocumentName extends LocalizedString {
	private static final String TABLE = "document_name";

	@Override
	public String getTable() {
		return TABLE;
	}
}
