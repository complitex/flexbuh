package org.complitex.flexbuh.personnel.entity;

import org.complitex.flexbuh.common.entity.DomainObject;

/**
 * @author Pavel Sknar
 *         Date: 16.02.12 17:27
 */
public class OrganizationType extends DomainObject {
    private String name;

    public OrganizationType() {
    }

    public OrganizationType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
