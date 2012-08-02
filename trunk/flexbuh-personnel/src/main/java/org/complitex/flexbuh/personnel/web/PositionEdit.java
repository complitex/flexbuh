package org.complitex.flexbuh.personnel.web;

import com.google.common.collect.Lists;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.flexbuh.common.logging.Event;
import org.complitex.flexbuh.common.logging.EventCategory;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.personnel.entity.Department;
import org.complitex.flexbuh.personnel.entity.Organization;
import org.complitex.flexbuh.personnel.entity.Payment;
import org.complitex.flexbuh.personnel.entity.Position;
import org.complitex.flexbuh.personnel.service.DepartmentBean;
import org.complitex.flexbuh.personnel.service.OrganizationBean;
import org.complitex.flexbuh.personnel.service.PositionBean;
import org.complitex.flexbuh.personnel.service.TemporalDomainObjectBean;
import org.complitex.flexbuh.personnel.web.component.TemporalDomainObjectUpdate;
import org.complitex.flexbuh.personnel.web.component.TemporalHistoryIncrementalPanel;
import org.complitex.flexbuh.personnel.web.component.TemporalObjectEdit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.swing.event.ListSelectionEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 26.07.12 15:49
 */
@AuthorizeInstantiation(SecurityRole.PERSONAL_MANAGER)
public class PositionEdit extends TemporalObjectEdit<Position> {

    private final static Logger log = LoggerFactory.getLogger(PositionEdit.class);

    public final static String PARAM_POSITION_ID = "position_id";
    public final static String PARAM_POSITION_VERSION = "position_version";

    private Position position = new Position();
    private Position oldPosition;

    @EJB
    private PositionBean positionBean;

    @EJB
    private OrganizationBean organizationBean;

    @EJB
    private DepartmentBean departmentBean;
    
    private Form<Position> form;

    private TemporalHistoryIncrementalPanel<Position> positionHistoryPanel;

    private TemporalDomainObjectUpdate<Position> historyUpdate;

    private PositionEdit() {
    }

    /**
     * Constructor used to create new position
     *
     * @param department Department
     */
    public PositionEdit(Department department) {
        position.setOrganization(position.getOrganization());
        position.setDepartment(department);
        init();
    }

    /**
     * Constructor used to create new position
     *
     * @param organization Organization
     */
    public PositionEdit(Organization organization) {
        position.setOrganization(organization);
        init();
    }

    /**
     * Constructor used to edit the position
     *
     * @param pageParameters Parameters
     */
    public PositionEdit(PageParameters pageParameters) {
        log.debug("Position edit");
        if (pageParameters.isEmpty()) {
            getSession().error(getString("error_empty_parameters"));
            throw new RestartResponseException(OrganizationList.class);
        }

        Long id = pageParameters.get(PARAM_POSITION_ID).toOptionalLong();
        Long departmentId = pageParameters.get(DepartmentEdit.PARAM_DEPARTMENT_ID).toOptionalLong();
        if (id != null) {
            log.debug("Start load position");
            if (departmentId == null) {
                position = positionBean.getTDObject(id);
            }
            log.debug("Loaded position: {}", position);
            if (position != null) {
                init();
            } else {
                getSession().error(getString("error_position_not_found"));
                throw new RestartResponseException(OrganizationList.class);
            }
            return;
        }

        if (departmentId != null) {
            Department department = departmentBean.getTDObject(departmentId);
            if (department != null) {
                position.setDepartment(department);
                init();
            } else {
                // department not found
                getSession().error(getString("error_department_not_found"));
                throw new RestartResponseException(OrganizationList.class);
            }
            return;
        }

        Long organizationId = pageParameters.get(OrganizationEdit.PARAM_ORGANIZATION_ID).toLong();
        if (organizationId != null) {
            Organization organization = organizationBean.getTDObject(organizationId);
            if (organization != null) {
                position.setOrganization(organization);
                init();
            } else {
                // organization not found
                getSession().error(getString("error_organization_not_found"));
                throw new RestartResponseException(OrganizationList.class);
            }
        }
    }

