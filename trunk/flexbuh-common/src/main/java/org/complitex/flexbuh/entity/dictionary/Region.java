package org.complitex.flexbuh.entity.dictionary;

/**
 * @author Pavel Sknar
 *         Date: 05.08.11 12:24
 */
public class Region extends AbstractPeriodDictionary {
	private Integer code;

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	@Override
	public boolean validate() {
		return super.validate() && code != null;
	}
}
