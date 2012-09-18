package org.complitex.flexbuh.personnel.web.component;

import com.google.common.collect.Maps;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.complitex.flexbuh.common.entity.TemporalDomainObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * @author Pavel Sknar
 *         Date: 23.04.12 13:20
 */
public abstract class TemporalHistoryPanel<T extends TemporalDomainObject> extends Panel {

    private static final Logger log = LoggerFactory.getLogger(TemporalHistoryPanel.class);

    private final static DateFormat HISTORY_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy\u00A0HH:mm:ss");

    private static final String HISTORY_START_BUTTON     = "start";
    private static final String HISTORY_BACK_BUTTON      = "back";
    private static final String HISTORY_FORWARD_BUTTON   = "forward";
    private static final String HISTORY_END_BUTTON       = "end";

    private static final Map<String, String> HTML_CLASSES = Maps.newHashMap();

    static {
        HTML_CLASSES.put(HISTORY_START_BUTTON, "history_button history_start_button");
        HTML_CLASSES.put(HISTORY_BACK_BUTTON, "history_button history_back_button");
        HTML_CLASSES.put(HISTORY_FORWARD_BUTTON, "history_button history_forward_button");
        HTML_CLASSES.put(HISTORY_END_BUTTON, "history_button history_end_button");
    }

    private WebMarkupContainer historyButtonsContainer;

    private IModel<String> entryIntoForceDate;
    private Component startHistoryButton;
    private Component backHistoryButton;
    private Component forwardHistoryButton;
    private Component endHistoryButton;

    protected T start;
    protected T previous;
    protected T next;
    protected T end;

    public TemporalHistoryPanel(String id, T currentObject, TemporalDomainObjectUpdate<T> update) {
        super(id);
        update.addToUpdate(this);
        initProperties(currentObject);

        historyButtonsContainer = new WebMarkupContainer("historyButtonsContainer");
        historyButtonsContainer.setOutputMarkupId(true);

        historyButtonsContainer.setVisible(previous != null || next != null);
        add(historyButtonsContainer);

        entryIntoForceDate = new Model<>(currentObject.getEntryIntoForceDate() != null?
                HISTORY_DATE_FORMAT.format(currentObject.getEntryIntoForceDate()): "");
        historyButtonsContainer.add(new Label("entryIntoForceDate", entryIntoForceDate));

        startHistoryButton = createHistoryButton(HISTORY_START_BUTTON, update);
        backHistoryButton = createHistoryButton(HISTORY_BACK_BUTTON, update);
        forwardHistoryButton = createHistoryButton(HISTORY_FORWARD_BUTTON, update);
        endHistoryButton = createHistoryButton(HISTORY_END_BUTTON, update);

        historyButtonsContainer.add(startHistoryButton);
        historyButtonsContainer.add(backHistoryButton);
        historyButtonsContainer.add(forwardHistoryButton);
        historyButtonsContainer.add(endHistoryButton);
    }

    public void internalPrepareForUpdate(TemporalDomainObjectUpdate<T> update) {

        T object = update.getObject();

        initProperties(object);

        entryIntoForceDate.setObject(HISTORY_DATE_FORMAT.format(object.getEntryIntoForceDate()));

        updateState(start, HTML_CLASSES.get(HISTORY_START_BUTTON), startHistoryButton);

        updateState(previous, HTML_CLASSES.get(HISTORY_BACK_BUTTON), backHistoryButton);

        updateState(next, HTML_CLASSES.get(HISTORY_FORWARD_BUTTON), forwardHistoryButton);

        updateState(end, HTML_CLASSES.get(HISTORY_END_BUTTON), endHistoryButton);
    }

    protected void initProperties(T currentObject) {
        start = getStartModification(currentObject);
        previous = getPreviousModification(currentObject);
        next = getNextModification(currentObject);
        end = getEndModification(currentObject);
    }

    private Component createHistoryButton(final String id, final TemporalDomainObjectUpdate<T> update) {

        return updateState(getButtonObject(id), HTML_CLASSES.get(id), new AjaxLink<Void>(id) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                T object = getButtonObject(id);

                log.debug("button '{}' object: {}", id, object);

                if (object != null) {
                    update.setObject(object);

                    internalPrepareForUpdate(update);

                    target.add(historyButtonsContainer);

                    update.onUpdate(target);
                }
            }
        });
    }

    private T getButtonObject(String id) {
        switch (id) {
            case HISTORY_START_BUTTON:
                return start;
            case HISTORY_BACK_BUTTON:
                return previous;
            case HISTORY_FORWARD_BUTTON:
                return next;
            case HISTORY_END_BUTTON:
                return end;
            default:
                throw new IllegalArgumentException();
        }
    }

    private Component updateState(T object, String htmlClass, Component component) {
        return component.setEnabled(object != null)
                .add(new AttributeModifier("title", object != null ?
                        HISTORY_DATE_FORMAT.format(object.getEntryIntoForceDate()) : ""))
                .add(new AttributeModifier("class", object != null ?
                        htmlClass + "_on": htmlClass + "_off"));
    }

    private T getPreviousModification(T object) {
        return object.getId() != null && object.getVersion() != null && object.getVersion() > 1?
                getTDObjectPreviousInHistory(object): null;
    }

    private T getNextModification(T object) {
        return object.getId() != null && object.getVersion() != null && object.getCompletionDate() != null?
                getTDObjectNextInHistory(object): null;
    }

    private T getStartModification(T object) {
        return object.getId() != null && object.getVersion() != null && object.getVersion() != 1?
                getTDObjectStartInHistory(object): null;
    }

    private T getEndModification(T object) {
        if (object.getId() != null && object.getVersion() != null &&
                (object.getVersion() != 1 || object.getCompletionDate() != null)) {
            T endOrganization = getTDObjectLastInHistory(object);
            return endOrganization.getVersion() > object.getVersion() ? endOrganization: null;
        }
        return null;
    }

    protected abstract T getTDObjectPreviousInHistory(T object);
    protected abstract T getTDObjectNextInHistory(T object);
    protected abstract T getTDObjectStartInHistory(T object);
    protected abstract T getTDObjectLastInHistory(T object);
}
