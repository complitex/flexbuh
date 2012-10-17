package org.complitex.flexbuh.personnel.web.component;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.complitex.flexbuh.common.entity.TemporalDomainObject;

import java.io.Serializable;

/**
 * @author Pavel Sknar
 *         Date: 16.10.12 13:06
 */
public class TemporalHistoryPanelState<T extends TemporalDomainObject> implements Serializable {
    private TemporalHistoryPanel<T> currentEnabledPanel;

    private boolean inHistoryContainer = false;

    private AjaxEventBehavior onmouseover;

    private Object object;

    private Object oldObject;

    public TemporalHistoryPanel<T> getCurrentEnabledPanel() {
        return currentEnabledPanel;
    }

    public void setCurrentEnabledPanel(TemporalHistoryPanel<T> currentEnabledPanel) {
        this.currentEnabledPanel = currentEnabledPanel;
    }

    public boolean isInHistoryContainer() {
        return inHistoryContainer;
    }

    public void setInHistoryContainer(boolean inHistoryContainer) {
        this.inHistoryContainer = inHistoryContainer;
    }

    public AjaxEventBehavior getOnmouseover() {
        return onmouseover;
    }

    public void setOnmouseover(AjaxEventBehavior onmouseover) {
        this.onmouseover = onmouseover;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Object getOldObject() {
        return oldObject;
    }

    public void setOldObject(Object oldObject) {
        this.oldObject = oldObject;
    }

    public boolean isNotCurrentEnabledPanel(TemporalHistoryPanel historyPanel) {
        return currentEnabledPanel != null && !currentEnabledPanel.equals(historyPanel);
    }

    public boolean isVisibleCurrentEnabledPanel() {
        return currentEnabledPanel != null && currentEnabledPanel.isVisible();
    }
}
