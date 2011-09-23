package org.complitex.flexbuh.document.entity;

import javax.xml.bind.annotation.*;

/**
 * Перечень связанных документов.
 * Данный элемент является узловым и состоит из ряда элементов с именем DOC , каждый из которых содержит информацию об отдельном документе.
 * Элемент DOC имеет следующие дочерние элемент, совпадают с соответствующими значениями в элементе DECLARHEAD связанного документа
 *
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 28.07.11 17:29
 */
@XmlType
@XmlAccessorType(value = XmlAccessType.FIELD)
public class LinkedDeclaration {
    @XmlAttribute(name = "NUM")
    private String num;         // номер связано документа в перечня

    @XmlAttribute(name = "TYPE")
    private String type;        // тип связи

    @XmlElement(name = "C_DOC")
    private String cDoc;        // код документа

    @XmlElement(name = "C_DOC_SUB")
    private String cDocSub;     // ПОДТИП ДОКУМЕНТА

    @XmlElement(name = "C_DOC_VER")
    private Integer cDocVer;     // номер версии документа

    @XmlElement(name = "C_DOC_TYPE")
    private Integer cDocType;    // № нового отчетного (уточняющих) документа (0 - отчетный)

    @XmlElement(name = "C_DOC_CNT")
    private Integer cDocCnt;     // № однотипных документов в периоде

    @XmlElement(name = "C_DOC_STAN")
    private Integer cDocStan;    // законодательство

    @XmlElement(name = "FILENAME")
    private String filename;    // имя файла

    @XmlTransient
    private Declaration declaration;

    public LinkedDeclaration() {
    }

    public LinkedDeclaration(Declaration declaration) {
        this.declaration = declaration;
        copyFieldsFromDeclaration();
    }

    public void copyFieldsFromDeclaration(){
        cDoc = declaration.getHead().getCDoc();
        cDocSub = declaration.getHead().getCDocSub();
        cDocVer = declaration.getHead().getCDocVer();
        cDocType = declaration.getHead().getCDocType();
        cDocCnt = declaration.getHead().getCDocCnt();
        cDocStan = declaration.getHead().getCDocStan();
    }

    public String getName(){
        return cDoc + cDocSub + (cDocVer < 10 ? "0" : "") + cDocVer;
    }

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
     * @see org.complitex.flexbuh.document.entity.Declaration#getHead().getCDoc()
     * @return Код документа
     */
    public String getСDoc() {
        return cDoc;
    }

    /**
     * @see org.complitex.flexbuh.document.entity.Declaration#getHead().setCDoc(String)
     * @param cDoc Код документа
     */
    public void setСDoc(String cDoc) {
        this.cDoc = cDoc;
    }

    /**
     * @see org.complitex.flexbuh.document.entity.Declaration#getHead().getCDocSub()
     * @return Подтип документа
     */
    public String getСDocSub() {
        return cDocSub;
    }

    /**
     * @see org.complitex.flexbuh.document.entity.Declaration#getHead().setCDocSub(String)
     * @param cDocSub Подтип документа
     */
    public void setСDocSub(String cDocSub) {
        this.cDocSub = cDocSub;
    }

    /**
     * @see org.complitex.flexbuh.document.entity.Declaration#getHead().getCDocVer()
     * @return Номер версии документа
     */
    public Integer getСDocVer() {
        return cDocVer;
    }

    /**
     * @see org.complitex.flexbuh.document.entity.Declaration#getHead().setCDocVer(String)
     * @param cDocVer Номер версии документа
     */
    public void setСDocVer(Integer cDocVer) {
        this.cDocVer = cDocVer;
    }

    /**
     * @see org.complitex.flexbuh.document.entity.Declaration#getHead().getCDocType()
     * @return Номер нового отчетного (уточняющего) документа
     */
    public Integer getСDocType() {
        return cDocType;
    }

    /**
     * @see org.complitex.flexbuh.document.entity.Declaration#getHead().setCDocType(String)
     * @param cDocType Номер нового отчетного (уточняющего) документа
     */
    public void setCDocType(Integer cDocType) {
        this.cDocType = cDocType;
    }

    /**
     * @see org.complitex.flexbuh.document.entity.Declaration#getHead().getCDocCnt()
     * @return Номер однотипного документа в периоде
     */
    public Integer getСDocCnt() {
        return cDocCnt;
    }

    /**
     * @see org.complitex.flexbuh.document.entity.Declaration#getHead().setCDocCnt(String)
     * @param cDocCnt Номер однотипного документа в периоде
     */
    public void setСDocCnt(Integer cDocCnt) {
        this.cDocCnt = cDocCnt;
    }

    /**
     * @see org.complitex.flexbuh.document.entity.Declaration#getHead().getCDocStan()
     * @return Законодательство
     */
    public Integer getСDocStan() {
        return cDocStan;
    }

    /**
     * @see org.complitex.flexbuh.document.entity.Declaration#getHead().setCDocStan(String)
     * @param cDocStan Законодательство
     */
    public void setСDocStan(Integer cDocStan) {
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

    public Declaration getDeclaration() {
        return declaration;
    }

    public void setDeclaration(Declaration declaration) {
        this.declaration = declaration;
    }
}
