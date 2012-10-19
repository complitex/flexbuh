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
 *         Date: 19.10.12 10:21
 */
public class AllowanceListAccordionPanel extends Panel {

    private Accordion allowanceAccordion;

    private ClickAjaxBehavior clickBehavior;

    private boolean collapsedPanel = true;

    private AllowanceListPanel allowanceListPanel;

    public AllowanceListAccordionPanel(@NotNull String id) {
        super(id);
        init(null);
    }

    public AllowanceListAccordionPanel(@NotNull String id, Organization organization) {
        super(id);
        init(null);
    }

    public AllowanceListAccordionPanel(@NotNull String id, Organization organization, boolean collapsedPanel) {
        super(id);
        this.collapsedPanel = collapsedPanel;
        init(organization);
    }

    private void init(Organization organization) {
        clickBehavior = new ClickAjaxBehavior(false);

        allowanceAccordion = new Accordion("allowance");
        allowanceAccordion.add(new Label("allowance_title", getString("legend_allowance")).add(clickBehavior));
        allowanceAccordion.setCollapsible(true);
        allowanceAccordion.setClearStyle(true);
        allowanceAccordion.setNavigation(true);
        if (collapsedPanel) {
            allowanceAccordion.setActive(new AccordionActive(false));
        }

        add(allowanceAccordion);

        allowanceListPanel = new AllowanceListPanel("allowance_list", organization);
        allowanceListPanel.setOutputMarkupId(true);

        allowanceAccordion.add(allowanceListPanel);
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
            allowanceAccordion.setActive(null);
        } else if (allowanceAccordion.getActive() == null) {
            allowanceAccordion.setActive(new AccordionActive(false));
        }

        allowanceListPanel.updateState(currentDate, enabled);
    }
}

