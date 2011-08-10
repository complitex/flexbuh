package org.complitex.flexbuh.entity.dictionary;

import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 05.08.11 12:24
 */
public class Region extends DictionaryOfLimitedTime {
	private static final String TABLE = "region";

	private Integer code;
	private List<RegionName> regionNames;

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public List<RegionName> getRegionNames() {
		return regionNames;
	}

	public void setRegionNames(List<RegionName> regionNames) {
		this.regionNames = regionNames;
	}

	@Override
	public String getTable() {
		return TABLE;
	}
}
