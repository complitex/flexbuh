package org.complitex.flexbuh.personnel.entity;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.complitex.flexbuh.common.entity.Address;
import org.complitex.flexbuh.common.entity.DomainObject;
import org.complitex.flexbuh.common.entity.organization.OrganizationBase;

/**
 * @author Pavel Sknar
 *         Date: 16.02.12 17:14
 */
public class Organization extends OrganizationBase {

    private String phone;
    private String fax;
    private String email;
    private String httpAddress;

    private Address physicalAddress;

    private Address juridicalAddress;

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

    public Address getPhysicalAddress() {
        return physicalAddress;
    }

    public void setPhysicalAddress(Address physicalAddress) {
        this.physicalAddress = physicalAddress;
    }

    public Address getJuridicalAddress() {
        return juridicalAddress;
    }

    public void setJuridicalAddress(Address juridicalAddress) {
        this.juridicalAddress = juridicalAddress;
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
