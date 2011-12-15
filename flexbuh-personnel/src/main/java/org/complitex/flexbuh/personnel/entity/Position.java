package org.complitex.flexbuh.personnel.entity;

import org.complitex.flexbuh.common.entity.AbstractTemporalEntity;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.12.11 14:39
 */
public class Position extends AbstractTemporalEntity{
    private String name;
    private Integer rateCount;
    private Long rateType;
    private Float minSalary;
    private Float maxSalary;
    private Long scheduleId;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRateCount() {
        return rateCount;
    }

    public void setRateCount(Integer rateCount) {
        this.rateCount = rateCount;
    }

    public Long getRateType() {
        return rateType;
    }

    public void setRateType(Long rateType) {
        this.rateType = rateType;
    }

    public Float getMinSalary() {
        return minSalary;
    }

    public void setMinSalary(Float minSalary) {
        this.minSalary = minSalary;
    }

    public Float getMaxSalary() {
        return maxSalary;
    }

    public void setMaxSalary(Float maxSalary) {
        this.maxSalary = maxSalary;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
