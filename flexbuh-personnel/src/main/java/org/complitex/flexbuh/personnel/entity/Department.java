package org.complitex.flexbuh.personnel.entity;

import org.complitex.flexbuh.common.entity.DomainObject;
import org.complitex.flexbuh.common.entity.organization.Organization;

import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 27.02.12 10:22
 */
public class Department extends DomainObject {

    private String name;

    private String code;

    private Organization organization;

    private Department masterDepartment;

    private List<Department> childDepartments;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Department getMasterDepartment() {
        return masterDepartment;
    }

    public void setMasterDepartment(Department masterDepartment) {
        this.masterDepartment = masterDepartment;
    }

    public List<Department> getChildDepartments() {
        return childDepartments;
    }

    public void setChildDepartments(List<Department> childDepartments) {
        this.childDepartments = childDepartments;
    }
}