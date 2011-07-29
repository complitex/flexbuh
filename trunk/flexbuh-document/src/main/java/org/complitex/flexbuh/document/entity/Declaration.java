package org.complitex.flexbuh.document.entity;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 28.07.11 16:05
 */
@XmlRootElement(name = "DECLAR", namespace = "http://www.w3.org/2001/XMLSchema-instance")
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlSeeAlso(DeclarationValue.class)
public class Declaration {
    private static class Head{
        @XmlElement(name = "TIN") String tin;                   // код плательщика
        @XmlElement(name = "C_DOC") String cDoc;                // код документа
        @XmlElement(name = "C_DOC_SUB") String cDocSub;         // подтип документа
        @XmlElement(name = "C_DOC_VER") String cDocVer;         // номер версии документа
        @XmlElement(name = "C_DOC_TYPE") String cDocType;       // № исправительной документа (0 - основной (первого поданного))
        @XmlElement(name = "C_DOC_CNT") String cDocCnt;         // № однотипных документов в периоде
        @XmlElement(name = "C_REG") String cReg;                // код области
        @XmlElement(name = "C_RAJ") String cRaj;                // код административного района
        @XmlElement(name = "PERIOD_MONTH") String periodMonth;  // отчетный месяц (последний в отчетном периоде)
        @XmlElement(name = "PERIOD_TYPE") String periodType;    // тип периода
        @XmlElement(name = "PERIOD_YEAR") String periodYear;    // отчетный год
        @XmlElement(name = "C_STI_ORIG") String cStiOrig;       // Код ГНИ, в которую подается оригинал документа
        @XmlElement(name = "C_DOC_STAN") String cDocStan;       // законодательство
        @XmlElement(name = "D_FILL") String dFill;              // дата заполненные документы плательщиком
        @XmlElement(name = "SOFTWARE") String software;         // сигнатура программы
    }

    @XmlElement(name = "DECLARHEAD")
    private Head head;

    @XmlTransient
    private List<DeclarationValue> values = new ArrayList<DeclarationValue>();

    @XmlElementWrapper(name = "DECLARBODY")
    @XmlAnyElement
    private List<JAXBElement<DeclarationValue>> xmlValues = new ArrayList<JAXBElement<DeclarationValue>>();

    public Declaration() {
        head = new Head();
    }

    public void prepareXmlValues(){
        xmlValues.clear();

        for (DeclarationValue v : values){
            xmlValues.add(new JAXBElement<DeclarationValue>(new QName(v.getName()), DeclarationValue.class, v.getValue() != null ? v : null));
        }
    }

    /**
     * Значением элемента является код ЕГРПОУ для юридических лиц и идентификационный номер ГРФЛ для физических лиц.
     * Целое число (от 5 до 10 знаков) или символьное значение (формата серия + номер паспорта)
     * @return код плательщика
     */
    public String getTin() {
        return head.tin;
    }

    /**
     * Значением элемента является код ЕГРПОУ для юридических лиц и идентификационный номер ГРФЛ для физических лиц.
     * Целое число (от 5 до 10 знаков) или символьное значение (формата серия + номер паспорта)
     * @param tin код плательщика
     */
    public void setTin(String tin) {
        head.tin = tin;
    }

    /**
     * Соответствует значению элемента C_DOC из справочника отчетных документов (SPR_DOC.XML).
     * Фиксированное значение: Для юридических лиц J12, для физических лиц F12
     * @return код документа
     */
    public String getCDoc() {
        return head.cDoc;
    }

    /**
     * Соответствует значению элемента C_DOC из справочника отчетных документов (SPR_DOC.XML).
     * Фиксированное значение: Для юридических лиц J12, для физических лиц F12
     * @param cDoc код документа
     */
    public void setCDoc(String cDoc) {
        head.cDoc = cDoc;
    }

    /**
     * Соответствует значению элемента C_DOC_SUB из справочника отчетных документов (SPR_DOC.XML).
     * Фиксированное значение 015
     * @return подтип документа
     */
    public String getCDocSub() {
        return head.cDocSub;
    }

    /**
     * Соответствует значению элемента C_DOC_SUB из справочника отчетных документов (SPR_DOC.XML).
     * Фиксированное значение 015
     * @param cDocSub подтип документа
     */
    public void setCDocSub(String cDocSub) {
        head.cDocSub = cDocSub;
    }

