package org.complitex.flexbuh.common.entity;

import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.Locale;

/**
 * @author Pavel Sknar
 *         Date: 05.08.11 11:21
 */
public class Language extends DomainObject {
	private String langIsoCode;
	private boolean defaultLang;

	public String getLangIsoCode() {
		return langIsoCode;
	}

	public void setLangIsoCode(String langIsoCode) {
		this.langIsoCode = langIsoCode;
	}

	public boolean isDefaultLang() {
		return defaultLang;
	}

	public void setDefaultLang(boolean defaultLang) {
		this.defaultLang = defaultLang;
	}

	public Locale getLocale() {
		return LocaleUtils.toLocale(langIsoCode);
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

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).
				append("id", getId()).
				append("langIsoCode", langIsoCode).
				append("default", defaultLang).toString();

	}
}
