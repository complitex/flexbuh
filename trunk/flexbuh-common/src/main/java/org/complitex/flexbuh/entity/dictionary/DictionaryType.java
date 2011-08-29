package org.complitex.flexbuh.entity.dictionary;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.complitex.flexbuh.entity.DomainObject;

import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 05.08.11 12:19
 */
public class DictionaryType extends DomainObject {
	public static final String TABLE = "dictionary_type";

	private String code;
	private List<DictionaryTypeName> names;
	private List<String> fileNames;

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

	public String getDefaultName() {
		if (names == null) {
			return null;
		}
		for (DictionaryTypeName name : names) {
			if (name.getLanguage().isDefaultLang()) {
				return name.getValue();
			}
		}
		return null;
	}

	public List<String> getFileNames() {
		return fileNames;
	}

	public void setFileNames(List<String> fileNames) {
		this.fileNames = fileNames;
	}

	@Override
	public String getTable() {
		return TABLE;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).
				append("id", getId()).
				append("code", code).
				append("names", names).
				append("fileNames", fileNames).toString();
	}
}
