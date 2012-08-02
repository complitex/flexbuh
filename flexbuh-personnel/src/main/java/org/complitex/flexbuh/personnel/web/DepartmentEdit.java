package org.complitex.flexbuh.personnel.web;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.flexbuh.common.logging.Event;
import org.complitex.flexbuh.common.logging.EventCategory;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.personnel.entity.Department;
import org.complitex.flexbuh.personnel.entity.Organization;
import org.complitex.flexbuh.personnel.service.DepartmentBean;
import org.complitex.flexbuh.personnel.service.OrganizationBean;
import org.complitex.flexbuh.personnel.service.TemporalDomainObjectBean;
import org.complitex.flexbuh.personnel.web.component.DepartmentTreePanel;
import org.complitex.flexbuh.personnel.web.component.TemporalDomainObjectUpdate;
import org.complitex.flexbuh.personnel.web.component.TemporalHistoryIncrementalPanel;
import org.complitex.flexbuh.personnel.web.component.TemporalHistoryPanel;
import org.complitex.flexbuh.personnel.web.component.TemporalObjectEdit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.text.MessageFormat;
import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 26.03.12 18:48
 */
@AuthorizeInstantiation(SecurityRole.PERSONAL_MANAGER)
public class DepartmentEdit extends TemporalObjectEdit<Department> {

    private final static Logger log = LoggerFactory.getLogger(DepartmentEdit.class);

    public final static String PARAM_MASTER_DEPARTMENT_ID = "master_department_id";
    public final static String PARAM_DEPARTMENT_ID = "department_id";

    private final static String FORM_DATE_FORMAT = "dd.MM.yyyy";

    @EJB
    private DepartmentBean departmentBean;

    @EJB
    private OrganizationBean organizationBean;

    private Department department;

    private Department oldDepartment = null;

    private DepartmentTreePanel panel;

    private Form<Department> form;

    private TemporalHistoryIncrementalPanel<Department> departmentHistoryPanel;

    private TemporalDomainObjectUpdate<Department> historyUpdate;

    private DepartmentEdit() {
    }

    /**
     * Constructor used to create new department
     *
     * @param masterDepartment Master department
     */
    public DepartmentEdit(Department masterDepartment) {
        this.department = new Department();
        this.department.setOrganization(masterDepartment.getOrganization());
        this.department.setMasterDepartment(masterDepartment);
        init();
    }

    /**
     * Constructor used to create new department
     *
     * @param organization Organization
     */
    public DepartmentEdit(Organization organization) {
        department = new Department();
        department.setOrganization(organization);
        init();
    }

    /**
     * Constructor used to edit the department
     *
     * @param pageParameters Parameters
     */
    public DepartmentEdit(PageParameters pageParameters) {
        log.debug("Department edit");
        if (pageParameters.isEmpty()) {
            getSession().error(getString("error_empty_parameters"));
            throw new RestartResponseException(OrganizationList.class);
        }

        Long id = pageParameters.get(PARAM_DEPARTMENT_ID).toOptionalLong();
        if (id != null) {
            log.debug("Start load department");
            department = departmentBean.getTDObject(id);
            log.debug("Loaded department: {}", department);
            if (department != null) {
                init();
            } else {
                getSession().error(getString("error_department_not_found"));
                throw new RestartResponseException(OrganizationList.class);
            }
            return;
        }

        id = pageParameters.get(PARAM_MASTER_DEPARTMENT_ID).toOptionalLong();
        if (id != null) {
            Department masterDepartment = departmentBean.getTDObject(id);
            if (masterDepartment != null) {
                this.department = new Department();
                this.department.setOrganization(masterDepartment.getOrganization());
                this.department.setMasterDepartment(masterDepartment);
                init();
            } else {
                // master department not found
                getSession().error(getString("error_master_department_not_found"));
                setResponsePage(OrganizationList.class);
            }
            return;
        }

        id = pageParameters.get(OrganizationEdit.PARAM_ORGANIZATION_ID).toOptionalLong();
        if (id != null) {
            Organization organization = organizationBean.getTDObject(id);
            if (organization != null) {
                this.department = new Department();
                this.department.setOrganization(organization);
                init();
            } else {
                // organization not found
                getSession().error(getString("error_organization_not_found"));
                throw new RestartResponseException(OrganizationList.class);
            }
        }
    }

