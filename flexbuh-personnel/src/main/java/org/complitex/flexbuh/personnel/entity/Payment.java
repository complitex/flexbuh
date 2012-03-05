package org.complitex.flexbuh.personnel.entity;

import org.complitex.flexbuh.common.entity.TemporalDomainObject;

import java.util.Date;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.12.11 14:58
 */
public class Payment extends TemporalDomainObject {

    // Оклад/ставка
    private Float salary;

    // Название валюты
    private String currencyUnit;

    // Надбавки
    private List<Allowance> allowances;

    // Дата вступления в силу
    private Date startDate;
}
