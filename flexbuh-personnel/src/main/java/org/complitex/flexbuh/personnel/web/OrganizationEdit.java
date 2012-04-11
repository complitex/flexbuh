package org.complitex.flexbuh.personnel.web;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.convert.IConverter;
import org.complitex.flexbuh.common.entity.Address;
import org.complitex.flexbuh.common.entity.CityType;
import org.complitex.flexbuh.common.entity.StreetType;
import org.complitex.flexbuh.common.entity.organization.Organization;
import org.complitex.flexbuh.common.logging.EventCategory;
import org.complitex.flexbuh.common.logging.EventModel;
import org.complitex.flexbuh.common.logging.EventObjectFactory;
import org.complitex.flexbuh.common.logging.EventObjectId;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.common.service.CityTypeBean;
import org.complitex.flexbuh.common.service.StreetTypeBean;
import org.complitex.flexbuh.common.service.organization.OrganizationBean;
import org.complitex.flexbuh.common.template.FormTemplatePage;
import org.complitex.flexbuh.common.web.component.AddressPanel;
import org.complitex.flexbuh.common.web.component.IAjaxUpdate;
import org.complitex.flexbuh.common.web.component.OrganizationTypeAutoCompleteTextField;
import org.complitex.flexbuh.personnel.web.component.DepartmentTreePanel;
import org.complitex.flexbuh.personnel.web.component.TemporalObjectEditDialog;
import org.odlabs.wiquery.ui.accordion.Accordion;
import org.odlabs.wiquery.ui.accordion.AccordionActive;
import org.odlabs.wiquery.ui.datepicker.DatePicker;
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
public class OrganizationEdit extends FormTemplatePage {

    private Logger log = LoggerFactory.getLogger(OrganizationEdit.class);

    private final static String FORM_DATE_FORMAT = "dd.MM.yyyy";

    public final static String PARAM_ORGANIZATION_ID = "organization_id";

    @EJB
	private EventObjectFactory eventObjectFactory;

    @EJB
    private OrganizationBean organizationBean;

    @EJB
    private StreetTypeBean streetTypeBean;

    @EJB
    private CityTypeBean cityTypeBean;

    protected Organization organization;

    private IModel<String> physicalStreetModel = new Model<String>();
    private IModel<String> physicalCityModel = new Model<String>();

    private IModel<String> juridicalStreetModel = new Model<String>();
    private IModel<String> juridicalCityModel = new Model<String>();

    private TemporalObjectEditDialog dialog;

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

        Long id = pageParameters.get("organization_id").toLongObject();
        organization = organizationBean.getOrganization(id);

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
        add(new Label("title", getString("title")));
        add(new Label("header", getString("title")));

        add(new FeedbackPanel("messages"));

        final Form form = new Form("form");
        form.setOutputMarkupId(true);
        add(form);

        // Тип организации
        form.add(new OrganizationTypeAutoCompleteTextField("type", new PropertyModel<String>(organization, "type")));

        // Название организации
        form.add(new TextField<>("name", new PropertyModel<String>(organization, "name")));

        // Телефон
        form.add(new TextField<String>("phone", new PropertyModel<String>(organization, "phone")){
            @Override
            public <String> IConverter<String> getConverter(final Class<String> type) {
                return super.getConverter(type);
                // US telephone number mask
//                return new MaskConverter<>(PHONE_MASK);
            }
        });

        // Факс
        form.add(new TextField<String>("fax", new PropertyModel<String>(organization, "fax")){
            @Override
            public <String> IConverter<String> getConverter(final Class<String> type) {
                return super.getConverter(type);
                // US telephone number mask
//                return new MaskConverter<>(PHONE_MASK);
            }
        });

        form.add(new TextField<>("email", new PropertyModel<String>(organization, "email")));

        form.add(new TextField<>("httpAddress", new PropertyModel<String>(organization, "httpAddress")));

