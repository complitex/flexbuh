package org.complitex.flexbuh.common.entity.dictionary;

import org.complitex.flexbuh.common.entity.LocalizedDomainObject;

/**
 * @author Pavel Sknar
 *         Date: 05.08.11 12:19
 */
public class DictionaryType extends LocalizedDomainObject {
	private String code;
	private String fileName;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "DictionaryType{" +
                "code='" + code + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
