package org.complitex.flexbuh.personnel.web.component;

import org.complitex.flexbuh.common.entity.TemporalDomainObject;
import org.complitex.flexbuh.common.web.component.IAjaxUpdate;

/**
 * @author Pavel Sknar
 *         Date: 23.04.12 16:11
 */
public abstract class TemporalDomainObjectUpdate<T extends TemporalDomainObject> implements IAjaxUpdate {

    private T object;

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }
}
