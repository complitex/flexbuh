package org.complitex.flexbuh.personnel.entity;

import org.complitex.flexbuh.common.entity.AbstractTemporalEntity;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.12.11 14:58
 */
public class Payment extends AbstractTemporalEntity{
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
