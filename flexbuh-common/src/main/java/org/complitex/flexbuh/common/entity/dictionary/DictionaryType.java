package org.complitex.flexbuh.common.entity.dictionary;

/**
 * @author Pavel Sknar
 *         Date: 05.08.11 12:19
 */
public enum DictionaryType{
    CURRENCY("SPR_CURRENCY.XML"),
    DOCUMENT("SPR_DOC.XML"),
    DOCUMENT_TERM("SPR_TERM.XML"),
    DOCUMENT_VERSION("SPR_VER.XML"),
    REGION("SPR_REGION.XML"),
    TAX_INSPECTION("SPR_STI.XML"),
    FIELD_CODE("sprForFields.xml");

	private String fileName;

    private DictionaryType(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
