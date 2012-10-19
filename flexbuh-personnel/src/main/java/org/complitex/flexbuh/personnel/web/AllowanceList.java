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
import org.complitex.flexbuh.personnel.web.component.AllowanceListPanel;

import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 19.10.12 11:45
 */
@AuthorizeInstantiation(SecurityRole.PERSONAL_MANAGER)
public class AllowanceList extends TemplatePage {

    private AllowanceListPanel allowanceListPanel;

    public AllowanceList() {

        add(new Label("title", new ResourceModel("title")));
        add(new FeedbackPanel("messages"));

        AllowanceListPanel allowanceListPanel = getAllowanceListPanel();
        allowanceListPanel.setOutputMarkupId(true);
        allowanceListPanel.invisibleAddButtonInForm();
        allowanceListPanel.invisibleDeleteButtonInForm();
        add(new Form("allowance_list_form").add(allowanceListPanel));
    }

    @Override
    protected List<? extends ToolbarButton> getToolbarButtons(String id) {

        AllowanceListPanel allowanceListPanel = getAllowanceListPanel();

        return Lists.newArrayList(allowanceListPanel.getAddToolbarButton(id), allowanceListPanel.getDeleteToolbarButton(id));
    }

    private AllowanceListPanel getAllowanceListPanel() {
        if (allowanceListPanel == null) {
            allowanceListPanel = new AllowanceListPanel("allowances", getSessionId());
        }
        return allowanceListPanel;
    }
}

