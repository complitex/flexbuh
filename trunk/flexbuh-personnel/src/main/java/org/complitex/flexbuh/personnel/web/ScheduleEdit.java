package org.complitex.flexbuh.personnel.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.NotImplementedException;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.yui.calendar.TimeField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.Loop;
import org.apache.wicket.markup.html.list.LoopItem;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.flexbuh.common.logging.Event;
import org.complitex.flexbuh.common.logging.EventCategory;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.common.util.DateUtil;
import org.complitex.flexbuh.personnel.entity.Organization;
import org.complitex.flexbuh.personnel.entity.Schedule;
import org.complitex.flexbuh.personnel.entity.WorkTime;
import org.complitex.flexbuh.personnel.service.OrganizationBean;
import org.complitex.flexbuh.personnel.service.ScheduleBean;
import org.complitex.flexbuh.personnel.service.TemporalDomainObjectBean;
import org.complitex.flexbuh.personnel.web.component.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.io.Serializable;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.complitex.flexbuh.personnel.web.OrganizationEdit.PARAM_ORGANIZATION_ID;

/**
 * @author Pavel Sknar
 *         Date: 03.10.12 11:19
 */

@AuthorizeInstantiation(SecurityRole.PERSONAL_MANAGER)
public class ScheduleEdit extends TemporalObjectEdit<Schedule> {

    private final static Logger log = LoggerFactory.getLogger(ScheduleEdit.class);

    public static final String PARAM_SCHEDULE_ID = "schedule_id";

    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    @EJB
    private ScheduleBean scheduleBean;

    @EJB
    private OrganizationBean organizationBean;

