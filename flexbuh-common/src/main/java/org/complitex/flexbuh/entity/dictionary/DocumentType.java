package org.complitex.flexbuh.entity.dictionary;

import org.complitex.flexbuh.entity.DomainObject;

/**
 * @author Pavel Sknar
 *         Date: 05.08.11 15:46
 */
public class DocumentType extends DomainObject {
	private static final String TABLE = "document_type";
	private String code;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String getTable() {
		return TABLE;
	}
}
