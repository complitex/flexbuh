package org.complitex.flexbuh.personnel.entity;

import org.complitex.flexbuh.common.entity.DomainObject;

import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.12.11 14:58
 */
public class Payment extends DomainObject {

    // Оклад/ставка
    private Float salary;

    // Название валюты
    private String currencyUnit;

    // Надбавки
    private List<Allowance> allowances;
}
