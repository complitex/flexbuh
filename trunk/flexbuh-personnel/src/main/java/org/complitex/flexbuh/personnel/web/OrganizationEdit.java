package org.complitex.flexbuh.personnel.web;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.convert.IConverter;
import org.complitex.flexbuh.common.entity.Address;
import org.complitex.flexbuh.common.entity.CityType;
import org.complitex.flexbuh.common.entity.StreetType;
import org.complitex.flexbuh.common.logging.Event;
import org.complitex.flexbuh.common.logging.EventCategory;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.common.service.CityTypeBean;
import org.complitex.flexbuh.common.service.StreetTypeBean;
import org.complitex.flexbuh.common.template.FormTemplatePage;
import org.complitex.flexbuh.common.web.component.AddressPanel;
import org.complitex.flexbuh.personnel.entity.Organization;
import org.complitex.flexbuh.personnel.service.OrganizationBean;
import org.complitex.flexbuh.personnel.service.TemporalDomainObjectBean;
import org.complitex.flexbuh.personnel.web.component.*;
import org.odlabs.wiquery.ui.accordion.Accordion;
import org.odlabs.wiquery.ui.accordion.AccordionActive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 05.03.12 16:55
 */
@AuthorizeInstantiation(SecurityRole.PERSONAL_MANAGER)
public class OrganizationEdit extends TemporalObjectEdit<Organization> {

    private final static Logger log = LoggerFactory.getLogger(OrganizationEdit.class);

    //private final static String FORM_DATE_FORMAT = "dd.MM.yyyy";

    public final static String PARAM_ORGANIZATION_ID = "organization_id";
    public final static String PARAM_ORGANIZATION_VERSION = "organization_version";

    @EJB
    private OrganizationBean organizationBean;

    @EJB
    private StreetTypeBean streetTypeBean;

    @EJB
    private CityTypeBean cityTypeBean;

    protected Organization organization;

    private Organization oldOrganization = null;

    private IModel<String> physicalStreetModel = new Model<String>();
    private IModel<String> physicalCityModel = new Model<String>();

    private IModel<String> juridicalStreetModel = new Model<String>();
    private IModel<String> juridicalCityModel = new Model<String>();

    //private TemporalObjectEditDialog dialog;
    private FeedbackPanel messagesPanel;

    private UpdatedAjaxBehavior updatedAjaxBehavior;

    private TemporalHistoryIncrementalPanel<Organization> organizationHistoryPanel;

    private TemporalDomainObjectUpdate<Organization> historyUpdate;

    private boolean collapsedPositionPanel = true;

    private boolean collapsedSchedulePanel = true;

    protected OrganizationEdit() {
        organization = new Organization();
        organization.setJuridicalAddress(new Address());
        organization.setPhysicalAddress(new Address());
        init();
    }

    public OrganizationEdit(Organization organization) {
        this.organization = organization;
        init();
    }

    public OrganizationEdit(PageParameters pageParameters) {
        if (pageParameters.isEmpty()) {
            error(getString("error_empty_parameters"));
            setResponsePage(OrganizationList.class);
            return;
        }

        Long id = pageParameters.get(PARAM_ORGANIZATION_ID).toLongObject();
        Long version = pageParameters.get(PARAM_ORGANIZATION_VERSION).toOptionalLong();

        if (version != null) {
            organization = organizationBean.getTDObject(id, version);
        } else {
            organization = organizationBean.getTDObject(id);
        }
        collapsedPositionPanel = pageParameters.get(PositionEdit.PARAM_POSITION_ID).toOptionalLong() == null;
        collapsedSchedulePanel = pageParameters.get(ScheduleEdit.PARAM_SCHEDULE_ID).toOptionalLong() == null;


        if (organization != null) {
            if (organization.getPhysicalAddress() == null) {
                organization.setPhysicalAddress(new Address());
            }
            if (organization.getJuridicalAddress() == null) {
                organization.setJuridicalAddress(new Address());
            }
            init();
        } else {
            // organization not found
            error(getString("error_organization_not_found"));
            setResponsePage(OrganizationList.class);
        }
    }



