package org.complitex.flexbuh.personnel.entity;

import org.complitex.flexbuh.common.annotation.Display;
import org.complitex.flexbuh.common.entity.AbstractTemporalEntity;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.12.11 16:35
 */
public class Department extends AbstractTemporalEntity{
    @Display(order = 1)
    private String name;

    @Display(order = 2)
    private Long parentId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}