    private void init() {
        add(new Label("title", getString("title")));
        add(new Label("header", getString("title")));

        add(new FeedbackPanel("messages"));

        form = new Form<>("form", new CompoundPropertyModel<>(position));
        form.setOutputMarkupId(true);
        add(form);

        // Button update/create position
        form.add(new SaveDepartmentButton("submit"));

        // Button cancel changes and return to organization page or parent position page
        form.add(new Link<String>("cancel") {

            @Override
            public void onClick() {
                PageParameters parameters = new PageParameters();
                if (position.getDepartment() != null) {
                    parameters.set(DepartmentEdit.PARAM_DEPARTMENT_ID, position.getDepartment().getId());
                    setResponsePage(DepartmentEdit.class, parameters);
                    return;
                }
                if (position.getOrganization() != null) {
                    parameters.set(OrganizationEdit.PARAM_ORGANIZATION_ID, position.getOrganization().getId());
                    setResponsePage(OrganizationEdit.class, parameters);
                    return;
                }
                setResponsePage(OrganizationList.class);
            }
        }.add(new AttributeModifier("value", position.getDepartment() != null ? getString("go_to_department") : getString("go_to_organization"))));

        historyUpdate = new TemporalDomainObjectUpdate<Position>() {
            @Override
            public void onUpdate(AjaxRequestTarget target) {
                super.onUpdate(target);

                oldPosition = position;
                position = getObject();
                form.setModel(new CompoundPropertyModel<>(position));

                target.add(form);
                target.add(positionHistoryPanel);
            }
        };

        historyUpdate.setObject(position);

        // Название отдела
        addHistoryFieldToForm(form, "name", new TextField<>("name"));
        addHistoryFieldToForm(form, "code", new TextField<>("code"));
        addHistoryFieldToForm(form, "payment_salary", new NumberTextField<Float>("payment.salary"));
        addHistoryFieldToForm(form, "payment_currency_unit", new DropDownChoice<>("payment.currencyUnit", Payment.CURRENCY_UNIT));
        addHistoryFieldToForm(form, "description", new TextArea<>("description"));

        form.add(new TextField<>("organization", new Model<>(position.getOrganization() != null? position.getOrganization().getName(): "")));

        positionHistoryPanel = new TemporalHistoryIncrementalPanel<Position>("position_history", position, historyUpdate) {
            @Override
            protected TemporalDomainObjectBean<Position> getTDObjectBean() {
                return positionBean;
            }
        };
        positionHistoryPanel.setOutputMarkupId(true);
        add(positionHistoryPanel);
    }
    
    private class SaveDepartmentButton extends Button {
        public SaveDepartmentButton(String id) {
            super(id);
        }

        @Override
        public void onSubmit() {
            boolean emptyRequiredField = !checkRequiredField(position.getName(), "name");
            if (emptyRequiredField) {
                return;
            }

            position.setEntryIntoForceDate(new Date());

            boolean newObject = position.getId() == null;
            positionBean.save(position);
            info(getString("position_saved"));

            if (newObject) {
                log.debug("Создание подразделения", new Event(EventCategory.CREATE, position));
            } else {
                log.debug("Редактирование подрdазделения", new Event(EventCategory.CREATE, oldPosition, position));
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
            return position.getCompletionDate() == null && !position.isDeleted();
        }
    }

    @Override
    protected Position getTDObject() {
        return position;
    }

    @Override
    protected Position getOldTDObject() {
        return oldPosition;
    }

    @Override
    protected TemporalDomainObjectUpdate<Position> getTDObjectUpdate() {
        return historyUpdate;
    }

    @Override
    protected TemporalDomainObjectBean<Position> getTDObjectBean() {
        return positionBean;
    }
}
