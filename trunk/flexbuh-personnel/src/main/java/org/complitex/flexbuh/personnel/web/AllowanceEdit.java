package org.complitex.flexbuh.personnel.web;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.flexbuh.common.logging.Event;
import org.complitex.flexbuh.common.logging.EventCategory;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.personnel.entity.Allowance;
import org.complitex.flexbuh.personnel.entity.Organization;
import org.complitex.flexbuh.personnel.service.AllowanceBean;
import org.complitex.flexbuh.personnel.service.AllowanceTypeBean;
import org.complitex.flexbuh.personnel.service.OrganizationBean;
import org.complitex.flexbuh.personnel.service.TemporalDomainObjectBean;
import org.complitex.flexbuh.personnel.web.component.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.text.MessageFormat;
import java.util.Date;

import static org.complitex.flexbuh.personnel.web.OrganizationEdit.PARAM_ORGANIZATION_ID;

/**
 * @author Pavel Sknar
 *         Date: 18.10.12 16:31
 */
@AuthorizeInstantiation(SecurityRole.PERSONAL_MANAGER)
public class AllowanceEdit extends TemporalObjectEdit<Allowance> {
    
    private final static Logger log = LoggerFactory.getLogger(AllowanceEdit.class);
    
    public static final String PARAM_ALLOWANCE_ID = "allowance_id";
    
    @EJB
    private AllowanceBean allowanceBean;

    @EJB
    private AllowanceTypeBean allowanceTypeBean;

    @EJB
    private OrganizationBean organizationBean;

    private Allowance allowance = new Allowance();

    private Form<Allowance> form;

    private TemporalHistoryPanel<Allowance> allowanceHistoryPanel;

    private TemporalDomainObjectUpdate<Allowance> historyUpdate;
    
    public AllowanceEdit() {
        init();
    }

    public AllowanceEdit(Organization organization) {
        allowance.setOrganization(organization);
        init();
    }

    public AllowanceEdit(PageParameters pageParameters) {

        Long organizationId = pageParameters.get(PARAM_ORGANIZATION_ID).toOptionalLong();
        if (organizationId != null && organizationId > 0) {
            Organization organization = organizationBean.getTDObject(organizationId);
            if (organization != null) {
                allowance.setOrganization(organization);
                allowance.setSessionId(getSessionId());
                init();
            } else {
                // organization not found
                getSession().error(getString("error_organization_not_found"));
                throw new RestartResponseException(OrganizationList.class);
            }
            return;
        }

        Long id = pageParameters.get(PARAM_ALLOWANCE_ID).toOptionalLong();
        if (id != null) {
            log.debug("Start load allowance");
            allowance = allowanceBean.getTDObject(id);
            log.debug("Loaded allowance: {}", allowance);

            if (allowance != null) {

                if (!allowance.getSessionId().equals(getSessionId())) {
                    getSession().error(getString("error_allowance_permission_denied"));
                    throw new RestartResponseException(AllowanceList.class);
                }

                init();
            } else {
                getSession().error(getString("error_allowance_not_found"));
                throw new RestartResponseException(AllowanceList.class);
            }
            return;
        }

        allowance.setSessionId(getSessionId());

        init();
    }
    
    private void init() {
        add(new Label("title", getString("title")));
        add(new Label("header", getString("title")));

        add(new FeedbackPanel("messages"));
        
        log.debug("Edit allowance: {}", allowance);
        form = new Form<>("form", new CompoundPropertyModel<>(allowance));
        add(form);

        // Button update/create allowance
        form.add(new SaveAllowanceButton("submit"));

        // Button cancel changes and return to organization page or allowance list
        if (allowance.getOrganization() != null) {
            PageParameters parameters = new PageParameters();
            parameters.set(OrganizationEdit.PARAM_ORGANIZATION_ID, allowance.getOrganization().getId());
            parameters.set(PARAM_ALLOWANCE_ID, allowance.getId());
            form.add(new BookmarkablePageLink<OrganizationEdit>("cancel", OrganizationEdit.class, parameters) {
                @Override
                protected CharSequence getURL() {
                    return super.getURL() + "#allowances";
                }
            }.add(new AttributeModifier("value", getString("go_to_organization"))));
        } else {
            form.add(new Link<String>("cancel") {

                @Override
                public void onClick() {
                    setResponsePage(AllowanceList.class);
                }
            }.add(new AttributeModifier("value", getString("cancel"))));
        }
        
        historyUpdate = new TemporalDomainObjectUpdate<Allowance>() {
            @Override
            public void onUpdate(AjaxRequestTarget target) {
                super.onUpdate(target);

                getState().setOldObject(allowance);
                allowance = getObject();
                getState().setObject(allowance);
                form.setModel(new CompoundPropertyModel<>(allowance));

                target.add(form);
                target.add(allowanceHistoryPanel);
            }
        };

        allowanceHistoryPanel = new TemporalHistoryIncrementalPanel<Allowance>("allowance_history", allowance, historyUpdate) {
            @Override
            protected TemporalDomainObjectBean<Allowance> getTDObjectBean() {
                return allowanceBean;
            }
        };
        allowanceHistoryPanel.setOutputMarkupId(true);
        add(allowanceHistoryPanel);

        addHistoryFieldToForm(form, "value", new TextField<>("value"));
        addHistoryFieldToForm(form, "calculation_unit",
                new DropDownChoice<String>("calculationUnit", Allowance.CALCULATION_UNIT) {
                    @Override
                    protected boolean localizeDisplayValues() {
                        return true;
                    }
                });
        addHistoryFieldToForm(form, "type", new AllowanceTypeAutoCompleteTextField("type"));
        form.add(new TextField<>("organization", new Model<>(allowance.getOrganization() != null?
                allowance.getOrganization().getName(): "")));
    }
    
    private class SaveAllowanceButton extends Button {
        public SaveAllowanceButton(String id) {
            super(id);
        }

        @Override
        public void onSubmit() {
            boolean emptyRequiredField = !checkRequiredField(allowance.getType(), "type");
            emptyRequiredField |= !checkRequiredField(allowance.getValue() != null, "value");
            emptyRequiredField |= !checkRequiredField(allowance.getCalculationUnit() != null, "calculationUnit");
            if (emptyRequiredField) {
                return;
            }

            Allowance oldAllowance = allowanceBean.getTDObject(allowance.getId());

            allowance.setEntryIntoForceDate(new Date());

            boolean newObject = allowance.getId() == null;
            allowanceBean.save(allowance);
            info(getString("allowance_saved"));

            if (newObject) {
                log.debug("Создание графика работы", new Event(EventCategory.CREATE, allowance));
            } else {
                log.debug("Редактирование подразделения", new Event(EventCategory.EDIT, oldAllowance, allowance));
            }

        }

        private boolean checkRequiredField(Object value, String fieldName) {
            if (value == null) {
                error(MessageFormat.format(getString("required_field"), getString(fieldName)));
                return false;
            }
            return true;
        }

        @Override
        public boolean isEnabled() {
            return allowance.getCompletionDate() == null && !allowance.isDeleted();
        }
    }

    @Override
    protected HistoryPanelFactory<Allowance> getHistoryPanelFactory() {
        return new HistoryPanelFactory<Allowance>() {
            @Override
            protected Allowance getTDObject() {
                return allowance;
            }

            @Override
            protected TemporalDomainObjectUpdate<Allowance> getTDObjectUpdate() {
                return historyUpdate;
            }

            @Override
            protected TemporalDomainObjectBean<Allowance> getTDObjectBean() {
                return allowanceBean;
            }
        };
    }
}
