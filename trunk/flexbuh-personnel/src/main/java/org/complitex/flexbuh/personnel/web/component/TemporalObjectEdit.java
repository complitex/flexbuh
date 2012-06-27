package org.complitex.flexbuh.personnel.web.component;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
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

    private TemporalHistoryPanel<T> currentEnabledPanel;

    private boolean inHistoryContainer = false;

    private AjaxEventBehavior onmouseover;

    private void addOnMouseOver(Form<T> form) {
        if (onmouseover != null) {
            return;
        }
        onmouseover = new AjaxEventBehavior("onmouseover") {
            protected void onEvent(final AjaxRequestTarget target) {
                if (!inHistoryContainer && currentEnabledPanel != null && currentEnabledPanel.isVisible()) {
                    currentEnabledPanel.setVisible(false);
                    target.add(currentEnabledPanel);
                }
            }
        };
        form.add(onmouseover);
    }

    protected void addHistoryFieldToForm(Form<T> form, final String fieldName,
                                       final Component field) {
        addOnMouseOver(form);
        final TemporalHistoryPanel<T> historyPanel =
            new TemporalHistoryPanel<T>(fieldName + "_history",
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
        historyPanel.setOutputMarkupId(true);
        historyPanel.setVisible(false);
        historyPanel.setOutputMarkupPlaceholderTag(true);

        WebMarkupContainer container = new WebMarkupContainer(fieldName + "_container");

        container.add(new AjaxEventBehavior("onmouseover") {
            protected void onEvent(final AjaxRequestTarget target) {
                //log.debug("mouse on container");
                if (!inHistoryContainer) {
                    //log.debug("mouse on container first");
                    if (currentEnabledPanel != null && !currentEnabledPanel.equals(historyPanel)) {
                        currentEnabledPanel.setVisible(false);
                        target.add(currentEnabledPanel);
                    }
                    historyPanel.setVisible(true);
                    currentEnabledPanel = historyPanel;
                    target.add(historyPanel);
                }
                inHistoryContainer = true;
            }
        }).add(new AjaxEventBehavior("onmouseout") {
            protected void onEvent(final AjaxRequestTarget target) {
                inHistoryContainer = false;
            }
        });

        container.add(field);
        container.add(historyPanel);
        form.add(container);

        field.add(new AttributeModifier("class", "") {
            @Override
            protected String newValue(String currentValue, String replacementValue) {
                if (getOldTDObject() == null) {
                    return "";
                }
                PropertyModel<String> propertyModel1 = new PropertyModel<>(getOldTDObject(), field.getId());
                PropertyModel<String> propertyModel2 = new PropertyModel<>(getTDObject(), field.getId());
                return StringUtils.equals(propertyModel1.getObject(), propertyModel2.getObject()) ? "" : "edited";
            }
        });

        //form.add(field);
        //form.add(historyPanel);
    }

    abstract protected T getTDObject();

    abstract protected T getOldTDObject();

    abstract protected TemporalDomainObjectUpdate<T> getTDObjectUpdate();

    abstract protected TemporalDomainObjectBean<T> getTDObjectBean();
}
