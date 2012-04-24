package org.complitex.flexbuh.common.entity;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.complitex.flexbuh.common.util.FIOUtil;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 31.08.11 14:26
 */
@XmlType
@XmlAccessorType(value = XmlAccessType.PROPERTY)
public class PersonProfile extends SessionObject {
    public static final class DateAdapter extends XmlAdapter<String, Date> {
        private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

        @Override
        public Date unmarshal(String v) throws Exception {
            return DATE_FORMAT.parse(v);
        }

        @Override
        public String marshal(Date v) throws Exception {
            return DATE_FORMAT.format(v);
        }
    }

    public final static String SELECTED_PERSON_PROFILE_ID = "selected_person_profile_id";

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

    private String taxInspectionName;

    private String userName;

    private Integer num;

    //opz format
    private String savePath = "C:\\OPZ\\xml\\";
    private String printPath = "C:\\OPZ\\output\\";
    private String sendPath = "C:\\OPZ\\send\\";
    private String autoFill = "1";
    private String autoSend = "1";

    public void parsePhysicalNames(){
        lastName = FIOUtil.getLastName(name);
        firstName = FIOUtil.getFirstName(name);
        middleName = FIOUtil.getMiddleName(name);
    }

    public void mergePhysicalNames(){
        name = FIOUtil.getFIO(lastName, firstName, middleName);
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

    @XmlElement(name = "FB_PROFILE_NAME")
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

    @XmlJavaTypeAdapter(DateAdapter.class)
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
        return FIOUtil.getFIO(dLastName, dFirstName, dMiddleName);
    }

    public void setDFio(String dFio) {
        dLastName = FIOUtil.getLastName(dFio);
        dFirstName = FIOUtil.getFirstName(dFio);
        dMiddleName = FIOUtil.getMiddleName(dFio);
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
        return FIOUtil.getFIO(bLastName, bFirstName, bMiddleName);
    }

    public void setBFio(String bFio) {
        bLastName = FIOUtil.getLastName(bFio);
        bFirstName = FIOUtil.getFirstName(bFio);
        bMiddleName = FIOUtil.getMiddleName(bFio);
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

    /**
     * selected используется в OPZ, выбранный профиль в Flexbuh можно получить через предпочтения
     * <code>TemplateSession.getPreferenceLong(PersonProfile.SELECTED_PERSON_PROFILE_ID)</code>
     * @return активный профиль в OPZ
     */
    @XmlAttribute(name = "selected")
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @XmlAttribute(name = "num")
    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    @XmlElement(name = "SAVE_PATH")
    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    @XmlElement(name = "PRINT_PATH")
    public String getPrintPath() {
        return printPath;
    }

    public void setPrintPath(String printPath) {
        this.printPath = printPath;
    }

    @XmlElement(name = "SEND_PATH")
    public String getSendPath() {
        return sendPath;
    }

    public void setSendPath(String sendPath) {
        this.sendPath = sendPath;
    }

    @XmlElement(name = "AUTOFILL")
    public String getAutoFill() {
        return autoFill;
    }

    public void setAutoFill(String autoFill) {
        this.autoFill = autoFill;
    }

    @XmlElement(name = "AUTOSEND")
    public String getAutoSend() {
        return autoSend;
    }

    public void setAutoSend(String autoSend) {
        this.autoSend = autoSend;
    }

    @XmlTransient
    public Long getTaxInspectionId() {
        return taxInspectionId;
    }

    public void setTaxInspectionId(Long taxInspectionId) {
        this.taxInspectionId = taxInspectionId;
    }

    @XmlTransient
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @XmlTransient
    public String getTaxInspectionName() {
        return taxInspectionName;
    }

    public void setTaxInspectionName(String taxInspectionName) {
        this.taxInspectionName = taxInspectionName;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonProfile)) return false;

        PersonProfile that = (PersonProfile) o;

