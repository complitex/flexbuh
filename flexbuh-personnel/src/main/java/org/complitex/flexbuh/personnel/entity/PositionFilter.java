package org.complitex.flexbuh.personnel.entity;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.complitex.flexbuh.common.entity.AbstractFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 18.07.12 12:05
 */
public class PositionFilter extends TemporalDomainObjectFilter {

    private static final Logger log = LoggerFactory.getLogger(PositionFilter.class);

    private String name;

    private String code;

    private String description;

    private Long departmentId;

    private Long organizationId;

    private Date entryIntoForceDate;

    private Date completionDate;

    private Float minPaymentSalary;

    private Float maxPaymentSalary;

    private String paymentCurrencyUnit;

    public PositionFilter() {
    }

    public PositionFilter(Department department, Integer count) {
        log.debug("Start init filter: department={}, count={}", department, count);
        if (department != null && department.getId() != null) {
            this.departmentId = department.getId();
            this.entryIntoForceDate = department.getEntryIntoForceDate();
            log.debug("Init filter: departmentId={}, entryIntoForceDate={}", this.departmentId, this.entryIntoForceDate);
        }
        this.count = count;
    }

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

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Date getEntryIntoForceDate() {
        return entryIntoForceDate;
    }

    public void setEntryIntoForceDate(Date entryIntoForceDate) {
        this.entryIntoForceDate = entryIntoForceDate;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getMinPaymentSalary() {
        return minPaymentSalary;
    }

    public void setMinPaymentSalary(Float minPaymentSalary) {
        this.minPaymentSalary = minPaymentSalary;
    }

    public Float getMaxPaymentSalary() {
        return maxPaymentSalary;
    }

    public void setMaxPaymentSalary(Float maxPaymentSalary) {
        this.maxPaymentSalary = maxPaymentSalary;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
