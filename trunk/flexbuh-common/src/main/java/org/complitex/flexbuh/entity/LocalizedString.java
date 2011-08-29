package org.complitex.flexbuh.entity;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

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

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).
				append("id", getId()).
				append("language", language).
				append("value", value).toString();
	}
}
