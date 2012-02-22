package org.complitex.flexbuh.common.entity.organization;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.complitex.flexbuh.common.entity.DomainObject;

/**
 * @author Pavel Sknar
 *         Date: 16.02.12 17:14
 */
public class Organization extends OrganizationBase {

    private String phone;
    private String fax;
    private String email;
    private String httpAddress;

    private String physicalAddressZipCode;
    private String physicalAddressCountry;
    private String physicalAddressRegion;
    private String physicalAddressArea;
    private String physicalAddressCity;
    private String physicalAddressCityType;
    private String physicalAddressStreet;
    private String physicalAddressStreetType;
    private String physicalAddressBuilding;
    private String physicalAddressApartment;

    private String juridicalAddressZipCode;
    private String juridicalAddressCountry;
    private String juridicalAddressRegion;
    private String juridicalAddressArea;
    private String juridicalAddressCity;
    private String juridicalAddressCityType;
    private String juridicalAddressStreet;
    private String juridicalAddressStreetType;
    private String juridicalAddressBuilding;
    private String juridicalAddressApartment;

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

    public String getHttpAddress() {
        return httpAddress;
    }

    public void setHttpAddress(String httpAddress) {
        this.httpAddress = httpAddress;
    }

    public String getPhysicalAddressZipCode() {
        return physicalAddressZipCode;
    }

    public void setPhysicalAddressZipCode(String physicalAddressZipCode) {
        this.physicalAddressZipCode = physicalAddressZipCode;
    }

    public String getPhysicalAddressCountry() {
        return physicalAddressCountry;
    }

    public void setPhysicalAddressCountry(String physicalAddressCountry) {
        this.physicalAddressCountry = physicalAddressCountry;
    }

    public String getPhysicalAddressRegion() {
        return physicalAddressRegion;
    }

    public void setPhysicalAddressRegion(String physicalAddressRegion) {
        this.physicalAddressRegion = physicalAddressRegion;
    }

    public String getPhysicalAddressArea() {
        return physicalAddressArea;
    }

    public void setPhysicalAddressArea(String physicalAddressArea) {
        this.physicalAddressArea = physicalAddressArea;
    }

    public String getPhysicalAddressCity() {
        return physicalAddressCity;
    }

    public void setPhysicalAddressCity(String physicalAddressCity) {
        this.physicalAddressCity = physicalAddressCity;
    }

    public String getPhysicalAddressCityType() {
        return physicalAddressCityType;
    }

    public void setPhysicalAddressCityType(String physicalAddressCityType) {
        this.physicalAddressCityType = physicalAddressCityType;
    }

    public String getPhysicalAddressStreet() {
        return physicalAddressStreet;
    }

    public void setPhysicalAddressStreet(String physicalAddressStreet) {
        this.physicalAddressStreet = physicalAddressStreet;
    }

    public String getPhysicalAddressStreetType() {
        return physicalAddressStreetType;
    }

    public void setPhysicalAddressStreetType(String physicalAddressStreetType) {
        this.physicalAddressStreetType = physicalAddressStreetType;
    }

    public String getPhysicalAddressBuilding() {
        return physicalAddressBuilding;
    }

    public void setPhysicalAddressBuilding(String physicalAddressBuilding) {
        this.physicalAddressBuilding = physicalAddressBuilding;
    }

    public String getPhysicalAddressApartment() {
        return physicalAddressApartment;
    }

    public void setPhysicalAddressApartment(String physicalAddressApartment) {
        this.physicalAddressApartment = physicalAddressApartment;
    }

    public String getJuridicalAddressZipCode() {
        return juridicalAddressZipCode;
    }

    public void setJuridicalAddressZipCode(String juridicalAddressZipCode) {
        this.juridicalAddressZipCode = juridicalAddressZipCode;
    }

    public String getJuridicalAddressCountry() {
        return juridicalAddressCountry;
    }

    public void setJuridicalAddressCountry(String juridicalAddressCountry) {
        this.juridicalAddressCountry = juridicalAddressCountry;
    }

    public String getJuridicalAddressRegion() {
        return juridicalAddressRegion;
    }

    public void setJuridicalAddressRegion(String juridicalAddressRegion) {
        this.juridicalAddressRegion = juridicalAddressRegion;
    }

    public String getJuridicalAddressArea() {
        return juridicalAddressArea;
    }

    public void setJuridicalAddressArea(String juridicalAddressArea) {
        this.juridicalAddressArea = juridicalAddressArea;
    }

    public String getJuridicalAddressCity() {
        return juridicalAddressCity;
    }

    public void setJuridicalAddressCity(String juridicalAddressCity) {
        this.juridicalAddressCity = juridicalAddressCity;
    }

    public String getJuridicalAddressCityType() {
        return juridicalAddressCityType;
    }

    public void setJuridicalAddressCityType(String juridicalAddressCityType) {
        this.juridicalAddressCityType = juridicalAddressCityType;
    }

    public String getJuridicalAddressStreet() {
        return juridicalAddressStreet;
    }

    public void setJuridicalAddressStreet(String juridicalAddressStreet) {
        this.juridicalAddressStreet = juridicalAddressStreet;
    }

    public String getJuridicalAddressStreetType() {
        return juridicalAddressStreetType;
    }

    public void setJuridicalAddressStreetType(String juridicalAddressStreetType) {
        this.juridicalAddressStreetType = juridicalAddressStreetType;
    }

    public String getJuridicalAddressBuilding() {
        return juridicalAddressBuilding;
    }

    public void setJuridicalAddressBuilding(String juridicalAddressBuilding) {
        this.juridicalAddressBuilding = juridicalAddressBuilding;
    }

    public String getJuridicalAddressApartment() {
        return juridicalAddressApartment;
    }

    public void setJuridicalAddressApartment(String juridicalAddressApartment) {
        this.juridicalAddressApartment = juridicalAddressApartment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || (!(o instanceof OrganizationBase) && getClass() != o.getClass())) return false;

        DomainObject that = (DomainObject) o;

        if (getId() != null ? ! getId().equals(that.getId()) : that.getId() != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
