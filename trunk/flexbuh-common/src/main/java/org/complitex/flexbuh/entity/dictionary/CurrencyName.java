package org.complitex.flexbuh.entity.dictionary;

import org.complitex.flexbuh.entity.LocalizedString;

/**
 * @author Pavel Sknar
 *         Date: 05.08.11 11:56
 */
public class CurrencyName extends LocalizedString {
	private static final String TABLE = "currency_name";

	@Override
	public String getTable() {
		return TABLE;
	}
}
