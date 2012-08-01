package org.complitex.flexbuh.personnel.entity;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.complitex.flexbuh.common.entity.HierarchicalTemporalDomainObject;
import org.complitex.flexbuh.common.entity.TemporalDomainObjectIterator;

import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 27.02.12 10:22
 */
public class Department extends HierarchicalTemporalDomainObject<Department> {

    // Название
    private String name;

    // Код
    private String code;

    // Организация частью которой является подразделение
    private Organization organization;

    // Вышестоящее подразделение
    private Department masterDepartment;

    // Нижестоящие подразделения
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

    @Override
    public void setChildes(List<Department> childes) {
        childDepartments = childes;
    }

    @Override
    public void setMaster(Department master) {
        masterDepartment = master;
    }

    @Override
    public TemporalDomainObjectIterator<Department> getChildes() {
        return new TemporalDomainObjectIterator<>(childDepartments);
    }

    @Override
    public Department getMaster() {
        return masterDepartment;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}