package org.complitex.flexbuh.common.entity;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

import static org.complitex.flexbuh.common.util.StringUtil.getString;

/**
 * @author Pavel Sknar
 *         Date: 31.08.11 14:26
 */
@XmlType
@XmlAccessorType(value = XmlAccessType.FIELD)
public class PersonProfile extends SessionObject {
    @XmlJavaTypeAdapter(PersonTypeAdapter.class)
    @XmlElement(name = "PERSON_TYPE", required = true)
    private PersonType personType = PersonType.JURIDICAL_PERSON; // тип плательщика

    @XmlElement(name = "C_STI")
    private Integer cSti; // код ДПІ

    @XmlElement(name = "C_STI_TIN")
    private String cStiTin; //Код ЄДРПОУ ДПІ

    @XmlElement(name = "TIN")
    private String tin; // код ЕДРПОУ

    @XmlElement(name = "NAME", required = true)
    private String name;

    @XmlTransient
    private String firstName;

    @XmlTransient
    private String middleName;

    @XmlTransient
    private String lastName;

    @XmlTransient
    private String profileName;

    @XmlElement(name = "NUMPDVSVD")
    private String numPdvSvd; // номер свидетельства ПДВ

    @XmlElement(name = "IPN")
    private String ipn; // индивидуальный налоговый номер

    @XmlElement(name = "KVED")
    private String kved; // код основного вида экономической деятельности(за КВЕД)

    @XmlElement(name = "KOATUU")
    private String koatuu; // КОАТУУ

    @XmlElement(name = "CONTRACT_DATE")
    private Date contractDate; // договор об общей (совместной) деятельности (дата)

    @XmlElement(name = "CONTRACT_NUMBER")
    private String contractNumber; // договор об общей (совместной) деятельности (номер)

    @XmlElement(name = "ZIPCODE")
    private String zipCode; // почтовый индекс

    @XmlElement(name = "ADRESS")
    private String address; // адрес

    @XmlElement(name = "PHONE")
    private String phone; // телефон

    @XmlElement(name = "FAX")
    private String fax; // факс

    @XmlElement(name = "EMAIL")
    private String email; // e-mail

    @XmlElement(name = "DINN")
    private String dInn; // Код ДРФО директора

    @XmlElement(name = "DFIO")
    private String dFio; // ФИО директора предприятия

    @XmlElement(name = "BINN")
    private String bInn; // Код ДРФО  бухгалтера

    @XmlElement(name = "BFIO")
    private String bFio; // ФИО бухгалтера

    @XmlAttribute(name = "SELECTED")
    private boolean selected;

    @XmlTransient
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

    public PersonType getPersonType() {
        return personType;
    }

    public void setPersonType(PersonType personType) {
        this.personType = personType;
    }

    public Integer getCSti() {
        return cSti;
    }

    public void setCSti(Integer cSti) {
        this.cSti = cSti;
    }

    public String getCStiTin() {
        return cStiTin;
    }

    public void setCStiTin(String cStiTin) {
        this.cStiTin = cStiTin;
    }

    public String getTin() {
        return tin;
    }

    public void setTin(String tin) {
        this.tin = tin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getNumPdvSvd() {
        return numPdvSvd;
    }

    public void setNumPdvSvd(String numPdvSvd) {
        this.numPdvSvd = numPdvSvd;
    }

    public String getIpn() {
        return ipn;
    }

    public void setIpn(String ipn) {
        this.ipn = ipn;
    }

    public String getKved() {
        return kved;
    }

    public void setKved(String kved) {
        this.kved = kved;
    }

    public String getKoatuu() {
        return koatuu;
    }

    public void setKoatuu(String koatuu) {
        this.koatuu = koatuu;
    }

    public Date getContractDate() {
        return contractDate;
    }

    public void setContractDate(Date contractDate) {
        this.contractDate = contractDate;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDInn() {
        return dInn;
    }

    public void setDInn(String dInn) {
        this.dInn = dInn;
    }

    public String getDFio() {
        return dFio;
    }

    public void setDFio(String dFio) {
        this.dFio = dFio;
    }

    public String getBInn() {
        return bInn;
    }

    public void setBInn(String bInn) {
        this.bInn = bInn;
    }

    public String getBFio() {
        return bFio;
    }

    public void setBFio(String bFio) {
        this.bFio = bFio;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

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
