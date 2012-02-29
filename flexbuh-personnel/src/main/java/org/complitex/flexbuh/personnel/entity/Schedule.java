package org.complitex.flexbuh.personnel.entity;

import org.complitex.flexbuh.common.entity.DomainObject;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.12.11 14:38
 */
public class Schedule extends DomainObject {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
