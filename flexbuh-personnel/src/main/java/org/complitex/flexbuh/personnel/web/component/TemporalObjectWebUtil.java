package org.complitex.flexbuh.personnel.web.component;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.PropertyModel;
import org.complitex.flexbuh.common.entity.TemporalDomainObject;

/**
 * @author Pavel Sknar
 *         Date: 16.10.12 12:20
 */
public class TemporalObjectWebUtil {

    private static <T extends TemporalDomainObject> void addOnMouseOver(MarkupContainer form, final TemporalHistoryPanelState<T> state) {
        if (state.getOnmouseover() != null) {
            return;
        }
        state.setOnmouseover(new AjaxEventBehavior("onmouseover") {
            protected void onEvent(final AjaxRequestTarget target) {
                if (!state.isInHistoryContainer() && state.isVisibleCurrentEnabledPanel()) {
                    state.getCurrentEnabledPanel().setVisible(false);
                    target.add(state.getCurrentEnabledPanel());
                }
            }
        });
        form.add(state.getOnmouseover());
    }

    public static <T extends TemporalDomainObject> void addHistoryFieldToForm(MarkupContainer form,
                                                                              HistoryPanelFactory<T> historyPanelFactory,
                                                                              final TemporalHistoryPanelState<T> state,
                                                                              final String fieldName,
                                                                              final Component field) {
        addOnMouseOver(form, state);
        final TemporalHistoryPanel<T> historyPanel = historyPanelFactory.getHistoryPanel(fieldName);
        historyPanel.setOutputMarkupId(true);
        historyPanel.setVisible(false);
        historyPanel.setOutputMarkupPlaceholderTag(true);

        WebMarkupContainer container = new WebMarkupContainer(fieldName + "_container");

        container.add(new AjaxEventBehavior("onmouseover") {
            protected void onEvent(final AjaxRequestTarget target) {
                //log.debug("mouse on container");
                if (!state.isInHistoryContainer()) {
                    //log.debug("mouse on container first");
                    if (state.isNotCurrentEnabledPanel(historyPanel)) {
                        state.getCurrentEnabledPanel().setVisible(false);
                        target.add(state.getCurrentEnabledPanel());
                    }
                    historyPanel.setVisible(true);
                    state.setCurrentEnabledPanel(historyPanel);
                    target.add(historyPanel);
                }
                state.setInHistoryContainer(true);
            }
        }).add(new AjaxEventBehavior("onmouseout") {
            protected void onEvent(final AjaxRequestTarget target) {
                state.setInHistoryContainer(false);
            }
        });

        container.add(field);
        container.add(historyPanel);
        form.add(container);

        field.add(new AttributeModifier("class", "") {
            @Override
            protected String newValue(String currentValue, String replacementValue) {
                if (state.getOldObject() == null) {
                    return "";
                }
                return StringUtils.equals(getValueField(state.getOldObject(), field.getId()),
                        getValueField(state.getObject(), field.getId())) ? "" : "edited";
            }
        });
    }

    private static String getValueField(Object object, String fieldName) {
        PropertyModel<Object> result = new PropertyModel<>(object, fieldName);
        return result.getObject() != null? result.getObject().toString(): null;
    }
}