        if (selected != that.selected) return false;
        if (address != null ? !address.equals(that.address) : that.address != null) return false;
        if (bFirstName != null ? !bFirstName.equals(that.bFirstName) : that.bFirstName != null) return false;
        if (bInn != null ? !bInn.equals(that.bInn) : that.bInn != null) return false;
        if (bLastName != null ? !bLastName.equals(that.bLastName) : that.bLastName != null) return false;
        if (bMiddleName != null ? !bMiddleName.equals(that.bMiddleName) : that.bMiddleName != null) return false;
        if (cSti != null ? !cSti.equals(that.cSti) : that.cSti != null) return false;
        if (cStiTin != null ? !cStiTin.equals(that.cStiTin) : that.cStiTin != null) return false;
        if (contractDate != null ? !contractDate.equals(that.contractDate) : that.contractDate != null) return false;
        if (contractNumber != null ? !contractNumber.equals(that.contractNumber) : that.contractNumber != null)
            return false;
        if (dFirstName != null ? !dFirstName.equals(that.dFirstName) : that.dFirstName != null) return false;
        if (dInn != null ? !dInn.equals(that.dInn) : that.dInn != null) return false;
        if (dLastName != null ? !dLastName.equals(that.dLastName) : that.dLastName != null) return false;
        if (dMiddleName != null ? !dMiddleName.equals(that.dMiddleName) : that.dMiddleName != null) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (fax != null ? !fax.equals(that.fax) : that.fax != null) return false;
        if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null) return false;
        if (ipn != null ? !ipn.equals(that.ipn) : that.ipn != null) return false;
        if (koatuu != null ? !koatuu.equals(that.koatuu) : that.koatuu != null) return false;
        if (kved != null ? !kved.equals(that.kved) : that.kved != null) return false;
        if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null) return false;
        if (middleName != null ? !middleName.equals(that.middleName) : that.middleName != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (numPdvSvd != null ? !numPdvSvd.equals(that.numPdvSvd) : that.numPdvSvd != null) return false;
        if (personType != that.personType) return false;
        if (phone != null ? !phone.equals(that.phone) : that.phone != null) return false;
        if (profileName != null ? !profileName.equals(that.profileName) : that.profileName != null) return false;
        if (taxInspectionId != null ? !taxInspectionId.equals(that.taxInspectionId) : that.taxInspectionId != null)
            return false;
        if (tin != null ? !tin.equals(that.tin) : that.tin != null) return false;
        if (zipCode != null ? !zipCode.equals(that.zipCode) : that.zipCode != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = personType != null ? personType.hashCode() : 0;
        result = 31 * result + (cSti != null ? cSti.hashCode() : 0);
        result = 31 * result + (cStiTin != null ? cStiTin.hashCode() : 0);
        result = 31 * result + (tin != null ? tin.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (middleName != null ? middleName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (profileName != null ? profileName.hashCode() : 0);
        result = 31 * result + (numPdvSvd != null ? numPdvSvd.hashCode() : 0);
        result = 31 * result + (ipn != null ? ipn.hashCode() : 0);
        result = 31 * result + (kved != null ? kved.hashCode() : 0);
        result = 31 * result + (koatuu != null ? koatuu.hashCode() : 0);
        result = 31 * result + (contractDate != null ? contractDate.hashCode() : 0);
        result = 31 * result + (contractNumber != null ? contractNumber.hashCode() : 0);
        result = 31 * result + (zipCode != null ? zipCode.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (fax != null ? fax.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (dInn != null ? dInn.hashCode() : 0);
        result = 31 * result + (dLastName != null ? dLastName.hashCode() : 0);
        result = 31 * result + (dFirstName != null ? dFirstName.hashCode() : 0);
        result = 31 * result + (dMiddleName != null ? dMiddleName.hashCode() : 0);
        result = 31 * result + (bInn != null ? bInn.hashCode() : 0);
        result = 31 * result + (bLastName != null ? bLastName.hashCode() : 0);
        result = 31 * result + (bFirstName != null ? bFirstName.hashCode() : 0);
        result = 31 * result + (bMiddleName != null ? bMiddleName.hashCode() : 0);
        result = 31 * result + (selected ? 1 : 0);
        result = 31 * result + (taxInspectionId != null ? taxInspectionId.hashCode() : 0);
        return result;
    }
}
