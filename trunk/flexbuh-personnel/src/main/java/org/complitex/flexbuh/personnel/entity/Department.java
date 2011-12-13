package org.complitex.flexbuh.personnel.entity;

import org.complitex.flexbuh.common.entity.AbstractTemporalEntity;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.12.11 16:35
 */
public class Department extends AbstractTemporalEntity{
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
