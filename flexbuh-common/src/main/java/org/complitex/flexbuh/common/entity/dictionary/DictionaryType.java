package org.complitex.flexbuh.common.entity.dictionary;

/**
 * @author Pavel Sknar
 *         Date: 05.08.11 12:19
 */
public enum DictionaryType{
    CURRENCY("SPR_CURRENCY.XML", Currency.class),
    DOCUMENT("SPR_DOC.XML", Document.class),
    DOCUMENT_TERM("SPR_TERM.XML", DocumentTerm.class),
    DOCUMENT_VERSION("SPR_VER.XML", DocumentVersion.class),
    REGION("SPR_REGION.XML", Region.class),
    TAX_INSPECTION("SPR_STI.XML", TaxInspectionRegion.class),
    FIELD_CODE("sprForFields.xml", FieldCode.class);

	private String fileName;
    private Class modelClass;

    private DictionaryType(String fileName, Class modelClass) {
        this.fileName = fileName;
        this.modelClass = modelClass;
    }

    public String getFileName() {
        return fileName;
    }

    public Class getModelClass() {
        return modelClass;
    }
}
