package org.complitex.flexbuh.common.entity.dictionary;

/**
 * @author Pavel Sknar
 *         Date: 24.11.11 17:37
 */
public class RegionFilter extends PeriodDictionaryFilter {

	private Integer code;
	private String nameUk;

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getNameUk() {
		return nameUk;
	}

	public void setNameUk(String nameUk) {
		this.nameUk = nameUk;
	}
}