    /**
     * Соответствует значению элемента C_DOC_VER из справочника отчетных документов (SPR_DOC.XML).
     * Фиксированное значение для действующей формы
     * @return номер версии документа
     */
    public String getCDocVer() {
        return head.cDocVer;
    }

    /**
     * Соответствует значению элемента C_DOC_VER из справочника отчетных документов (SPR_DOC.XML).
     * Фиксированное значение для действующей формы
     * @param cDocVer номер версии документа
     */
    public void setCDocVer(String cDocVer) {
        head.cDocVer = cDocVer;
    }

    /**
     * Для первого поданного в отчетном периоде документа значения данного элемента равен 0, каждый следующий исправительный документ
     * этого же типа в данном отчетном периоде в счет этого элемента больше на единицу.
     * Признак исправительного документа, для основного (базового) документа: 0, 1,2, ... для всех последующих, что является исправительными
     * в отношении базового документа
     * @return № исправительной документа (0 - основной (первого поданного))
     */
    public String getCDocType() {
        return head.cDocType;
    }

    /**
     * Для первого поданного в отчетном периоде документа значения данного элемента равен 0, каждый следующий исправительный документ
     * этого же типа в данном отчетном периоде в счет этого элемента больше на единицу.
     * Признак исправительного документа, для основного (базового) документа: 0, 1,2, ... для всех последующих, что является исправительными
     * в отношении базового документа
     * @param cDocType № исправительной документа (0 - основной (первого поданного))
     */
    public void setCDocType(String cDocType) {
        head.cDocType = cDocType;
    }

    /**
     * Если в одном отчетном периоде подается несколько однотипных документов, то значение данного элемента содержит порядковый номер
     * для каждого документа в данном периоде. Первый документ имеет номер 1. При формировании электронного документа, является уточненным
     * к поданному ранее (значение элемента C_DOC_TYPE> 0), нумерация однотипных документов в периоде (значение элемента C_DOC_CNT) должна
     * оставаться неизменной относительно нумерации документов, уточняются.
     * Признак, проставляется для документов, которые подаются несколько раз за период, в счет 1,2, ..., если документ подается 1 раз
     * за период имеет значение 1, если данный документ уточняется его С_DOC_CNT сохраняется
     * @return № однотипных документов в периоде
     */
    public String getCDocCnt() {
        return head.cDocCnt;
    }

    /**
     * Если в одном отчетном периоде подается несколько однотипных документов, то значение данного элемента содержит порядковый номер
     * для каждого документа в данном периоде. Первый документ имеет номер 1. При формировании электронного документа, является уточненным
     * к поданному ранее (значение элемента C_DOC_TYPE> 0), нумерация однотипных документов в периоде (значение элемента C_DOC_CNT) должна
     * оставаться неизменной относительно нумерации документов, уточняются.
     * Признак, проставляется для документов, которые подаются несколько раз за период, в счет 1,2, ..., если документ подается 1 раз
     * за период имеет значение 1, если данный документ уточняется его С_DOC_CNT сохраняется
     * @param cDocCnt № однотипных документов в периоде
     */
    public void setCDocCnt(String cDocCnt) {
        head.cDocCnt = cDocCnt;
    }

    /**
     * Значением этого элемента является код области, на территории которой расположена налоговая инспекция (или отделение), в которую
     * подается документ. Код области заполняется согласно справочнику государственных налоговых инспекций (SPR_STI.XML) и соответствует
     * значению элемента C_REG.
     * Код области ГНИ, в которую подается налоговая отчетность
     * @return код области
     */
    public String getCReg() {
        return head.cReg;
    }

    /**
     * Значением этого элемента является код области, на территории которой расположена налоговая инспекция (или отделение), в которую
     * подается документ. Код области заполняется согласно справочнику государственных налоговых инспекций (SPR_STI.XML) и соответствует
     * значению элемента C_REG.
     * Код области ГНИ, в которую подается налоговая отчетность
     * @param cReg код области
     */
    public void setCReg(String cReg) {
        head.cReg = cReg;
    }