    private Schedule schedule = new Schedule();

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
        if (organizationId != null && organizationId > 0) {
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

        if (schedule.getPeriodSchedule() != null)

        log.debug("Edit schedule: {}", schedule);
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

        final Component periodSchedule = new Loop("period_schedule_list", new PropertyModel<Integer>(schedule, "periodNumberDate") {
            @Override
            public Integer getObject() {
                Integer object = super.getObject();
                return object == null? 0: object;
            }
        }) {
            @Override
            protected void populateItem(final LoopItem item) {
                log.debug("populate: {}", item.getIndex());

                final List<List<WorkTime>> periodScheduleTime = schedule.getPeriodScheduleTime();

                final List<WorkTime> workTimes;
                if (periodScheduleTime.size() < item.getIndex() + 1) {
                    workTimes = Lists.newArrayList(getNullWorkTime());
                    periodScheduleTime.add(workTimes);
                } else {
                    workTimes = periodScheduleTime.get(item.getIndex());
                    if (workTimes.size() == 0) {
                        workTimes.add(getNullWorkTime());
                    }
                }

                final WebMarkupContainer listContainer = new WebMarkupContainer("dateSchedule");
                listContainer.setOutputMarkupId(true);
                final Loop dateSchedule = new Loop("date_schedule_list", workTimes.size()) {
                    @Override
                    protected void populateItem(final LoopItem item2) {
                        WorkTime workTime = workTimes.get(item2.getIndex());
                        item2.add(new Label("index", item2.getIndex() == 0? Integer.toString(item.getIndex() + 1) + ".": ""));
                        item2.add(new TimeField("begin_time", new PropertyModel<Date>(workTime, "beginTime")) {
                            @Override
                            protected boolean use12HourFormat() {
                                return false;
                            }
                        });
                        item2.add(new TimeField("end_time", new PropertyModel<Date>(workTime, "endTime")) {
                            @Override
                            protected boolean use12HourFormat() {
                                return false;
                            }
                        });
                        item2.add(new AjaxLink("action_add") {
                            @Override
                            public void onClick(AjaxRequestTarget target) {
                                workTimes.add(item2.getIndex() + 1, getNullWorkTime());
                                setDefaultModelObject2(workTimes.size());
                                target.add(listContainer);
                            }
                        }.add(new Label("action_label", getString("add"))));
                        item2.add(new AjaxLink("action_delete") {
                            @Override
                            public void onClick(AjaxRequestTarget target) {
                                workTimes.remove(item2.getIndex());
                                if (workTimes.size() == 0) {
                                    workTimes.add(getNullWorkTime());
                                }
                                setDefaultModelObject2(workTimes.size());
                                target.add(listContainer);
                            }
                        }.add(new Label("action_label", getString("delete"))));
                    }

                    private void setDefaultModelObject2(int value) {
                        setDefaultModelObject(value);
                    }
                };
                listContainer.add(dateSchedule);

                item.add(listContainer);
            }
        }.setOutputMarkupId(true);
        final WebMarkupContainer listContainer = new WebMarkupContainer("periodSchedule");
        listContainer.setOutputMarkupId(true);
        listContainer.add(periodSchedule);

        historyUpdate = new TemporalDomainObjectUpdate<Schedule>() {
            @Override
            public void onUpdate(AjaxRequestTarget target) {
                super.onUpdate(target);

                getState().setOldObject(schedule);
                schedule = getObject();
                getState().setObject(schedule);
                form.setModel(new CompoundPropertyModel<>(schedule));
                periodSchedule.setDefaultModel(new PropertyModel<Integer>(schedule, "periodNumberDate") {
                    @Override
                    public Integer getObject() {
                        Integer object = super.getObject();
                        return object == null? 0: object;
                    }
                });

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
        addHistoryFieldToForm(form, "reg_work_time_unit",
                new DropDownChoice<String>("regWorkTimeUnit", Schedule.REG_WORK_TIME_UNITS) {
                    @Override
                    protected boolean localizeDisplayValues() {
                        return true;
                    }
                });
        addHistoryFieldToForm(form, "total_work_time", new CheckBox("totalWorkTime"));
        addHistoryFieldToForm(form, "period_number_date",
                new DropDownChoice<>("periodNumberDate", Lists.newArrayList(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14)).
                        add(new AjaxFormComponentUpdatingBehavior("onchange") {
                            protected void onUpdate(AjaxRequestTarget target) {
                                log.debug("changed periodNumberDate: {}", schedule.getPeriodNumberDate());
                                target.add(listContainer);
                            }
                        }));
        addHistoryFieldToForm(form, "period_schedule", listContainer);
        form.add(new TextField<>("organization", new Model<>(schedule.getOrganization() != null?
                schedule.getOrganization().getName(): "")));

        /*
        Заготовка для модального окна: редактирование годового графика
        final ModalWindow modal1;
        add(modal1 = new ModalWindow("modal1"));

        modal1.setCookieName("modal-1");

        modal1.setPageCreator(new ModalWindow.PageCreator() {
            public Page createPage() {
                return new YearSchedulePage(ScheduleEdit.this.getPageReference(), modal1);
            }
        });
        modal1.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
            public void onClose(AjaxRequestTarget target) {
            }
        });
        modal1.setCloseButtonCallback(new ModalWindow.CloseButtonCallback() {
            public boolean onCloseButtonClicked(AjaxRequestTarget target) {
                return true;
            }
        });

        add(new AjaxLink<Void>("showModal1") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                modal1.show(target);
            }
        });
        */
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
            for (List<WorkTime> workTimeList : schedule.getPeriodScheduleTime()) {
                Date endTime = null;
                for (WorkTime workTime : workTimeList) {
                    if (workTime.isEmpty()) {
                        continue;
                    }
                    if (!workTime.checkTime()) {
                        error(MessageFormat.format(getString("error_schedule_failed_range_time"),
                                timeFormat.format(workTime.getBeginTime()),
                                timeFormat.format(workTime.getEndTime())));
                        return;
                    }
                    if (endTime != null && endTime.after(workTime.getBeginTime())) {
                        error(MessageFormat.format(getString("error_schedule_failed_preview_range_time"),
                                timeFormat.format(workTime.getBeginTime()),
                                timeFormat.format(endTime)));
                        return;
                    }
                    endTime = workTime.getEndTime();
                }
            }

            Schedule oldSchedule = scheduleBean.getTDObject(schedule.getId());

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

    private WorkTime getNullWorkTime() {
        WorkTime workTime = new WorkTime();
        workTime.setBeginTime(DateUtil.getBeginOfDay(new Date()));
        workTime.setEndTime(DateUtil.getBeginOfDay(new Date()));
        return workTime;
    }

    @Override
    protected HistoryPanelFactory<Schedule> getHistoryPanelFactory() {
        return new HistoryPanelFactory<Schedule>() {
            @Override
            protected Schedule getTDObject() {
                return schedule;
            }

            @Override
            protected TemporalDomainObjectUpdate<Schedule> getTDObjectUpdate() {
                return historyUpdate;
            }

            @Override
            protected TemporalDomainObjectBean<Schedule> getTDObjectBean() {
                return scheduleBean;
            }
        };
    }

    private boolean isAdmin() {
        return getTemplateWebApplication().hasAnyRole(SecurityRole.ADMIN_MODULE_EDIT);
    }

    private boolean isNotAdmin() {
        return !isAdmin();
    }
}
