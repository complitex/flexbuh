package org.complitex.flexbuh.personnel.web.component;

import org.complitex.flexbuh.common.entity.TemporalDomainObject;
import org.complitex.flexbuh.personnel.service.TemporalDomainObjectBean;

import java.io.Serializable;

/**
 * @author Pavel Sknar
 *         Date: 16.10.12 12:48
 */
public abstract class HistoryPanelFactory<T extends TemporalDomainObject> implements Serializable {

    public TemporalHistoryPanel<T> getHistoryPanel(final String fieldName) {
        return new TemporalHistoryPanel<T>(fieldName + "_history",
                    getTDObject(), getTDObjectUpdate()) {

            @Override
            protected T getTDObjectPreviousInHistory(T object) {
                return getTDObjectBean().getTDObjectPreviewInHistoryByField(object.getId(),
                        object.getVersion(), fieldName);
            }

            @Override
            protected T getTDObjectNextInHistory(T object) {
                return getTDObjectBean().getTDObjectNextInHistoryByField(object.getId(),
                        object.getVersion(), fieldName);
            }

            @Override
            protected T getTDObjectStartInHistory(T object) {
                return getTDObjectBean().getTDObject(object.getId(), 1);
            }

            @Override
            protected T getTDObjectLastInHistory(T object) {
                return getTDObjectBean().getTDObjectLastInHistory(object.getId());
            }
        };
    }

    abstract protected T getTDObject();

    abstract protected TemporalDomainObjectUpdate<T> getTDObjectUpdate();

    abstract protected TemporalDomainObjectBean<T> getTDObjectBean();
}