    /**
     * Значением этого элемента является код административного района, на территории которой расположена налоговая инспекция
     * (или отделение), в которую подается документ. Код административного района заполняется согласно справочнику государственных
     * налоговых инспекций (SPR_STI.XML) и соответствует значению элемента C_RAJ.
     * Код административного района ГНИ
     * @return код административного района
     */
    public String getCRaj() {
        return head.cRaj;
    }

    /**
     * Значением этого элемента является код административного района, на территории которой расположена налоговая инспекция
     * (или отделение), в которую подается документ. Код административного района заполняется согласно справочнику государственных
     * налоговых инспекций (SPR_STI.XML) и соответствует значению элемента C_RAJ.
     * Код административного района ГНИ
     * @param cRaj код административного района
     */
    public void setCRaj(String cRaj) {
        head.cRaj = cRaj;
    }

    /**
     * Отчетным месяцем считается последний месяц в отчетном периоде.
     * Месяц, за который подается отчетность, (для 1,2,3,4 кварталов это 3,6,9,12 месяц соответственно, для года - 12)
     * @return отчетный месяц (последний в отчетном периоде)
     */
    public String getPeriodMonth() {
        return head.periodMonth;
    }

    /**
     * Отчетным месяцем считается последний месяц в отчетном периоде.
     * Месяц, за который подается отчетность, (для 1,2,3,4 кварталов это 3,6,9,12 месяц соответственно, для года - 12)
     * @param periodMonth отчетный месяц (последний в отчетном периоде)
     */
    public void setPeriodMonth(String periodMonth) {
        head.periodMonth = periodMonth;
    }

    /**
     * Фиксированное значение: 1-месяц, 2-квартал, 3-полугодие, 4-9 месяцев, 5-год
     * @return тип периода
     */
    public String getPeriodType() {
        return head.periodType;
    }

    /**
     * Фиксированное значение: 1-месяц, 2-квартал, 3-полугодие, 4-9 месяцев, 5-год
     * @param periodType тип периода
     */
    public void setPeriodType(String periodType) {
        head.periodType = periodType;
    }

    /**
     * Год, за который подается отчетность
     * @return отчетный год
     */
    public String getPeriodYear() {
        return head.periodYear;
    }

    /**
     * Год, за который подается отчетность
     * @param periodYear отчетный год
     */
    public void setPeriodYear(String periodYear) {
        head.periodYear = periodYear;
    }

    /**
     * Код ГНИ выбирается из справочника инспекций, является числом, которое соответствует формуле:
     * значение элемента C_REG * 100 + значение элемента C_RAJ.
     * @return Код ГНИ, в которую подается оригинал документа
     */
    public String getCStiOrig() {
        return head.cStiOrig;
    }

    /**
     * Код ГНИ выбирается из справочника инспекций, является числом, которое соответствует формуле:
     * значение элемента C_REG * 100 + значение элемента C_RAJ.
     * @param cStiOrig Код ГНИ, в которую подается оригинал документа
     */
    public void setCStiOrig(String cStiOrig) {
        head.cStiOrig = cStiOrig;
    }

    /**
     * Принимает фиксированные значения: 1-отчетный документ, 2-новый отчетный документ, 3-уточняющий документ
     * @return законодательство
     */
    public String getCDocStan() {
        return head.cDocStan;
    }

    /**
     * Принимает фиксированные значения: 1-отчетный документ, 2-новый отчетный документ, 3-уточняющий документ
     * @param cDocStan законодательство
     */
    public void setCDocStan(String cDocStan) {
        head.cDocStan = cDocStan;
    }

    /**
     * Формат ддммрррр
     * @return дата заполненные документы плательщиком
     */
    public String getDFill() {
        return head.dFill;
    }

    /**
     * Формат ддммрррр
     * @param dFill дата заполненные документы плательщиком
     */
    public void setDFill(String dFill) {
        head.dFill = dFill;
    }

    /**
     * Заполняется только программным обеспечением, которое сертифицировано ГНА Украины для формирования отчетности в электронном виде
     * @return сигнатура программы
     */
    public String getSoftware() {
        return head.software;
    }

    /**
     * Заполняется только программным обеспечением, которое сертифицировано ГНА Украины для формирования отчетности в электронном виде
     * @param software сигнатура программы
     */
    public void setSoftware(String software) {
        head.software = software;
    }

    public List<DeclarationValue> getValues() {
        return values;
    }

    public void setValues(List<DeclarationValue> values) {
        this.values = values;
    }
}
