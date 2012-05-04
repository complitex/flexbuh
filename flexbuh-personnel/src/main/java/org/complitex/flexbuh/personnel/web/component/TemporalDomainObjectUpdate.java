package org.complitex.flexbuh.personnel.web.component;

import com.google.common.collect.Lists;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.complitex.flexbuh.common.entity.TemporalDomainObject;
import org.complitex.flexbuh.common.web.component.IAjaxUpdate;

import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 23.04.12 16:11
 */
public abstract class TemporalDomainObjectUpdate<T extends TemporalDomainObject> implements IAjaxUpdate {

    private T object;

    private List<TemporalHistoryPanel<T>> panels = Lists.newArrayList();

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public void addToUpdate(TemporalHistoryPanel<T> panel) {
        panels.add(panel);
    }

    @Override
    public void onUpdate(AjaxRequestTarget target) {
        for (TemporalHistoryPanel<T> panel : panels) {
            panel.internalPrepareForUpdate(this);
        }
    }
}
