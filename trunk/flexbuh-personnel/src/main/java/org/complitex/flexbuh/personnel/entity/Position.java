package org.complitex.flexbuh.personnel.entity;

import org.complitex.flexbuh.common.entity.HierarchicalTemporalDomainObject;
import org.complitex.flexbuh.common.entity.TemporalDomainObject;
import org.complitex.flexbuh.common.entity.TemporalDomainObjectIterator;

import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.12.11 14:39
 */
public class Position extends TemporalDomainObject {

    // Название должности (специальности, профессии), разряда, класса (категории) квалификации
    private String name;

    // Код должности
    private String code;

    // Система оплаты труда
    private Payment payment;

    // Описание (должностные обязонности и т.п.)
    private String description;

    // График работы
    private Schedule schedule;

    // Подразделение
    private Department department;

    // Организация
    private Organization organization;

    // Специфические значения параметров подразделения
    private Attributes departmentAttributes;

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

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Attributes getDepartmentAttributes() {
        return departmentAttributes;
    }

    public void setDepartmentAttributes(Attributes departmentAttributes) {
        this.departmentAttributes = departmentAttributes;
    }

    public class Attributes {
        // Система оплаты труда
        private Payment payment;

        // Описание (должностные обязонности и т.п.)
        private String description;

        // График работы
        private Schedule schedule;

        public Payment getPayment() {
            return payment;
        }

        public void setPayment(Payment payment) {
            this.payment = payment;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Schedule getSchedule() {
            return schedule;
        }

        public void setSchedule(Schedule schedule) {
            this.schedule = schedule;
        }
    }
}
