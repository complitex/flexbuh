package org.complitex.flexbuh.entity;

import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.Locale;

/**
 * @author Pavel Sknar
 *         Date: 05.08.11 11:21
 */
public class Language extends DomainObject {
	public static final String TABLE = "language";

	private String langIsoCode;

	public String getLangIsoCode() {
		return langIsoCode;
	}

	public void setLangIsoCode(String langIsoCode) {
		this.langIsoCode = langIsoCode;
	}

	public Locale getLocale() {
		return LocaleUtils.toLocale(langIsoCode);
	}

	@Override
	public String getTable() {
		return TABLE;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof Language)) {
			return false;
		}

		Language that = (Language) o;
		return new EqualsBuilder()
				.append(langIsoCode, that.getLangIsoCode())
				.isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder()
				.append(langIsoCode)
				.toHashCode();
	}
}
