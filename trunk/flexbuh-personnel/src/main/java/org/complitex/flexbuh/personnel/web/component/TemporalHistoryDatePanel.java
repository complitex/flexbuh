package org.complitex.flexbuh.personnel.web.component;

import org.apache.commons.lang.time.DateUtils;
import org.complitex.flexbuh.common.entity.TemporalDomainObject;
import org.complitex.flexbuh.personnel.entity.TemporalDomainObjectFilter;
import org.complitex.flexbuh.personnel.service.TemporalDomainObjectBean;

import java.util.Date;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 30.08.12 10:36
 */
public abstract class TemporalHistoryDatePanel<T extends TemporalDomainObject> extends TemporalHistoryPanel<T> {

    public static final Date LAST_IN_HISTORY = null;
    public static final Date START_IN_HISTORY = new Date(0);

    public TemporalHistoryDatePanel(String id, T currentObject, TemporalDomainObjectUpdate<T> update) {
        super(id, currentObject, update);
    }

    @Override
    protected T getTDObjectPreviousInHistory(T object) {
        TemporalDomainObjectFilter filter = getFilter();
        filter.setId(object.getId());
        filter.setCurrentDate(DateUtils.addMilliseconds(object.getEntryIntoForceDate(), -1));
        return getTemporalDomainObject(filter);
    }

    @Override
    protected T getTDObjectNextInHistory(T object) {
        TemporalDomainObjectFilter filter = getFilter();
        filter.setId(object.getId());
        filter.setCurrentDate(DateUtils.addMilliseconds(object.getEntryIntoForceDate(), +1));
        return getTemporalDomainObject(filter);
    }

    @Override
    protected T getTDObjectStartInHistory(T object) {
        TemporalDomainObjectFilter filter = getFilter();
        filter.setId(object.getId());
        filter.setCurrentDate(START_IN_HISTORY);
        return getTemporalDomainObject(filter);
    }

    @Override
    protected T getTDObjectLastInHistory(T object) {
        TemporalDomainObjectFilter filter = getFilter();
        filter.setId(object.getId());
        filter.setCurrentDate(LAST_IN_HISTORY);
        return getTemporalDomainObject(filter);
    }

    protected <F extends TemporalDomainObjectFilter> T getTemporalDomainObject(TemporalDomainObjectFilter filter) {
        List<T> result = getTDObjectBean().getTDOObjects(filter);
        return result.size() != 0? result.get(0): null;
    }

    protected abstract TemporalDomainObjectBean<T> getTDObjectBean();

    protected abstract <F extends TemporalDomainObjectFilter> F getFilter();

}
