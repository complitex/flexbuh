package org.complitex.flexbuh.personnel.entity;

import org.complitex.flexbuh.common.entity.DomainObject;

/**
 * @author Pavel Sknar
 *         Date: 18.10.12 10:20
 */
public class AllowanceType extends DomainObject {
    private String name;

    public AllowanceType() {
    }

    public AllowanceType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
