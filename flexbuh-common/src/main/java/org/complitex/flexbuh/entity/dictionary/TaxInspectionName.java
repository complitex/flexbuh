package org.complitex.flexbuh.entity.dictionary;

import org.complitex.flexbuh.entity.LocalizedString;

/**
 * @author Pavel Sknar
 *         Date: 05.08.11 13:57
 */
public class TaxInspectionName extends LocalizedString {
	private static final String TABLE = "tax_inspection_name";

	@Override
	public String getTable() {
		return TABLE;
	}
}
