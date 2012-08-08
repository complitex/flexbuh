package org.complitex.flexbuh.personnel.entity;

import com.google.common.collect.Lists;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.complitex.flexbuh.common.entity.TemporalDomainObject;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.12.11 14:58
 */
public class Payment implements Serializable {

    public static final List<String> CURRENCY_UNIT = Lists.newArrayList("RUB", "UAH", "USD", "EUR");

    // Оклад/ставка
    private Float salary;

    // Название валюты
    private String currencyUnit;

    public Float getSalary() {
        return salary;
    }

    public void setSalary(Float salary) {
        this.salary = salary;
    }

    public String getCurrencyUnit() {
        return currencyUnit;
    }

    public void setCurrencyUnit(String currencyUnit) {
        this.currencyUnit = currencyUnit;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
