package org.complitex.flexbuh.entity.dictionary;

import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 05.08.11 12:24
 */
public class Region extends AbstractPeriodDictionary {
	private Integer code;
	private List<RegionName> names;

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public List<RegionName> getNames() {
		return names;
	}

	public void setNames(List<RegionName> names) {
		this.names = names;
	}

	@Override
	public boolean validate() {
		return super.validate() && code != null && names != null && names.size() > 0;
	}
}
