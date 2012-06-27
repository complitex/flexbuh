package org.complitex.flexbuh.personnel.web.component;

import org.complitex.flexbuh.common.entity.TemporalDomainObject;
import org.complitex.flexbuh.personnel.entity.Department;
import org.complitex.flexbuh.personnel.service.TemporalDomainObjectBean;

/**
 * @author Pavel Sknar
 *         Date: 27.04.12 18:45
 */
public abstract class TemporalHistoryIncrementalPanel<T extends TemporalDomainObject> extends TemporalHistoryPanel<T> {

    public TemporalHistoryIncrementalPanel(String id, T currentObject, TemporalDomainObjectUpdate<T> update) {
        super(id, currentObject, update);
    }

    @Override
    protected T getTDObjectPreviousInHistory(T object) {
        return getTemporalDomainObject(object.getId(), object.getVersion() - 1);
    }

    @Override
    protected T getTDObjectNextInHistory(T object) {
        return getTemporalDomainObject(object.getId(), object.getVersion() + 1);
    }

    @Override
    protected T getTDObjectStartInHistory(T object) {
        return getTemporalDomainObject(object.getId(), 1);
    }

    @Override
    protected T getTDObjectLastInHistory(T object) {
        return getTDObjectBean().getTDObjectLastInHistory(object.getId());
    }

    protected T getTemporalDomainObject(long id, long version) {
        return getTDObjectBean().getTDObject(id, version);
    }

    protected abstract TemporalDomainObjectBean<T> getTDObjectBean();

}
