package org.complitex.flexbuh.personnel.web;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.flexbuh.common.logging.Event;
import org.complitex.flexbuh.common.logging.EventCategory;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.personnel.entity.*;
import org.complitex.flexbuh.personnel.service.*;
import org.complitex.flexbuh.personnel.web.component.*;
import org.complitex.flexbuh.personnel.web.component.theme.ObjectAttributesModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.validation.constraints.NotNull;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 26.07.12 15:49
 */
@AuthorizeInstantiation(SecurityRole.PERSONAL_MANAGER)
public class PositionEdit extends TemporalObjectEdit<Position> implements ObjectAttributes {

    private final static Logger log = LoggerFactory.getLogger(PositionEdit.class);

    public final static String PARAM_POSITION_ID = "position_id";
    public final static String PARAM_POSITION_VERSION = "position_version";

    private Position position = new Position();

    @EJB
    private PositionBean positionBean;

    @EJB
    private OrganizationBean organizationBean;

    @EJB
    private DepartmentBean departmentBean;

    @EJB
    private ScheduleBean scheduleBean;
    
    private Form<Position> form;

    private TemporalHistoryDatePanel<Position> positionHistoryPanel;

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
        Department department = null;
        if (departmentId != null) {
            department = departmentBean.getTDObject(departmentId);
            if (department == null) {
                // department not found
                getSession().error(getString("error_department_not_found"));
                throw new RestartResponseException(OrganizationList.class);
            }
        }
        if (id != null) {
            log.debug("Start load position");
            position = positionBean.getTDObject(id, department);
            fixPositionVersion(position);

            log.debug("Loaded position: {}", position);
            if (position != null && position.getDepartment() == null && department != null) {
                position.setDepartment(department);
            }
            if (position != null) {
                init();
            } else {
                getSession().error(getString("error_position_not_found"));
                throw new RestartResponseException(OrganizationList.class);
            }
            return;
        }

