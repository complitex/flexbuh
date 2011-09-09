package org.complitex.flexbuh.entity.user;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.complitex.flexbuh.entity.DomainObject;
import org.complitex.flexbuh.service.user.String2PersonType;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 31.08.11 14:26
 */
@XmlType()
public class PersonProfile extends DomainObject {
	public static final String TABLE = "person_profile";

	private String name;
	private String codeTIN; // Код ЕДРПОУ
	private Integer codeTaxInspection; // Код ДПІ
	private String codeKVED; // Код основного виду економічної діяльності (за КВЕД)

	private PersonType personType; // Тип платника
	private Date contractDate; // Договір про спільну (сумісну) діяльність (дата)
	private String contractNumber; // Договір про спільну (сумісну) діяльність (номер)
	private String zipCode; // Поштовий індекс
	private String address; // Адреса
	private String phone; // Телефон
	private String fax; // Факс
	private String email; // E-mail
	private String directorFIO; // ФИО директора предприятия
	private String accountantFIO; // ФИО бухгалтера
	private String directorINN; // Код ДРФО директора
	private String accountantINN; // Код ДРФО  бухгалтера
	private String ipn; // Індивідуальний податковий номер
	private String numSvdPDV; // Номер свідоцтва ПДВ

	private boolean selected;

	@XmlElement(name = "NAME")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name = "TIN")
	public String getCodeTIN() {
		return codeTIN;
	}
	public void setCodeTIN(String codeTIN) {
		this.codeTIN = codeTIN;
	}

	@XmlElement(name = "C_STI")
	public Integer getCodeTaxInspection() {
		return codeTaxInspection;
	}
	public void setCodeTaxInspection(Integer codeTaxInspection) {
		this.codeTaxInspection = codeTaxInspection;
	}

	@XmlElement(name = "KVED")
	public String getCodeKVED() {
		return codeKVED;
	}
	public void setCodeKVED(String codeKVED) {
		this.codeKVED = codeKVED;
	}

	@XmlSchemaType(name="string")
	@XmlJavaTypeAdapter( String2PersonType.class )
	@XmlElement(name = "PERSON_TYPE")
	public PersonType getPersonType() {
		return personType;
	}
	public void setPersonType(PersonType personType) {
		this.personType = personType;
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

	@XmlElement(name = "DFIO")
	public String getDirectorFIO() {
		return directorFIO;
	}
	public void setDirectorFIO(String directorFIO) {
		this.directorFIO = directorFIO;
	}

	@XmlElement(name = "BFIO")
	public String getAccountantFIO() {
		return accountantFIO;
	}
	public void setAccountantFIO(String accountantFIO) {
		this.accountantFIO = accountantFIO;
	}

	@XmlElement(name = "DINN")
	public String getDirectorINN() {
		return directorINN;
	}
	public void setDirectorINN(String directorINN) {
		this.directorINN = directorINN;
	}

	@XmlElement(name = "BINN")
	public String getAccountantINN() {
		return accountantINN;
	}
	public void setAccountantINN(String accountantINN) {
		this.accountantINN = accountantINN;
	}

	@XmlElement(name = "IPN")
	public String getIpn() {
		return ipn;
	}
	public void setIpn(String ipn) {
		this.ipn = ipn;
	}

	@XmlElement(name = "NUMPDVSVD")
	public String getNumSvdPDV() {
		return numSvdPDV;
	}
	public void setNumSvdPDV(String numSvdPDV) {
		this.numSvdPDV = numSvdPDV;
	}

	@XmlAttribute(name = "selected")
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public String getTable() {
		return TABLE;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