        // Дата создания организации
		final DatePicker<Date> birthdayPicker = new DatePicker<Date>("entryIntoForceDate",
                new PropertyModel<Date>(organization, "entryIntoForceDate"), Date.class) {

            @Override
			public <Date> IConverter<Date> getConverter(Class<Date> type) {
				return (IConverter<Date>)new PatternDateConverter(FORM_DATE_FORMAT, true);
			}
        };
        birthdayPicker.setDateFormat("dd.mm.yy");
		form.add(birthdayPicker);

        // Departments
        final DepartmentTreePanel panel = new DepartmentTreePanel("departments", organization);
        if (organization.getId() == null) {
            panel.setVisible(false);
        }
        form.add(panel);

        // Organization physical address
        Accordion physicalAddress = new Accordion("physical_address");
        physicalAddress.setCollapsible(true);
        physicalAddress.setClearStyle(true);
        physicalAddress.setNavigation(true);
        physicalAddress.setActive(new AccordionActive(false));
        physicalAddress.add(new Label("physical_address_title", getString("legend_physical_address")));
        form.add(physicalAddress);

        physicalAddress.add(new AddressPanel("physical_address_fields",
                organization.getPhysicalAddress(), physicalCityModel, physicalStreetModel));

        // User home address
        Accordion juridicalAddress = new Accordion("juridical_address");
        juridicalAddress.setCollapsible(true);
        juridicalAddress.setClearStyle(true);
        juridicalAddress.setNavigation(true);
        juridicalAddress.setActive(new AccordionActive(false));
        juridicalAddress.add(new Label("juridical_address_title", getString("legend_juridical_address")));
        form.add(juridicalAddress);

        juridicalAddress.add(new AddressPanel("juridical_address_fields",
                organization.getJuridicalAddress(), juridicalCityModel, juridicalStreetModel));

        // Button update/create organization
        form.add(new SaveOrganizationButton("submit"));

        // Button cancel changes and return to "Organizations list" page
        form.add(new Link("cancel") {

            @Override
            public void onClick() {
                setResponsePage(OrganizationList.class);
            }
        });

        IAjaxUpdate update =  new IAjaxUpdate() {
            @Override
            public void onUpdate(AjaxRequestTarget target) {
                save();
                setResponsePage(OrganizationList.class);
            }
        };

        dialog = new TemporalObjectEditDialog("template_object_edit_dialog", organization, update);
        add(dialog);
    }

    private void save() {
        // Street of physical address
        setStreet(organization.getPhysicalAddress(), physicalStreetModel.getObject());

        // City of physical address
        setCity(organization.getPhysicalAddress(), physicalStreetModel.getObject());

        // Street of juridical address
        setStreet(organization.getJuridicalAddress(), juridicalStreetModel.getObject());

        // City of juridical address
        setCity(organization.getJuridicalAddress(), juridicalCityModel.getObject());

        boolean createOrganization = true;
        Organization oldOrganization = null;
        if (organization.getId() != null) {
            oldOrganization = organizationBean.getOrganization(organization.getId());
            createOrganization = false;
        }
        organizationBean.save(organization, getLocale());
        if (createOrganization) {
            log.info("Create organization '{}'", new Object[]{organization, EventCategory.CREATE,
                        new EventObjectId(organization.getId()), new EventModel(Organization.class.getName()),
                        eventObjectFactory.getEventNewObject(organization)});
        } else {
            log.info("Edit organization '{}'", new Object[]{organization, EventCategory.EDIT,
                        new EventObjectId(organization.getId()), new EventModel(Organization.class.getName()),
                        eventObjectFactory.getEventNewObject(organization),
                        eventObjectFactory.getEventOldObject(oldOrganization)});
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

            boolean emptyRequiredField = !checkRequiredField(organization.getName(), "name");
            if (!checkRequiredField(organization.getEmail(), "email")) {
                emptyRequiredField = true;
            }
            if (!checkRequiredField(organization.getEntryIntoForceDate(), "entryIntoForceDate")) {
                emptyRequiredField = true;
            }

            if (emptyRequiredField) {
                return;
            }

            dialog.open(target);
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
        public boolean isVisible() {
            return true;
        }
    }
}