        if (department != null) {
            position.setDepartment(department);
            init();
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
        form.add(new SavePositionButton("submit"));

        // Button cancel changes and return to organization page or parent position page
        PageParameters parameters = new PageParameters();
        parameters.set(PARAM_POSITION_ID, position.getId() != null? position.getId(): 0L);
        if (position.getDepartment() != null) {
            parameters.set(DepartmentEdit.PARAM_DEPARTMENT_ID, position.getDepartment().getId());
            form.add(new BookmarkablePageLink<DepartmentEdit>("cancel", DepartmentEdit.class, parameters) {
                @Override
                protected CharSequence getURL() {
                    return super.getURL() + "#positions";
                }
            }.add(new AttributeModifier("value", getString("go_to_department"))));
        } else if (position.getOrganization() != null) {
            parameters.set(OrganizationEdit.PARAM_ORGANIZATION_ID, position.getOrganization().getId());
            form.add(new BookmarkablePageLink<OrganizationEdit>("cancel", OrganizationEdit.class, parameters) {
                @Override
                protected CharSequence getURL() {
                    return super.getURL() + "#positions";
                }
            }.add(new AttributeModifier("value", getString("go_to_organization"))));
        } else {
            form.add(new BookmarkablePageLink<OrganizationList>("cancel", OrganizationList.class, parameters) {

            }.add(new AttributeModifier("value", getString("go_to_organization_list"))));
        }

        historyUpdate = new TemporalDomainObjectUpdate<Position>() {
            @Override
            public void onUpdate(AjaxRequestTarget target) {
                super.onUpdate(target);

                getState().setOldObject(position);
                position = getObject();
                getState().setObject(position);
                fixPositionVersion(position);

                form.setModel(new CompoundPropertyModel<>(position));

                target.add(form);
                target.add(positionHistoryPanel);
            }
        };

        historyUpdate.setObject(position);

        // Название отдела
        addHistoryFieldToForm(form, "name", new TextField<>("name"));
        addHistoryFieldToForm(form, "code", new TextField<>("code"));
        addHistoryFieldToForm(form, "description", new TextArea<>("description",
                new ObjectAttributesModel<String>(this, "description")));

        form.add(new PaymentPanel<Position>("payment_panel", this, getState()) {
            @Override
            protected HistoryPanelFactory<Position> getHistoryPanelFactory() {
                return new HistoryPanelFactory<Position>() {
                    @Override
                    protected Position getTDObject() {
                        return position;
                    }

                    @Override
                    protected TemporalDomainObjectUpdate<Position> getTDObjectUpdate() {
                        return historyUpdate;
                    }

                    @Override
                    protected TemporalDomainObjectBean<Position> getTDObjectBean() {
                        return positionBean;
                    }
                };
            }
        });

        addHistoryFieldToForm(form, "schedule_id", new DropDownChoice<>("schedule", new ObjectAttributesModel<Schedule>(this, "schedule"),
                scheduleBean.getTDOObjects(new ScheduleFilter(position.getOrganization(), getSessionId(), true, new Date(), Integer.MAX_VALUE)),
                new IChoiceRenderer<Schedule>() {

                    @Override
                    public Object getDisplayValue(Schedule object) {
                        return getStringOrKey(object.getName());
                    }

                    @Override
                    public String getIdValue(Schedule object, int index) {
                        return String.valueOf(object.getId());
                    }
                }));

        form.add(new TextField<>("organization", new Model<>(position.getOrganization() != null? position.getOrganization().getName(): "")));


        positionHistoryPanel = new TemporalHistoryDatePanel<Position>("position_history", position, historyUpdate) {

            @Override
            protected <F extends TemporalDomainObjectFilter> Position getTemporalDomainObject(TemporalDomainObjectFilter filter) {
                Position position = super.getTemporalDomainObject(filter);
                fixPositionVersion(position);

                return position;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected PositionFilter getFilter(Position position) {
                log.debug("Filter position: {}", position);
                return new PositionFilter(position.getDepartment(), 1);
            }

            @Override
            protected TemporalDomainObjectBean<Position> getTDObjectBean() {
                return positionBean;
            }
        };
        positionHistoryPanel.setOutputMarkupId(true);
        add(positionHistoryPanel);
    }

    @Override
    protected TemporalHistoryPanel<Position> getHistoryPanel(final String fieldName) {
        return position.getDepartment() == null? super.getHistoryPanel(fieldName):
                new TemporalHistoryPanel<Position>(fieldName + "_history",
                    position, historyUpdate) {

            @Override
            protected void initProperties(Position currentObject) {
                super.initProperties(currentObject);
                if (previous != null && start == null) {
                    previous = null;
                }
            }

            @Override
            protected Position getTDObjectPreviousInHistory(Position object) {
                Position result = positionBean.getTDObjectPreviewInHistoryByField(object.getId(),
                        getMaxVersion(object), object.getDepartment().getId(), fieldName);
                if (result.getDepartment() == null) {
                    result.setDepartment(object.getDepartment());
                }
                if (result.getDepartmentAttributes() != null && result.getDepartmentAttributes().getVersion() != null) {
                    result.setEntryIntoForceDate(object.getDepartmentAttributes().getEntryIntoForceDate());
                }
                return result;
            }

            @Override
            protected Position getTDObjectNextInHistory(Position object) {
                Position result = positionBean.getTDObjectNextInHistoryByField(object.getId(),
                        getMaxVersion(object), object.getDepartment().getId(), fieldName);
                if (result.getDepartment() == null) {
                    result.setDepartment(object.getDepartment());
                }
                return result;
            }

            @Override
            protected Position getTDObjectStartInHistory(Position object) {
                PositionFilter filter = new PositionFilter(object.getDepartment(), 1);
                filter.setId(object.getId());
                filter.setCurrentDate(TemporalHistoryDatePanel.START_IN_HISTORY);
                List<Position> result = positionBean.getTDOObjects(filter);
                return result.size() != 0 && !getMaxVersion(result.get(0)).equals(getMaxVersion(object))? result.get(0): null;
            }

            @Override
            protected Position getTDObjectLastInHistory(Position object) {
                Position result = positionBean.getTDObjectLastInHistory(object.getId(), object.getDepartment().getId());
                if (result.getDepartment() == null) {
                    result.setDepartment(object.getDepartment());
                }
                return result;
            }

            private Long getMaxVersion(Position object) {
                return object.getDepartmentAttributes() != null && object.getDepartmentAttributes().getVersion() != null?
                        Math.max(object.getVersion(), object.getDepartmentAttributes().getVersion()): object.getVersion();
            }
        };
    }

    private void fixPositionVersion(Position position) {
        if (position != null && position.getDepartment() != null && position.getDepartmentAttributes() != null
                && position.getDepartmentAttributes().getVersion() != null) {
            if (position.getDepartmentAttributes().getVersion() > position.getVersion()) {
                position.setVersion(position.getDepartmentAttributes().getVersion());
                position.setEntryIntoForceDate(position.getDepartmentAttributes().getEntryIntoForceDate());
            }
            if (position.getDepartmentAttributes().getCompletionDate() != null &&
                    (position.getCompletionDate() == null || position.getCompletionDate() != null &&
                            position.getCompletionDate().after(position.getDepartmentAttributes().getCompletionDate()))) {
                position.setCompletionDate(position.getDepartmentAttributes().getCompletionDate());
            }
        }
    }

    private class SavePositionButton extends Button {
        public SavePositionButton(String id) {
            super(id);
        }

        @Override
        public void onSubmit() {
            boolean emptyRequiredField = !checkRequiredField(position.getName(), "name");
            if (emptyRequiredField) {
                return;
            }

            if (position.getDepartment() != null && position.getDepartmentAttributes() != null) {
                position.getDepartmentAttributes().setEntryIntoForceDate(new Date());
            } else {
                position.setEntryIntoForceDate(new Date());
            }

            Position oldPosition = positionBean.getTDObject(position.getId());

            log.debug("Position save: {}", position);

            boolean newObject = position.getId() == null;
            positionBean.save(position);
            info(getString("position_saved"));

            if (newObject) {
                log.debug("Создание должности", new Event(EventCategory.CREATE, position));
            } else {
                log.debug("Редактирование должности", new Event(EventCategory.EDIT, oldPosition, position));
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
    protected HistoryPanelFactory<Position> getHistoryPanelFactory() {
        return new HistoryPanelFactory<Position>() {
            @Override
            protected Position getTDObject() {
                return position;
            }

            @Override
            protected TemporalDomainObjectUpdate<Position> getTDObjectUpdate() {
                return historyUpdate;
            }

            @Override
            protected TemporalDomainObjectBean<Position> getTDObjectBean() {
                return positionBean;
            }
        };
    }

    @Override
    public Object getObject() {
        return position;
    }

    @Override
    public Object getAttributes() {
        return position.getDepartmentAttributes();
    }
}
