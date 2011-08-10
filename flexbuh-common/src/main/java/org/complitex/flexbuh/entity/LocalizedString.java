package org.complitex.flexbuh.entity;

/**
 * @author Pavel Sknar
 *         Date: 05.08.11 11:47
 */
public abstract class LocalizedString extends DomainObject {
	private String value;
	private Language language;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}
}