    protected  void init() {

        Organization lastOrganization = organization.getId() == null? null:
                organizationBean.getTDObjectLastInHistory(organization.getId());

        boolean deleted = lastOrganization != null && lastOrganization.isDeleted();

        add(new Label("title", deleted? getString("title_view") : getString("title_edit")));
        add(new Label("header", deleted? getString("title_view") : getString("title_edit")));

        messagesPanel = new FeedbackPanel("messages");
        messagesPanel.setOutputMarkupId(true);
        add(messagesPanel);

        final Form<Organization> form = new Form<>("form", new CompoundPropertyModel<>(organization));
        if (!collapsedPositionPanel || !collapsedPositionPanel) {
            form.add(new AttributeModifier("class", UNFOCUSABLE_CSS_CLASS));
        }
        form.setOutputMarkupId(true);

        updatedAjaxBehavior = new UpdatedAjaxBehavior();
        form.add(updatedAjaxBehavior);

        // Organization`s departments
        final DepartmentTreePanel departmentTreePanel = new DepartmentTreePanel("departments", organization);
        if (organization.getId() == null) {
            departmentTreePanel.setVisible(false);
        }
        //departmentTreePanel.setEnabled(!deleted);
        departmentTreePanel.setOutputMarkupId(true);
        form.add(departmentTreePanel);

        // Organization`s physical address

        final ClickAjaxBehavior physicalClickBehavior = new ClickAjaxBehavior(false);

        final Accordion physicalAddress = new Accordion("physical_address");
        physicalAddress.setCollapsible(true);
        physicalAddress.setClearStyle(false);
        physicalAddress.setNavigation(true);
        physicalAddress.setActive(new AccordionActive(false));
        physicalAddress.add(new Label("physical_address_title", getString("legend_physical_address")).add(physicalClickBehavior));
        form.add(physicalAddress);

        final AddressPanel physicalAddressPanel = new AddressPanel("physical_address_fields",
                new CompoundPropertyModel<>(organization.getPhysicalAddress()), physicalCityModel, physicalStreetModel);
        physicalAddress.add(physicalAddressPanel);

        // Organization`s juridical address

        final ClickAjaxBehavior juridicalClickBehavior = new ClickAjaxBehavior(false);

        final Accordion juridicalAddress = new Accordion("juridical_address");
        juridicalAddress.setCollapsible(true);
        juridicalAddress.setClearStyle(true);
        juridicalAddress.setNavigation(true);
        juridicalAddress.setActive(new AccordionActive(false));
        juridicalAddress.add(new Label("juridical_address_title", getString("legend_juridical_address")).add(juridicalClickBehavior));
        form.add(juridicalAddress);

        final AddressPanel juridicalAddressPanel = new AddressPanel("juridical_address_fields",
                new CompoundPropertyModel<>(organization.getJuridicalAddress()), juridicalCityModel, juridicalStreetModel);
        juridicalAddress.add(juridicalAddressPanel);

        // Organization`s positions

        final PositionListPanel positionListPanel = new PositionListPanel("positions", organization, collapsedPositionPanel);
        positionListPanel.setOutputMarkupId(true);
        if (organization.getId() == null) {
            positionListPanel.setVisible(false);
        }
        form.add(positionListPanel);

        // Organization`s schedules

        final ScheduleListAccordionPanel scheduleListAccordionPanel =
                new ScheduleListAccordionPanel("schedules", organization, collapsedSchedulePanel);
        scheduleListAccordionPanel.setOutputMarkupId(true);
        if (organization.getId() == null) {
            scheduleListAccordionPanel.setVisible(false);
        }
        form.add(scheduleListAccordionPanel);

        historyUpdate = new TemporalDomainObjectUpdate<Organization>() {
            @Override
            public void onUpdate(AjaxRequestTarget target) {
                super.onUpdate(target);

                oldOrganization = organization;
                organization = getObject();
                //form.setModelObject(organization);
                form.setModel(new CompoundPropertyModel<>(organization));
                physicalAddressPanel.updateModel(new CompoundPropertyModel<>(organization.getPhysicalAddress()),
                        physicalCityModel, physicalStreetModel);
                juridicalAddressPanel.updateModel(new CompoundPropertyModel<>(organization.getJuridicalAddress()),
                        juridicalCityModel, juridicalStreetModel);
                if (physicalClickBehavior.isOpened()) {
                    physicalAddress.setActive(null);
                } else if (physicalAddress.getActive() == null) {
                    physicalAddress.setActive(new AccordionActive(false));
                }
                if (juridicalClickBehavior.isOpened()) {
                    juridicalAddress.setActive(null);
                } else if (juridicalAddress.getActive() == null) {
                    juridicalAddress.setActive(new AccordionActive(false));
                }

                target.add(form);
                target.add(organizationHistoryPanel);
                //departmentTreePanel.update(target, organization);
                Date currentDate;
                if (organization.isDeleted()) {
                    currentDate = organization.getCompletionDate();
                } else if (organization.getCompletionDate() != null) {
                    currentDate = organization.getEntryIntoForceDate();
                } else {
                    currentDate = new Date();
                }

                departmentTreePanel.updateState(currentDate, isEnabledAction());
                target.add(departmentTreePanel);

                positionListPanel.updateState(currentDate, isEnabledAction());
                //target.add(positionListPanel);
            }
        };

        add(form);

        // Тип организации
        addHistoryFieldToForm(form, "type", new OrganizationTypeAutoCompleteTextField("type"));

        // Название организации
        addHistoryFieldToForm(form, "name", new TextField<>("name"));

        // Телефон
        addHistoryFieldToForm(form, "phone", new TextField<String>("phone") {
            @Override
            public <String> IConverter<String> getConverter(final Class<String> type) {
                return super.getConverter(type);
                // US telephone number mask
//                return new MaskConverter<>(PHONE_MASK);
            }
        });

        // Факс
        addHistoryFieldToForm(form, "fax", new TextField<String>("fax") {
            @Override
            public <String> IConverter<String> getConverter(final Class<String> type) {
                return super.getConverter(type);
                // US telephone number mask
//                return new MaskConverter<>(PHONE_MASK);
            }
        });

        addHistoryFieldToForm(form, "email", new TextField<>("email"));

        addHistoryFieldToForm(form, "http_address", new TextField<>("httpAddress"));

        // Дата создания организации
		/*final DatePicker<Date> birthdayPicker = new DatePicker<Date>("entryIntoForceDate",
                Date.class) {

            @Override
			public <Date> IConverter<Date> getConverter(Class<Date> type) {
				return (IConverter<Date>)new PatternDateConverter(FORM_DATE_FORMAT, true);
			}
        };
        birthdayPicker.setDateFormat("dd.mm.yy");
		form.add(birthdayPicker);
		*/

        // Button update/create organization
        form.add(new SaveOrganizationButton("submit").setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true));

