package org.complitex.flexbuh.entity.dictionary;

import org.complitex.flexbuh.entity.DomainObject;

import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 05.08.11 12:19
 */
public class DictionaryType extends DomainObject {
	private static final String TABLE = "dictionary_type";

	private String code;
	private List<DictionaryTypeName> names;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<DictionaryTypeName> getNames() {
		return names;
	}

	public void setNames(List<DictionaryTypeName> names) {
		this.names = names;
	}

	@Override
	public String getTable() {
		return TABLE;
	}
}
