package org.complitex.flexbuh.entity.user;

import org.complitex.flexbuh.entity.DomainObject;

import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 31.08.11 18:10
 */
public class PersonType extends DomainObject {
	private static final String TABLE = "person_type";

	private Integer code;

	private List<PersonTypeName> names;

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public List<PersonTypeName> getNames() {
		return names;
	}

	public void setNames(List<PersonTypeName> names) {
		this.names = names;
	}

	public String getDefaultName() {
		if (names == null) {
			return null;
		}
		for (PersonTypeName name : names) {
			if (name.getLanguage().isDefaultLang()) {
				return name.getValue();
			}
		}
		return null;
	}

	@Override
	public String getTable() {
		return TABLE;
	}
}
