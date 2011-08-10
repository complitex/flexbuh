package org.complitex.flexbuh.entity.dictionary;

import org.complitex.flexbuh.entity.DomainObject;

/**
 * @author Pavel Sknar
 *         Date: 04.08.11 21:54
 */
public abstract class Dictionary extends DomainObject {
	private DictionaryType dictionaryType;

	public DictionaryType getDictionaryType() {
		return dictionaryType;
	}

	public void setDictionaryType(DictionaryType dictionaryType) {
		this.dictionaryType = dictionaryType;
	}
}