        // Button cancel changes and return to "Organizations list" page
        form.add(new Link("cancel") {

            @Override
            public void onClick() {
                setResponsePage(OrganizationList.class);
            }
        }.add(new AttributeModifier("value", deleted ? getString("go_to_organization_list") : getString("cancel")))
         .add(new AttributeModifier("class", deleted ? "btnBig": "btnMiddle")));

        /*
        IAjaxUpdate update =  new IAjaxUpdate() {
            @Override
            public void onUpdate(AjaxRequestTarget target) {
                save();
                setResponsePage(OrganizationList.class);
            }
        };

        dialog = new TemporalObjectEditDialog("template_object_edit_dialog", organization, update);
        add(dialog);
        */

        historyUpdate.setObject(organization);

        organizationHistoryPanel =
                new TemporalHistoryIncrementalPanel<Organization>("organization_history", organization, historyUpdate) {
                    @Override
                    protected TemporalDomainObjectBean<Organization> getTDObjectBean() {
                        return organizationBean;
                    }
                };
        organizationHistoryPanel.setOutputMarkupId(true);
        add(organizationHistoryPanel);
        /*
        form.add(new AjaxEventBehavior("onmouseover") {
            protected void onEvent(final AjaxRequestTarget target) {
                if (!inHistoryContainer && currentEnabledPanel != null && currentEnabledPanel.isVisible()) {
                    currentEnabledPanel.setVisible(false);
                    target.add(currentEnabledPanel);
                }
            }
        });
        */

