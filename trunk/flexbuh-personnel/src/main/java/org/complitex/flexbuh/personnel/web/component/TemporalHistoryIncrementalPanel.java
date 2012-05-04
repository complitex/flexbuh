package org.complitex.flexbuh.personnel.web.component;

import org.complitex.flexbuh.common.entity.TemporalDomainObject;

/**
 * @author Pavel Sknar
 *         Date: 27.04.12 18:45
 */
public abstract class TemporalHistoryIncrementalPanel<T extends TemporalDomainObject> extends TemporalHistoryPanel<T> {

    public TemporalHistoryIncrementalPanel(String id, T currentObject, TemporalDomainObjectUpdate<T> update) {
        super(id, currentObject, update);
    }

    @Override
    protected T getTemporalDomainObjectPreviousInHistory(T object) {
        return getTemporalDomainObject(object.getId(), object.getVersion() - 1);
    }

    @Override
    protected T getTemporalDomainObjectNextInHistory(T object) {
        return getTemporalDomainObject(object.getId(), object.getVersion() + 1);
    }

    @Override
    protected T getTemporalDomainObjectStartInHistory(T object) {
        return getTemporalDomainObject(object.getId(), 1);
    }

    protected abstract T getTemporalDomainObject(long id, long version);

}
