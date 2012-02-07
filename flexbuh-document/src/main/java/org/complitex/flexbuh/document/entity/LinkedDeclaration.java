package org.complitex.flexbuh.document.entity;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

/**
 * Перечень связанных документов.
 * Данный элемент является узловым и состоит из ряда элементов с именем DOC , каждый из которых содержит информацию об отдельном документе.
 * Элемент DOC имеет следующие дочерние элемент, совпадают с соответствующими значениями в элементе DECLARHEAD связанного документа
 *
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 28.07.11 17:29
 */
@XmlType(propOrder = {"CDoc", "CDocSub", "CDocVer", "CDocType", "CDocCnt", "CDocStan", "filename", "num", "type"})
@XmlAccessorType(XmlAccessType.PROPERTY)
public class LinkedDeclaration implements Serializable{
    private Long id;
    private String num;         // номер связано документа в перечня
    private String type;        // тип связи

    @XmlTransient
    private Declaration declaration;

    public LinkedDeclaration() {
    }

    public LinkedDeclaration(Declaration declaration) {
        this.declaration = declaration;
    }

    @XmlTransient
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName(){
        return declaration.getTemplateName();
    }

    /**
     * @see org.complitex.flexbuh.document.entity.Declaration#getHead().getCDoc()
     * @return Код документа
     */
    @XmlElement(name = "C_DOC")
    public String getCDoc() {
        return declaration.getHead().getCDoc();
    }

    /**
     * @see org.complitex.flexbuh.document.entity.Declaration#getHead().setCDoc(String)
     * @param cDoc Код документа
     */
    public void setСDoc(String cDoc) {
        declaration.getHead().setCDoc(cDoc);
    }

    /**
     * @see org.complitex.flexbuh.document.entity.Declaration#getHead().getCDocSub()
     * @return Подтип документа
     */
    @XmlElement(name = "C_DOC_SUB")
    public String getCDocSub() {
        return declaration.getHead().getCDocSub();
    }

    /**
     * @see org.complitex.flexbuh.document.entity.Declaration#getHead().setCDocSub(String)
     * @param cDocSub Подтип документа
     */
    public void setCDocSub(String cDocSub) {
        declaration.getHead().setCDocSub(cDocSub);
    }

    /**
     * @see org.complitex.flexbuh.document.entity.Declaration#getHead().getCDocVer()
     * @return Номер версии документа
     */
    @XmlElement(name = "C_DOC_VER")
    public Integer getCDocVer() {
        return declaration.getHead().getCDocVer();
    }

    /**
     * @see org.complitex.flexbuh.document.entity.Declaration#getHead().setCDocVer(String)
     * @param cDocVer Номер версии документа
     */
    public void setCDocVer(Integer cDocVer) {
        declaration.getHead().setCDocVer(cDocVer);
    }

    /**
     * @see org.complitex.flexbuh.document.entity.Declaration#getHead().getCDocType()
     * @return Номер нового отчетного (уточняющего) документа
     */
    @XmlElement(name = "C_DOC_TYPE")
    public Integer getCDocType() {
        return declaration.getHead().getCDocType();
    }

    /**
     * @see org.complitex.flexbuh.document.entity.Declaration#getHead().setCDocType(String)
     * @param cDocType Номер нового отчетного (уточняющего) документа
     */
    public void setCDocType(Integer cDocType) {
        declaration.getHead().setCDocType(cDocType);
    }

    /**
     * @see org.complitex.flexbuh.document.entity.Declaration#getHead().getCDocCnt()
     * @return Номер однотипного документа в периоде
     */
    @XmlElement(name = "C_DOC_CNT")
    public Integer getCDocCnt() {
        return declaration.getHead().getCDocCnt();
    }

    /**
     * @see org.complitex.flexbuh.document.entity.Declaration#getHead().setCDocCnt(String)
     * @param cDocCnt Номер однотипного документа в периоде
     */
    public void setCDocCnt(Integer cDocCnt) {
        declaration.getHead().setCDocCnt(cDocCnt);
    }

    /**
     * @see org.complitex.flexbuh.document.entity.Declaration#getHead().getCDocStan()
     * @return Законодательство
     */
    @XmlElement(name = "C_DOC_STAN")
    public Integer getCDocStan() {
        return declaration.getHead().getCDocStan();
    }

    /**
     * @see org.complitex.flexbuh.document.entity.Declaration#getHead().setCDocStan(String)
     * @param cDocStan Законодательство
     */
    public void setCDocStan(Integer cDocStan) {
        declaration.getHead().setCDocStan(cDocStan);
    }

    /**
     *
     * @return Имя файла, содержащего связанный документ
     */
    @XmlElement(name = "FILENAME")
    public String getFilename() {
        return declaration.getFileName();
    }

    /**
     *
     * @param filename Имя файла, содержащего связанный документ
     */
    public void setFilename(String filename) {
        declaration.setFileName(filename);
    }

    /**
     *
     * @return Номер связанного документа в перечне
     */
    @XmlAttribute(name = "NUM")
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
    @XmlAttribute(name = "TYPE")
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

    @XmlTransient
    public Declaration getDeclaration() {
        return declaration;
    }

    public void setDeclaration(Declaration declaration) {
        this.declaration = declaration;
    }
}
