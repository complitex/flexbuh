package org.complitex.flexbuh.util;

import org.complitex.flexbuh.entity.LocalizedString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Collection;
import java.util.Locale;

/**
 * @author Pavel Sknar
 *         Date: 05.08.11 14:58
 */
public abstract class LocalizedStringUtil {

	/**
	 * Get localized string with required locale
	 *
	 * @param collection Localized strings in collection
	 * @param locale Locale
	 * @return found localized string, else <code>null</code>
	 */
	@Null
	public static <T extends LocalizedString> T getLocalizedString(@NotNull Collection<T> collection,
																   @NotNull Locale locale) {
		for (T string : collection) {
			if (string.getLanguage() != null && locale.equals(string.getLanguage().getLocale())) {
				return string;
			}
		}
		return null;
	}
}
