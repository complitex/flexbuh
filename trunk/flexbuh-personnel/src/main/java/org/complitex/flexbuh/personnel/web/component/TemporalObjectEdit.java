package org.complitex.flexbuh.personnel.web.component;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.PropertyModel;
import org.complitex.flexbuh.common.entity.TemporalDomainObject;
import org.complitex.flexbuh.common.logging.Event;
import org.complitex.flexbuh.common.logging.EventCategory;
import org.complitex.flexbuh.common.template.FormTemplatePage;
import org.complitex.flexbuh.personnel.entity.Organization;
import org.complitex.flexbuh.personnel.service.TemporalDomainObjectBean;
import org.complitex.flexbuh.personnel.web.component.TemporalDomainObjectUpdate;
import org.complitex.flexbuh.personnel.web.component.TemporalHistoryPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 25.06.12 15:49
 */
public abstract class TemporalObjectEdit<T extends TemporalDomainObject> extends FormTemplatePage {

    private final static Logger log = LoggerFactory.getLogger(TemporalObjectEdit.class);

    private TemporalHistoryPanelState<T> state = new TemporalHistoryPanelState<>();

    protected void addHistoryFieldToForm(MarkupContainer form, final String fieldName,
                                       final Component field) {
        TemporalObjectWebUtil.addHistoryFieldToForm(form, getHistoryPanelFactory(), state, fieldName, field);
    }

    protected TemporalHistoryPanel<T> getHistoryPanel(final String fieldName) {
        return new TemporalHistoryPanel<T>(fieldName + "_history",
                    getHistoryPanelFactory().getTDObject(), getHistoryPanelFactory().getTDObjectUpdate()) {

            @Override
            protected T getTDObjectPreviousInHistory(T object) {
                return getHistoryPanelFactory().getTDObjectBean().getTDObjectPreviewInHistoryByField(object.getId(),
                        object.getVersion(), fieldName);
            }

            @Override
            protected T getTDObjectNextInHistory(T object) {
                return getHistoryPanelFactory().getTDObjectBean().getTDObjectNextInHistoryByField(object.getId(),
                        object.getVersion(), fieldName);
            }

            @Override
            protected T getTDObjectStartInHistory(T object) {
                return getHistoryPanelFactory().getTDObjectBean().getTDObject(object.getId(), 1);
            }

            @Override
            protected T getTDObjectLastInHistory(T object) {
                return getHistoryPanelFactory().getTDObjectBean().getTDObjectLastInHistory(object.getId());
            }
        };
    }

    protected TemporalHistoryPanelState<T> getState() {
        return state;
    }

    protected abstract HistoryPanelFactory<T> getHistoryPanelFactory();

}
