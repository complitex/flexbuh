package org.complitex.flexbuh.personnel.web.component;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.complitex.flexbuh.personnel.entity.Organization;
import org.odlabs.wiquery.ui.accordion.Accordion;
import org.odlabs.wiquery.ui.accordion.AccordionActive;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 03.10.12 13:04
 */
public class ScheduleListAccordionPanel extends Panel {

    private Accordion scheduleAccordion;

    private ClickAjaxBehavior clickBehavior;

    private boolean collapsedPanel = true;

    private ScheduleListPanel scheduleListPanel;

    public ScheduleListAccordionPanel(@NotNull String id) {
        super(id);
        init(null);
    }

    public ScheduleListAccordionPanel(@NotNull String id, Organization organization) {
        super(id);
        init(null);
    }

    public ScheduleListAccordionPanel(@NotNull String id, Organization organization, boolean collapsedPanel) {
        super(id);
        this.collapsedPanel = collapsedPanel;
        init(organization);
    }

    private void init(Organization organization) {
        clickBehavior = new ClickAjaxBehavior(false);

        scheduleAccordion = new Accordion("schedule");
        scheduleAccordion.add(new Label("schedule_title", getString("legend_schedule")).add(clickBehavior));
        scheduleAccordion.setCollapsible(true);
        scheduleAccordion.setClearStyle(true);
        scheduleAccordion.setNavigation(true);
        if (collapsedPanel) {
            scheduleAccordion.setActive(new AccordionActive(false));
        }

        add(scheduleAccordion);

        scheduleListPanel = new ScheduleListPanel("schedule_list", organization);
        scheduleListPanel.setOutputMarkupId(true);

        scheduleAccordion.add(scheduleListPanel);
    }

    private class ClickAjaxBehavior extends AjaxEventBehavior {

        private boolean opened;

        private ClickAjaxBehavior(boolean opened) {
            super("onclick");
            this.opened = opened;
        }

        @Override
        protected void onEvent(AjaxRequestTarget target) {
            opened = !opened;
        }

        public boolean isOpened() {
            return opened;
        }
    }

    public void updateState(Date currentDate, boolean enabled) {

        if (clickBehavior.isOpened()) {
            scheduleAccordion.setActive(null);
        } else if (scheduleAccordion.getActive() == null) {
            scheduleAccordion.setActive(new AccordionActive(false));
        }

        scheduleListPanel.updateState(currentDate, enabled);
    }
}