        /*
        add(new TemporalHistoryList<Organization>("organization_history", organization) {
            @Override
            protected List<Organization> getObjects(TemporalDomainObjectHistoryFilter filter) {
                List<Organization> organizations = organizationBean.getOrganizationHistory(organization.getId(), filter);
                log.debug("Show history: {} by filter: {}", organizations, filter);
                return organizations;
            }

            @Override
            protected int getObjectsCount(TemporalDomainObjectHistoryFilter filter) {
                return organizationBean.getOrganizationHistoryCount(organization.getId(), filter);
            }

            @Override
            protected Class<? extends Page> getPageClass() {
                return OrganizationEdit.class;
            }
        }.setVisible(organization.getId() != null));
        */

    }

    private void save() {
        // Street of physical address
        setStreet(organization.getPhysicalAddress(), physicalStreetModel.getObject());

        // City of physical address
        setCity(organization.getPhysicalAddress(), physicalCityModel.getObject());

        // Street of juridical address
        setStreet(organization.getJuridicalAddress(), juridicalStreetModel.getObject());

        // City of juridical address
        setCity(organization.getJuridicalAddress(), juridicalCityModel.getObject());

        boolean createOrganization = true;
        Organization oldOrganization = null;
        if (organization.getId() != null) {
            oldOrganization = organizationBean.getTDObject(organization.getId());
            createOrganization = false;
        }
        organization.setEntryIntoForceDate(new Date()); // TODO modification by current date
        organizationBean.save(organization);
        if (createOrganization) {
            log.debug("Создание организации", new Event(EventCategory.CREATE, organization));
        } else {
            log.debug("Редактирование организации", new Event(EventCategory.EDIT, oldOrganization, organization));
        }

        info(getString("organization_saved"));
    }

    private boolean updateStreetType(String[] resultSplit) {

            if (resultSplit.length == 2) {
                List<StreetType> streetTypes = streetTypeBean.getStreetTypes(resultSplit[0], getLocale());
                if (streetTypes.size() == 1 && StringUtils.equalsIgnoreCase(streetTypes.get(0).getName(getLocale()), resultSplit[0])) {
                    return true;
                }
            }
            return false;
        }

        private boolean updateCityType(String[] resultSplit) {
            if (resultSplit.length == 2) {
                List<CityType> cityTypes = cityTypeBean.getCityTypes(resultSplit[0], getLocale());
                if (cityTypes.size() == 1 && StringUtils.equalsIgnoreCase(cityTypes.get(0).getName(getLocale()), resultSplit[0])) {
                    return true;
                }
            }
            return false;
        }

        private void setStreet(Address address, String street) {
            if (street == null) {
                return;
            }
            String[] resultSplit = street.split(" ", 2);
            if (updateStreetType(resultSplit)) {
                address.setStreetType(resultSplit[0]);
                address.setStreet(resultSplit[1]);
            } else {
                address.setStreet(street);
            }
        }

        private void setCity(Address address, String city) {
            if (city == null) {
                return;
            }
            String[] resultSplit = city.split(" ", 2);
            if (updateCityType(resultSplit)) {
                address.setCityType(resultSplit[0]);
                address.setCity(resultSplit[1]);
            } else {
                address.setCity(city);
            }
        }

    private class SaveOrganizationButton extends AjaxButton {
        public SaveOrganizationButton(String id) {
            super(id);
        }

        @Override
        public void onSubmit(AjaxRequestTarget target, Form<?> form) {

            if (updatedAjaxBehavior.isUpdated() || organization.getId() == null) {
                boolean emptyRequiredField = !checkRequiredField(organization.getName(), "name");
                if (!checkRequiredField(organization.getEmail(), "email")) {
                    emptyRequiredField = true;
                }
                /*
                if (!checkRequiredField(organization.getEntryIntoForceDate(), "entryIntoForceDate")) {
                    emptyRequiredField = true;
                }*/

                if (emptyRequiredField) {
                    target.add(messagesPanel);
                    return;
                }

                save();
            }

            setResponsePage(OrganizationList.class);
            //dialog.open(target);
        }

        @Override
        protected void onError(AjaxRequestTarget target, Form<?> form) {

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
            return isEnabledAction();
        }
    }

    private boolean isEnabledAction() {
        return organization.getId() == null || organization.getCompletionDate() == null;
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

    private class UpdatedAjaxBehavior extends AjaxEventBehavior {
        private boolean updated = false;

        private UpdatedAjaxBehavior() {
            super("onchange");
        }

        @Override
        protected void onEvent(AjaxRequestTarget target) {
            updated = true;
        }

        public boolean isUpdated() {
            return updated;
        }
    }

    @Override
    protected Organization getTDObject() {
        return organization;
    }

    @Override
    protected Organization getOldTDObject() {
        return oldOrganization;
    }

    @Override
    protected TemporalDomainObjectUpdate<Organization> getTDObjectUpdate() {
        return historyUpdate;
    }

    @Override
    protected TemporalDomainObjectBean<Organization> getTDObjectBean() {
        return organizationBean;
    }
}
