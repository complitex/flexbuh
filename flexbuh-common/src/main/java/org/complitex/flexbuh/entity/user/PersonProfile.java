package org.complitex.flexbuh.entity.user;

import org.complitex.flexbuh.entity.DomainObject;

import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 31.08.11 14:26
 */
public class PersonProfile extends DomainObject {
	public static final String TABLE = "user";

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCodeTIN() {
		return codeTIN;
	}

	public void setCodeTIN(String codeTIN) {
		this.codeTIN = codeTIN;
	}

	public Integer getCodeTaxInspection() {
		return codeTaxInspection;
	}

	public void setCodeTaxInspection(Integer codeTaxInspection) {
		this.codeTaxInspection = codeTaxInspection;
	}

	public String getCodeKVED() {
		return codeKVED;
	}

	public void setCodeKVED(String codeKVED) {
		this.codeKVED = codeKVED;
	}

	public PersonType getPersonType() {
		return personType;
	}

	public void setPersonType(PersonType personType) {
		this.personType = personType;
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

	public String getDirectorFIO() {
		return directorFIO;
	}

	public void setDirectorFIO(String directorFIO) {
		this.directorFIO = directorFIO;
	}

	public String getAccountantFIO() {
		return accountantFIO;
	}

	public void setAccountantFIO(String accountantFIO) {
		this.accountantFIO = accountantFIO;
	}

	public String getDirectorINN() {
		return directorINN;
	}

	public void setDirectorINN(String directorINN) {
		this.directorINN = directorINN;
	}

	public String getAccountantINN() {
		return accountantINN;
	}

	public void setAccountantINN(String accountantINN) {
		this.accountantINN = accountantINN;
	}

	public String getIpn() {
		return ipn;
	}

	public void setIpn(String ipn) {
		this.ipn = ipn;
	}

	public String getNumSvdPDV() {
		return numSvdPDV;
	}

	public void setNumSvdPDV(String numSvdPDV) {
		this.numSvdPDV = numSvdPDV;
	}

	@Override
	public String getTable() {
		return TABLE;
	}
}
