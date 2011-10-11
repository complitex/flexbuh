package org.complitex.flexbuh.entity.user;

import org.complitex.flexbuh.entity.LocalizedDomainObject;

/**
 * @author Pavel Sknar
 *         Date: 31.08.11 18:10
 */
public class PersonType extends LocalizedDomainObject {
    private Integer code;

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}
}
