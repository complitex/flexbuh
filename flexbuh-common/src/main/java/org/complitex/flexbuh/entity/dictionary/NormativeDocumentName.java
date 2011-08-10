package org.complitex.flexbuh.entity.dictionary;

import org.complitex.flexbuh.entity.LocalizedString;

/**
 * @author Pavel Sknar
 *         Date: 10.08.11 14:41
 */
public class NormativeDocumentName extends LocalizedString {
	private static final String TABLE = "normative_document_name";

	@Override
	public String getTable() {
		return TABLE;
	}
}
