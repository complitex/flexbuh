package org.complitex.flexbuh.personnel.web;

import org.apache.commons.lang.NotImplementedException;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.flexbuh.common.logging.Event;
import org.complitex.flexbuh.common.logging.EventCategory;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.personnel.entity.Organization;
import org.complitex.flexbuh.personnel.entity.Schedule;
import org.complitex.flexbuh.personnel.service.OrganizationBean;
import org.complitex.flexbuh.personnel.service.ScheduleBean;
import org.complitex.flexbuh.personnel.service.TemporalDomainObjectBean;
import org.complitex.flexbuh.personnel.web.component.TemporalDomainObjectUpdate;
import org.complitex.flexbuh.personnel.web.component.TemporalHistoryIncrementalPanel;
import org.complitex.flexbuh.personnel.web.component.TemporalHistoryPanel;
import org.complitex.flexbuh.personnel.web.component.TemporalObjectEdit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.text.MessageFormat;
import java.util.Date;

import static org.complitex.flexbuh.personnel.web.OrganizationEdit.PARAM_ORGANIZATION_ID;

/**
 * @author Pavel Sknar
 *         Date: 03.10.12 11:19
 */

@AuthorizeInstantiation(SecurityRole.PERSONAL_MANAGER)
public class ScheduleEdit extends TemporalObjectEdit<Schedule> {

    private final static Logger log = LoggerFactory.getLogger(ScheduleEdit.class);

    public static final String PARAM_SCHEDULE_ID = "schedule_id";

    @EJB
    private ScheduleBean scheduleBean;

    @EJB
    private OrganizationBean organizationBean;

    private Schedule schedule = new Schedule();

    private Schedule oldSchedule;

    private Form<Schedule> form;

    private TemporalHistoryPanel<Schedule> scheduleHistoryPanel;

    private TemporalDomainObjectUpdate<Schedule> historyUpdate;

    public ScheduleEdit() {
        init();
    }

    public ScheduleEdit(Organization organization) {
        schedule.setOrganization(organization);
        init();
    }

    public ScheduleEdit(PageParameters pageParameters) {

        Long organizationId = pageParameters.get(PARAM_ORGANIZATION_ID).toOptionalLong();
        if (organizationId != null) {
            Organization organization = organizationBean.getTDObject(organizationId);
            if (organization != null) {
                schedule.setOrganization(organization);
                schedule.setSessionId(getSessionId());
                init();
            } else {
                // organization not found
                getSession().error(getString("error_organization_not_found"));
                throw new RestartResponseException(OrganizationList.class);
            }
            return;
        }

        Long id = pageParameters.get(PARAM_SCHEDULE_ID).toOptionalLong();
        if (id != null) {
            log.debug("Start load schedule");
            schedule = scheduleBean.getTDObject(id);
            log.debug("Loaded schedule: {}", schedule);

            if (schedule != null) {

                if (isNotAdmin() && !schedule.getSessionId().equals(getSessionId())) {
                    getSession().error(getString("error_schedule_permission_denied"));
                    throw new RestartResponseException(ScheduleList.class);
                }

                init();
            } else {
                getSession().error(getString("error_schedule_not_found"));
                throw new RestartResponseException(ScheduleList.class);
            }
            return;
        }

        if (isNotAdmin()) {
            schedule.setSessionId(getSessionId());
        }
        init();
    }

    private void init() {
        add(new Label("title", getString("title")));
        add(new Label("header", getString("title")));

        add(new FeedbackPanel("messages"));

        form = new Form<>("form", new CompoundPropertyModel<>(schedule));
        add(form);

        // Button update/create schedule
        form.add(new SaveDepartmentButton("submit"));

        // Button cancel changes and return to organization page or schedule list
        form.add(new Link<String>("cancel") {

            @Override
            public void onClick() {
                PageParameters parameters = new PageParameters();
                if (schedule.getOrganization() != null) {
                    parameters.set(PARAM_ORGANIZATION_ID, schedule.getOrganization().getId());
                    setResponsePage(OrganizationEdit.class, parameters);
                    return;
                }
                setResponsePage(ScheduleList.class);
            }
        }.add(new AttributeModifier("value", schedule.getOrganization() == null || schedule.getId() == null ?
                getString("cancel") : getString("go_to_organization"))));

        historyUpdate = new TemporalDomainObjectUpdate<Schedule>() {
            @Override
            public void onUpdate(AjaxRequestTarget target) {
                super.onUpdate(target);

                oldSchedule = schedule;
                schedule = getObject();
                form.setModel(new CompoundPropertyModel<>(schedule));

                target.add(form);
                target.add(scheduleHistoryPanel);
            }
        };

        scheduleHistoryPanel = new TemporalHistoryIncrementalPanel<Schedule>("schedule_history", schedule, historyUpdate) {
            @Override
            protected TemporalDomainObjectBean<Schedule> getTDObjectBean() {
                return scheduleBean;
            }
        };
        scheduleHistoryPanel.setOutputMarkupId(true);
        add(scheduleHistoryPanel);

        addHistoryFieldToForm(form, "name", new TextField<>("name"));
        addHistoryFieldToForm(form, "comment", new TextArea<>("comment"));
        form.add(new TextField<>("organization", new Model<>(schedule.getOrganization() != null?
                schedule.getOrganization().getName(): "")));
    }

    private class SaveDepartmentButton extends Button {
        public SaveDepartmentButton(String id) {
            super(id);
        }

        @Override
        public void onSubmit() {
            boolean emptyRequiredField = !checkRequiredField(schedule.getName(), "name");
            if (emptyRequiredField) {
                return;
            }

            schedule.setEntryIntoForceDate(new Date());

            boolean newObject = schedule.getId() == null;
            scheduleBean.save(schedule);
            info(getString("schedule_saved"));

            if (newObject) {
                log.debug("Создание графика работы", new Event(EventCategory.CREATE, schedule));
            } else {
                log.debug("Редактирование подразделения", new Event(EventCategory.EDIT, oldSchedule, schedule));
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
            return schedule.getCompletionDate() == null && !schedule.isDeleted();
        }
    }

    @Override
    protected Schedule getTDObject() {
        return schedule;
    }

    @Override
    protected Schedule getOldTDObject() {
        return oldSchedule;
    }

    @Override
    protected TemporalDomainObjectUpdate<Schedule> getTDObjectUpdate() {
        return historyUpdate;
    }

    @Override
    protected TemporalDomainObjectBean<Schedule> getTDObjectBean() {
        return scheduleBean;
    }

    private boolean isAdmin() {
        return getTemplateWebApplication().hasAnyRole(SecurityRole.ADMIN_MODULE_EDIT);
    }

    private boolean isNotAdmin() {
        return !isAdmin();
    }
}
