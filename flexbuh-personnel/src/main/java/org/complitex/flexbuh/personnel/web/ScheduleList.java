package org.complitex.flexbuh.personnel.web;

import com.google.common.collect.Lists;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.ResourceModel;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.common.template.TemplatePage;
import org.complitex.flexbuh.common.template.toolbar.ToolbarButton;
import org.complitex.flexbuh.personnel.web.component.ScheduleListPanel;

import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 04.10.12 11:05
 */
@AuthorizeInstantiation(SecurityRole.PERSONAL_MANAGER)
public class ScheduleList extends TemplatePage {

    private ScheduleListPanel scheduleListPanel;

    public ScheduleList() {

        add(new Label("title", new ResourceModel("title")));
        add(new FeedbackPanel("messages"));

        ScheduleListPanel scheduleListPanel = getScheduleListPanel();
        scheduleListPanel.setOutputMarkupId(true);
        scheduleListPanel.invisibleAddButtonInForm();
        scheduleListPanel.invisibleDeleteButtonInForm();
        add(new Form("schedule_list_form").add(scheduleListPanel));
    }

    @Override
    protected List<? extends ToolbarButton> getToolbarButtons(String id) {

        ScheduleListPanel scheduleListPanel = getScheduleListPanel();

        return Lists.newArrayList(scheduleListPanel.getAddToolbarButton(id), scheduleListPanel.getDeleteToolbarButton(id));
    }

    private ScheduleListPanel getScheduleListPanel() {
        if (scheduleListPanel == null) {
            scheduleListPanel = new ScheduleListPanel("schedules", isAdmin(), getSessionId());
        }
        return scheduleListPanel;
    }

    private boolean isAdmin() {
        return getTemplateWebApplication().hasAnyRole(SecurityRole.ADMIN_MODULE_EDIT);
    }
}
