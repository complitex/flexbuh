package org.complitex.flexbuh.personnel.entity;

import org.complitex.flexbuh.common.entity.Address;
import org.complitex.flexbuh.common.entity.TemporalDomainObject;

import java.util.Date;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.12.11 14:59
 */
public class Personnel extends TemporalDomainObject {
    private String lastName;
    private String firstName;
    private String middleName;

    private Date birthday;

    private boolean gender;
    private String maritalStatus;

    private String email;
    private String phone;

    // Адрес прописки
    private Address registrationAddress;

    // Адрес фактического проживания
    private String actualAddress;

    // Медицинский полис
    private String medicalPolicy;

    // ИНН
    private String inn;

    // СНИЛС
    private String snils;

    // Паспорт
    private String passport;

    // Резидент/нерезидент
    private boolean resident;

    // Дата приема на работу
    private Date employmentDate;

    // Дата увольнения
    private Date terminationDate;

    // Должность
    private Position position;

    // График работы
    private Schedule schedule;

    // Персональные надбавки
    private List<Allowance> personalAllowances;

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Address getRegistrationAddress() {
        return registrationAddress;
    }

    public void setRegistrationAddress(Address registrationAddress) {
        this.registrationAddress = registrationAddress;
    }

    public String getActualAddress() {
        return actualAddress;
    }

    public void setActualAddress(String actualAddress) {
        this.actualAddress = actualAddress;
    }

    public String getMedicalPolicy() {
        return medicalPolicy;
    }

    public void setMedicalPolicy(String medicalPolicy) {
        this.medicalPolicy = medicalPolicy;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getSnils() {
        return snils;
    }

    public void setSnils(String snils) {
        this.snils = snils;
    }

    public String getPassport() {
        return passport;
    }

    public void setPassport(String passport) {
        this.passport = passport;
    }

    public boolean isResident() {
        return resident;
    }

    public void setResident(boolean resident) {
        this.resident = resident;
    }

    public Date getEmploymentDate() {
        return employmentDate;
    }

    public void setEmploymentDate(Date employmentDate) {
        this.employmentDate = employmentDate;
    }

    public Date getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public List<Allowance> getPersonalAllowances() {
        return personalAllowances;
    }

    public void setPersonalAllowances(List<Allowance> personalAllowances) {
        this.personalAllowances = personalAllowances;
    }
}
