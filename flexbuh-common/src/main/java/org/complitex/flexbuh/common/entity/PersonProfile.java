package org.complitex.flexbuh.common.entity;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.complitex.flexbuh.common.service.FIOBean;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

import static org.complitex.flexbuh.common.util.StringUtil.getString;

/**
 * @author Pavel Sknar
 *         Date: 31.08.11 14:26
 */
@XmlType
@XmlAccessorType(value = XmlAccessType.PROPERTY)
public class PersonProfile extends SessionObject {
    private PersonType personType = PersonType.JURIDICAL_PERSON; // тип плательщика

    private Integer cSti; // код ДПІ

    private Integer cStiTin; //Код ЄДРПОУ ДПІ

    private Integer tin; // код ЕДРПОУ (Единый государственный реестр предприятий и организаций Украины)

    private String name;

    private String firstName;

    private String middleName;

    private String lastName;

    private String profileName;

    private String numPdvSvd; // номер свидетельства ПДВ

    private String ipn; // индивидуальный налоговый номер

    private String kved; // код основного вида экономической деятельности(за КВЕД)

    private String koatuu; // КОАТУУ

    private Date contractDate; // договор об общей (совместной) деятельности (дата)

    private String contractNumber; // договор об общей (совместной) деятельности (номер)

    private String zipCode; // почтовый индекс

    private String address; // адрес

    private String phone; // телефон

    private String fax; // факс

    private String email; // e-mail

    private String dInn; // Код ДРФО директора

    // ФИО директора предприятия
    private String dLastName;
    private String dFirstName;
    private String dMiddleName;

    private String bInn; // Код ДРФО  бухгалтера

    // ФИО бухгалтера
    private String bLastName;
    private String bFirstName;
    private String bMiddleName;

    private boolean selected;

    private Long taxInspectionId;
    
    public void parsePhysicalNames(){
        String[] names = name.split(" ");

        if (names.length > 0){
            lastName = names[0];
        }
        if (names.length > 1){
            firstName = names[1];
        }
        if (names.length > 2){
            middleName = names[2];
        }
    }
    
    public void mergePhysicalNames(){
        name = (getString(lastName) + " " + getString(firstName) + " " + getString(middleName)).trim();
    }

    @XmlJavaTypeAdapter(PersonTypeAdapter.class)
    @XmlElement(name = "PERSON_TYPE", required = true)
    public PersonType getPersonType() {
        return personType;
    }

    public void setPersonType(PersonType personType) {
        this.personType = personType;
    }

    @XmlElement(name = "C_STI")
    public Integer getCSti() {
        return cSti;
    }

    public void setCSti(Integer cSti) {
        this.cSti = cSti;
    }

    @XmlElement(name = "C_STI_TIN")
    public Integer getCStiTin() {
        return cStiTin;
    }

    public void setCStiTin(Integer cStiTin) {
        this.cStiTin = cStiTin;
    }

    @XmlElement(name = "TIN")
    public Integer getTin() {
        return tin;
    }

    public void setTin(Integer tin) {
        this.tin = tin;
    }

    @XmlElement(name = "NAME", required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlTransient
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @XmlTransient
    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    @XmlTransient
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @XmlTransient
    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    @XmlElement(name = "NUMPDVSVD")
    public String getNumPdvSvd() {
        return numPdvSvd;
    }

    public void setNumPdvSvd(String numPdvSvd) {
        this.numPdvSvd = numPdvSvd;
    }

    @XmlElement(name = "IPN")
    public String getIpn() {
        return ipn;
    }

    public void setIpn(String ipn) {
        this.ipn = ipn;
    }

    @XmlElement(name = "KVED")
    public String getKved() {
        return kved;
    }

    public void setKved(String kved) {
        this.kved = kved;
    }

    @XmlElement(name = "KOATUU")
    public String getKoatuu() {
        return koatuu;
    }

    public void setKoatuu(String koatuu) {
        this.koatuu = koatuu;
    }

    @XmlElement(name = "CONTRACT_DATE")
    public Date getContractDate() {
        return contractDate;
    }

    public void setContractDate(Date contractDate) {
        this.contractDate = contractDate;
    }

    @XmlElement(name = "CONTRACT_NUMBER")
    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    @XmlElement(name = "ZIPCODE")
    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    @XmlElement(name = "ADRESS")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @XmlElement(name = "PHONE")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @XmlElement(name = "FAX")
    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    @XmlElement(name = "EMAIL")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @XmlElement(name = "DINN")
    public String getDInn() {
        return dInn;
    }

    public void setDInn(String dInn) {
        this.dInn = dInn;
    }

    @XmlElement(name = "DFIO")
    public String getDFio() {
        return FIOBean.getFIO(dLastName, dFirstName, dMiddleName);
    }

    public void setDFio(String dFio) {
        dLastName = FIOBean.getLastName(dFio);
        dFirstName = FIOBean.getFirstName(dFio);
        dMiddleName = FIOBean.getMiddleName(dFio);
    }

    @XmlTransient
    public String getDLastName() {
        return dLastName;
    }

    public void setDLastName(String dLastName) {
        this.dLastName = dLastName;
    }

    @XmlTransient
    public String getDFirstName() {
        return dFirstName;
    }

    public void setDFirstName(String dFirstName) {
        this.dFirstName = dFirstName;
    }

    @XmlTransient
    public String getDMiddleName() {
        return dMiddleName;
    }

    public void setDMiddleName(String dMiddleName) {
        this.dMiddleName = dMiddleName;
    }

    @XmlElement(name = "BINN")
    public String getBInn() {
        return bInn;
    }

    public void setBInn(String bInn) {
        this.bInn = bInn;
    }

    @XmlElement(name = "BFIO")
    public String getBFio() {
        return FIOBean.getFIO(bLastName, bFirstName, bMiddleName);
    }

    public void setBFio(String bFio) {
        bLastName = FIOBean.getLastName(bFio);
        bFirstName = FIOBean.getFirstName(bFio);
        bMiddleName = FIOBean.getMiddleName(bFio);
    }

    @XmlTransient
    public String getBLastName() {
        return bLastName;
    }

    public void setBLastName(String bLastName) {
        this.bLastName = bLastName;
    }

    @XmlTransient
    public String getBFirstName() {
        return bFirstName;
    }

    public void setBFirstName(String bFirstName) {
        this.bFirstName = bFirstName;
    }

    @XmlTransient
    public String getBMiddleName() {
        return bMiddleName;
    }

    public void setBMiddleName(String bMiddleName) {
        this.bMiddleName = bMiddleName;
    }

    @XmlAttribute(name = "SELECTED")
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @XmlTransient
    public Long getTaxInspectionId() {
        return taxInspectionId;
    }

    public void setTaxInspectionId(Long taxInspectionId) {
        this.taxInspectionId = taxInspectionId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
