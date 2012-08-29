package org.complitex.flexbuh.personnel.entity;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.complitex.flexbuh.common.entity.HierarchicalTemporalDomainObject;
import org.complitex.flexbuh.common.entity.TemporalDomainObject;
import org.complitex.flexbuh.common.entity.TemporalDomainObjectIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.12.11 14:39
 */
public class Position extends TemporalDomainObject {

    private final static Logger log = LoggerFactory.getLogger(Position.class);

    // Название должности (специальности, профессии), разряда, класса (категории) квалификации
    private String name;

    // Код должности
    private String code;

    // Система оплаты труда
    private Payment payment = new Payment();

    // Описание (должностные обязонности и т.п.)
    private String description;

    // График работы
    private Schedule schedule;

    // Надбавки
    private List<Allowance> allowances;

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
        log.debug("Set department");
        this.department = department;
        if (departmentAttributes == null) {
            departmentAttributes = new Attributes();
        }
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

    public void copyDepartmentAttributes(Position departmentPosition) {
        log.debug("copy department attributes: {}", departmentPosition);
        departmentAttributes = new Attributes();
        departmentAttributes.setPayment(departmentPosition.getPayment());
        departmentAttributes.setDescription(departmentPosition.getDescription());
        departmentAttributes.setSchedule(departmentPosition.getSchedule());
        departmentAttributes.setVersion(departmentPosition.getVersion());
        log.debug("result: {}", departmentAttributes);
    }

    public class Attributes extends TemporalDomainObject {
        // Система оплаты труда
        private Payment payment = new Payment();

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

        public boolean isNew() {
            return getVersion() == null;
        }

        public boolean isNotNew() {
            return getVersion() != null;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(Attributes.this);
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
