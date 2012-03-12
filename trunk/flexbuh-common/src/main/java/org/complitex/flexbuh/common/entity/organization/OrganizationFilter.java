package org.complitex.flexbuh.common.entity.organization;

import org.complitex.flexbuh.common.entity.AbstractFilter;

import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 05.03.12 17:13
 */
public class OrganizationFilter extends AbstractFilter {

    private String name;
    private String type;

    private String phone;
    private String email;

    private String physicalAddress;
    private String juridicalAddress;

    private List<Long> organizationIds;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhysicalAddress() {
        return physicalAddress;
    }

    public void setPhysicalAddress(String physicalAddress) {
        this.physicalAddress = physicalAddress;
    }

    public String getJuridicalAddress() {
        return juridicalAddress;
    }

    public void setJuridicalAddress(String juridicalAddress) {
        this.juridicalAddress = juridicalAddress;
    }

    public List<Long> getOrganizationIds() {
        return organizationIds;
    }

    public void setOrganizationIds(List<Long> organizationIds) {
        this.organizationIds = organizationIds;
    }
}