    protected void init() {

        add(new Label("title", getString("title")));
        add(new Label("header", getString("title")));

        add(new FeedbackPanel("messages"));

        form = new Form<>("form", new CompoundPropertyModel<>(department));
        form.setOutputMarkupId(true);
        add(form);

        // Дата создания подразделения
        /*
        final DatePicker<Date> birthdayPicker = new DatePicker<Date>("entryIntoForceDate",
                new PropertyModel<Date>(department, "entryIntoForceDate"), Date.class) {

            @Override
            public <Date> IConverter<Date> getConverter(Class<Date> type) {
                return (IConverter<Date>)new PatternDateConverter(FORM_DATE_FORMAT, true);
            }
        };
        birthdayPicker.setDateFormat("dd.mm.yy");
        form.add(birthdayPicker);
        */

        // Departments
        panel = new DepartmentTreePanel("departments", department);
        /*if (department.getId() == null) {
            panel.setEnabled(false);
        }*/
        form.add(panel);

        // Button update/create department
        form.add(new SaveDepartmentButton("submit"));

        // Button cancel changes and return to organization page or parent department page
        form.add(new Link<String>("cancel") {

            @Override
            public void onClick() {
                PageParameters parameters = new PageParameters();
                if (department.getId() == null && panel.getMasterDepartment() != null) {
                    parameters.set(PARAM_DEPARTMENT_ID, panel.getMasterDepartment().getId());
                    setResponsePage(DepartmentEdit.class, parameters);
                    return;
                }
                if (department.getOrganization() != null) {
                    parameters.set(OrganizationEdit.PARAM_ORGANIZATION_ID, department.getOrganization().getId());
                    setResponsePage(OrganizationEdit.class, parameters);
                    return;
                }
                setResponsePage(OrganizationList.class);
            }
        }.add(new AttributeModifier("value", department.getId() == null ? getString("cancel") : getString("go_to_organization"))));

        historyUpdate = new TemporalDomainObjectUpdate<Department>() {
            @Override
            public void onUpdate(AjaxRequestTarget target) {
                super.onUpdate(target);

                oldDepartment = department;
                department = getObject();
                form.setModel(new CompoundPropertyModel<>(department));

                target.add(form);
                target.add(departmentHistoryPanel);
                //panel.update(target, organization);
                Date currentDate;
                if (department.isDeleted()) {
                    currentDate = department.getCompletionDate();
                } else if (department.getCompletionDate() != null) {
                    currentDate = department.getEntryIntoForceDate();
                } else {
                    currentDate = new Date();
                }

                panel.updateState(currentDate, isEnabledAction());
                target.add(panel);
            }
        };

        historyUpdate.setObject(department);

        // Название отдела
        addHistoryFieldToForm(form, "name", new TextField<>("name"));
        addHistoryFieldToForm(form, "code", new TextField<>("code"));

        departmentHistoryPanel = new TemporalHistoryIncrementalPanel<Department>("department_history", department, historyUpdate) {
            @Override
            protected TemporalDomainObjectBean<Department> getTDObjectBean() {
                return departmentBean;
            }
        };
        departmentHistoryPanel.setOutputMarkupId(true);
        add(departmentHistoryPanel);
    }

    private boolean isEnabledAction() {
        return department.getId() == null || department.getCompletionDate() == null;
    }

    private class SaveDepartmentButton extends Button {
        public SaveDepartmentButton(String id) {
            super(id);
        }

        @Override
        public void onSubmit() {
            boolean emptyRequiredField = !checkRequiredField(department.getName(), "name");
            /*
            if (!checkRequiredField(department.getEntryIntoForceDate(), "entryIntoForceDate")) {
                emptyRequiredField = true;
            }
            */
            if (emptyRequiredField) {
                return;
            }

            department.setEntryIntoForceDate(new Date());

            boolean newObject = department.getId() == null;
            departmentBean.save(department);
            info(getString("department_saved"));

            if (newObject) {
                form.remove(panel);
                panel = new DepartmentTreePanel("departments", department);
                form.add(panel);
            }

            if (newObject) {
                log.debug("Создание подразделения", new Event(EventCategory.CREATE, department));
            } else {
                log.debug("Редактирование подразделения", new Event(EventCategory.CREATE, oldDepartment, department));
            }

            /*
            PageParameters parameters = new PageParameters();
            parameters.set(PARAM_DEPARTMENT_ID, department.getId());
            throw new RestartResponseException(DepartmentEdit.class, parameters);
            */
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
            return department.getCompletionDate() == null && !department.isDeleted();
        }
    }

    @Override
    protected Department getTDObject() {
        return department;
    }

    @Override
    protected Department getOldTDObject() {
        return oldDepartment;
    }

    @Override
    protected TemporalDomainObjectUpdate<Department> getTDObjectUpdate() {
        return historyUpdate;
    }

    @Override
    protected TemporalDomainObjectBean<Department> getTDObjectBean() {
        return departmentBean;
    }
}
