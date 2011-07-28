package org.complitex.flexbuh.document.entity;

/**
 * Перечень связанных документов.
 * Данный элемент является узловым и состоит из ряда элементов с именем DOC , каждый из которых содержит информацию об отдельном документе.
 * Элемент DOC имеет следующие дочерние элемент, совпадают с соответствующими значениями в элементе DECLARHEAD связанного документа
 *
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 28.07.11 17:29
 */
public class LinkedDocument {
    private String num;         // номер связано документа в перечня
    private String type;        // тип связи
    private String cDoc;        // код документа
    private String cDocSub;     // ПОДТИП ДОКУМЕНТА
    private String cDocVer;     // номер версии документа
    private String cDocType;    // № нового отчетного (уточняющих) документа (0 - отчетный)
    private String cDocCnt;     // № однотипных документов в периоде
    private String cDocStan;    // законодательство
    private String filename;    // имя файла

    /**
     *
     * @return Номер связанного документа в перечне
     */
    public String getNum() {
        return num;
    }

    /**
     *
     * @param num Номер связанного документа в перечне
     */
    public void setNum(String num) {
        this.num = num;
    }

    /**
     * Фиксированные значения: 1 - ссылка на приложение, 2 - ссылки на основной документ, 3 - ссылка на документ
     * @return тип связи
     */
    public String getType() {
        return type;
    }

    /**
     * Фиксированные значения: 1 - ссылка на приложение, 2 - ссылки на основной документ, 3 - ссылка на документ
     * @param type тип связи
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @see Declaration#getCDoc()
     * @return Код документа
     */
    public String getСDoc() {
        return cDoc;
    }

    /**
     * @see Declaration#setCDoc(String)
     * @param cDoc Код документа
     */
    public void setСDoc(String cDoc) {
        this.cDoc = cDoc;
    }

    /**
     * @see Declaration#getCDocSub()
     * @return Подтип документа
     */
    public String getСDocSub() {
        return cDocSub;
    }

    /**
     * @see Declaration#setCDocSub(String)
     * @param cDocSub Подтип документа
     */
    public void setСDocSub(String cDocSub) {
        this.cDocSub = cDocSub;
    }

    /**
     * @see Declaration#getCDocVer()
     * @return Номер версии документа
     */
    public String getСDocVer() {
        return cDocVer;
    }

    /**
     * @see Declaration#setCDocVer(String)
     * @param cDocVer Номер версии документа
     */
    public void setСDocVer(String cDocVer) {
        this.cDocVer = cDocVer;
    }

    /**
     * @see Declaration#getCDocType()
     * @return Номер нового отчетного (уточняющего) документа
     */
    public String getСDocType() {
        return cDocType;
    }

    /**
     * @see Declaration#setCDocType(String)
     * @param cDocType Номер нового отчетного (уточняющего) документа
     */
    public void setCDocType(String cDocType) {
        this.cDocType = cDocType;
    }

    /**
     * @see Declaration#getCDocCnt()
     * @return Номер однотипного документа в периоде
     */
    public String getСDocCnt() {
        return cDocCnt;
    }

    /**
     * @see Declaration#setCDocCnt(String)
     * @param cDocCnt Номер однотипного документа в периоде
     */
    public void setСDocCnt(String cDocCnt) {
        this.cDocCnt = cDocCnt;
    }

    /**
     * @see Declaration#getCDocStan()
     * @return Законодательство
     */
    public String getСDocStan() {
        return cDocStan;
    }

    /**
     * @see Declaration#setCDocStan(String)
     * @param cDocStan Законодательство
     */
    public void setСDocStan(String cDocStan) {
        this.cDocStan = cDocStan;
    }

    /**
     *
     * @return Имя файла, содержащего связанный документ
     */
    public String getFilename() {
        return filename;
    }

    /**
     *
     * @param filename Имя файла, содержащего связанный документ
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }
}
