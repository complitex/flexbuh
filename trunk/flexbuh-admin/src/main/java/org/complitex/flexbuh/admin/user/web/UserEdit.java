package org.complitex.flexbuh.admin.user.web;

import com.google.common.collect.Lists;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.string.Strings;
import org.complitex.flexbuh.common.entity.*;
import org.complitex.flexbuh.common.entity.user.User;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.common.service.CityTypeBean;
import org.complitex.flexbuh.common.service.FIOBean;
import org.complitex.flexbuh.common.service.StreetTypeBean;
import org.complitex.flexbuh.common.service.user.UserBean;
import org.complitex.flexbuh.common.template.FormTemplatePage;
import org.odlabs.wiquery.ui.accordion.Accordion;
import org.odlabs.wiquery.ui.accordion.AccordionActive;
import org.odlabs.wiquery.ui.datepicker.DatePicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Pavel Sknar
 *         Date: 20.12.11 13:37
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class UserEdit extends FormTemplatePage {

    private final static Logger log = LoggerFactory.getLogger(UserEdit.class);

    private final static String FORM_DATE_FORMAT = "dd.MM.yyyy";
    
    private final static int LIST_INIT_SIZE = 10;

    @EJB
    private UserBean userBean;

    @EJB
    private StreetTypeBean streetTypeBean;

    @EJB
    private CityTypeBean cityTypeBean;
    
    @EJB
    private FIOBean fioBean;

    private User user;

    public UserEdit() {
        user = new User();
        init();
    }

    public UserEdit(User user) {
        this.user = user;
        init();
    }

    public UserEdit(PageParameters pageParameters) {
        if (!pageParameters.isEmpty()) {
            Long id = pageParameters.get("user_id").toLongObject();
            user = userBean.getUser(id);

            if (user != null) {
                init();
            } else {
                // user not found
                error(getString("error_user_not_found"));
                setResponsePage(UserList.class);
            }
        } else {
            user = new User();
            init();
        }
    }

    private void init() {
        add(new Label("title", getString(user.getId() == null ? "title_create" : "title_edit")));
        add(new Label("header", getString(user.getId() == null ? "title_create" : "title_edit")));

        add(new FeedbackPanel("messages"));

        Form form = new Form("form");
        add(form);

        // Login
        TextField<String> loginField = new TextField<>("login", new PropertyModel<String>(user, "login"));
        form.add(loginField);
        if (user.getId() != null) {
            loginField.setEnabled(false);
        }

        // Password
        PasswordTextField password = new PasswordTextField("password", new PropertyModel<String>(user, "password"));
        password.setRequired(false);
        form.add(password);

        // First name
        AutoCompleteTextField<String> firstNameField = new AutoCompleteTextField<String>("firstName", new PropertyModel<String>(user, "firstName")) {
            @Override
            protected Iterator<String> getChoices(String input) {
                if (Strings.isEmpty(input)) {
                    List<String> emptyList = Collections.emptyList();
                    return emptyList.iterator();
                }

                List<String> choices = Lists.newArrayListWithCapacity(LIST_INIT_SIZE);

                for (FirstName firstName : fioBean.getFirstNames(input, getLocale())) {
                    choices.add(firstName.getName(getLocale()));
                }

                return choices.iterator();
            }
        };
        form.add(firstNameField);

        // Last name
        final AutoCompleteTextField<String> lastNameField = new AutoCompleteTextField<String>("lastName", new PropertyModel<String>(user, "lastName")) {
            @Override
            protected Iterator<String> getChoices(String input) {
                if (Strings.isEmpty(input)) {
                    List<String> emptyList = Collections.emptyList();
                    return emptyList.iterator();
                }

                List<String> choices = Lists.newArrayListWithCapacity(LIST_INIT_SIZE);

                for (LastName lastName : fioBean.getLastNames(input, getLocale())) {
                    choices.add(lastName.getName(getLocale()));
                }

                return choices.iterator();
            }
        };
        form.add(lastNameField);

        // E-mail
        TextField<String> emailField = new TextField<>("email", new PropertyModel<String>(user, "email"));
        form.add(emailField);

        // Middle name
        final AutoCompleteTextField<String> middleNameField = new AutoCompleteTextField<String>("middleName", new PropertyModel<String>(user, "middleName")) {
            @Override
            protected Iterator<String> getChoices(String input) {
                if (Strings.isEmpty(input)) {
                    List<String> emptyList = Collections.emptyList();
                    return emptyList.iterator();
                }

                List<String> choices = Lists.newArrayListWithCapacity(LIST_INIT_SIZE);

                for (MiddleName middleName : fioBean.getMiddleNames(input, getLocale())) {
                    choices.add(middleName.getName(getLocale()));
                }

                return choices.iterator();
            }
        };
        form.add(middleNameField);

        // Birthday
		final DatePicker<Date> birthdayPicker = new DatePicker<Date>("birthday", new PropertyModel<Date>(user, "birthday"), Date.class) {

            @Override
			public <Date> IConverter<Date> getConverter(Class<Date> type) {
				return (IConverter<Date>)new PatternDateConverter(FORM_DATE_FORMAT, true);
			}
		};
		form.add(birthdayPicker);

        // User home address
        Accordion userAddress = new Accordion("user_address");
        userAddress.setCollapsible(true);
        userAddress.setClearStyle(true);
        userAddress.setNavigation(true);
        userAddress.setActive(new AccordionActive(false));
        userAddress.add(new Label("user_address_title", getString("legend_address")));
        form.add(userAddress);

        // Phone
        userAddress.add(new TextField<>("phone", new PropertyModel<String>(user, "phone")));

        // Zip code
        userAddress.add(new TextField<>("zipCode", new PropertyModel<String>(user, "zipCode")));

        // Country
        userAddress.add(new TextField<>("country", new PropertyModel<String>(user, "country")));

        // Region
        userAddress.add(new TextField<>("region", new PropertyModel<String>(user, "region")));

        // Area
        userAddress.add(new TextField<>("area", new PropertyModel<String>(user, "area")));

        //form.add(new TextField<>("city", new PropertyModel<String>(user, "city")));

        // City
        final IModel<String> cityModel = new Model<String>();
        if (StringUtils.isNotEmpty(user.getCityType()) && StringUtils.isNotEmpty(user.getCity())) {
            cityModel.setObject(user.getCityType() + " " + user.getCity());
        } else if (user.getCity() != null) {
            cityModel.setObject(user.getCity());
        }
        final AutoCompleteTextField<String> cityField = new AutoCompleteTextField<String>("city", cityModel) {
            @Override
            protected Iterator<String> getChoices(String input) {
                if (Strings.isEmpty(input)) {
                    List<String> emptyList = Collections.emptyList();
                    return emptyList.iterator();
                }

                List<String> choices = Lists.newArrayListWithCapacity(LIST_INIT_SIZE);

                for (CityType cityType : cityTypeBean.getCityTypes(input, getLocale())) {
                    choices.add(cityType.getName(getLocale()));
                }

                return choices.iterator();
            }
        };
        userAddress.add(cityField);

        // Street
        final IModel<String> streetModel = new Model<String>();
        if (StringUtils.isNotEmpty(user.getStreetType()) && StringUtils.isNotEmpty(user.getStreet())) {
            streetModel.setObject(user.getStreetType() + " " + user.getStreet());
        } else if (user.getStreetType() != null) {
            streetModel.setObject(user.getStreet());
        }
        final AutoCompleteTextField<String> streetField = new AutoCompleteTextField<String>("street", streetModel) {
            @Override
            protected Iterator<String> getChoices(String input) {
                if (Strings.isEmpty(input)) {
                    List<String> emptyList = Collections.emptyList();
                    return emptyList.iterator();
                }

                List<String> choices = Lists.newArrayListWithCapacity(LIST_INIT_SIZE);

                for (StreetType streetType : streetTypeBean.getStreetTypes(input, getLocale())) {
                    choices.add(streetType.getName(getLocale()));
                }

                return choices.iterator();
            }
        };
        userAddress.add(streetField);

        // Building
        userAddress.add(new TextField<>("building", new PropertyModel<String>(user, "building")));

        // Apartment
        userAddress.add(new TextField<>("apartment", new PropertyModel<String>(user, "apartment")));

        // Organization name
        form.add(new TextField<>("organizationName", new PropertyModel<String>(user, "organizationName")));

        // Organization department
        form.add(new TextField<>("organizationDepartment", new PropertyModel<String>(user, "organizationDepartment")));

        // Organization post
        form.add(new TextField<>("organizationPost", new PropertyModel<String>(user, "organizationPost")));

        // Organization address
        Accordion organizationAddress = new Accordion("organization_address");
        organizationAddress.setCollapsible(true);
        organizationAddress.setClearStyle(true);
        organizationAddress.setNavigation(true);
        organizationAddress.setActive(new AccordionActive(false));
        organizationAddress.add(new Label("organization_address_title", getString("legend_organization_address")));
        form.add(organizationAddress);

        // Organization phone
        organizationAddress.add(new TextField<>("organizationPhone", new PropertyModel<String>(user, "organizationPhone")));

        // Organization zip code
        organizationAddress.add(new TextField<>("organizationZipCode", new PropertyModel<String>(user, "organizationZipCode")));

        // Organization country
        organizationAddress.add(new TextField<>("organizationCountry", new PropertyModel<String>(user, "organizationCountry")));

        // Organization region
        organizationAddress.add(new TextField<>("organizationRegion", new PropertyModel<String>(user, "organizationRegion")));

        // Organization area
        organizationAddress.add(new TextField<>("organizationArea", new PropertyModel<String>(user, "organizationArea")));

        // Organization city
        final IModel<String> organizationCityModel = new Model<String>();
        if (StringUtils.isNotEmpty(user.getOrganizationCityType()) && StringUtils.isNotEmpty(user.getOrganizationCity())) {
            organizationCityModel.setObject(user.getOrganizationCityType() + " " + user.getOrganizationCity());
        } else if (user.getOrganizationCity() != null) {
            organizationCityModel.setObject(user.getOrganizationCity());
        }
        final AutoCompleteTextField<String> organizationCityField = new AutoCompleteTextField<String>("organizationCity", organizationCityModel) {
            @Override
            protected Iterator<String> getChoices(String input) {
                if (Strings.isEmpty(input)) {
                    List<String> emptyList = Collections.emptyList();
                    return emptyList.iterator();
                }

                List<String> choices = Lists.newArrayListWithCapacity(LIST_INIT_SIZE);

                for (CityType cityType : cityTypeBean.getCityTypes(input, getLocale())) {
                    choices.add(cityType.getName(getLocale()));
                }

                return choices.iterator();
            }
        };
        organizationAddress.add(organizationCityField);

        // Organization street
        final IModel<String> organizationStreetModel = new Model<String>();
        if (StringUtils.isNotEmpty(user.getOrganizationStreetType()) && StringUtils.isNotEmpty(user.getOrganizationStreet())) {
            organizationStreetModel.setObject(user.getOrganizationStreetType() + " " + user.getOrganizationStreet());
        } else if (user.getOrganizationStreetType() != null) {
            organizationStreetModel.setObject(user.getOrganizationStreet());
        }
        final AutoCompleteTextField<String> organizationStreetField = new AutoCompleteTextField<String>("organizationStreet", organizationStreetModel) {
            @Override
            protected Iterator<String> getChoices(String input) {
                if (Strings.isEmpty(input)) {
                    List<String> emptyList = Collections.emptyList();
                    return emptyList.iterator();
                }

                List<String> choices = Lists.newArrayListWithCapacity(LIST_INIT_SIZE);

                for (StreetType streetType : streetTypeBean.getStreetTypes(input, getLocale())) {
                    choices.add(streetType.getName(getLocale()));
                }

                return choices.iterator();
            }
        };
        organizationAddress.add(organizationStreetField);

        // Organization building
        organizationAddress.add(new TextField<>("organizationBuilding", new PropertyModel<String>(user, "organizationBuilding")));

        // Organization apartment
        organizationAddress.add(new TextField<>("organizationApartment", new PropertyModel<String>(user, "organizationApartment")));

        final IModel<ArrayList<String>> selectedEnabledRoles = new Model<ArrayList<String>>();
        final ListMultipleChoice<String> enabledRolesChoice = new ListMultipleChoice<String>("enabled_roles", selectedEnabledRoles, new PropertyModel<List<String>>(user, "roles")) {
            @Override
            protected boolean localizeDisplayValues() {
                return true;
            }
        };
        enabledRolesChoice.setMaxRows(10);
        enabledRolesChoice.setOutputMarkupId(true);
        form.add(enabledRolesChoice);

        // User roles
        final ArrayList<String> selectedNewRoles = new ArrayList<String>();
        final List<String> selectRoles = getSelectRoles();
        final ListMultipleChoice<String> selectRolesChoice = new ListMultipleChoice<String>("select_roles", new Model<ArrayList<String>>(selectedNewRoles), selectRoles) {
            @Override
            protected boolean localizeDisplayValues() {
                return true;
            }
        };
        selectRolesChoice.setMaxRows(10);
        selectRolesChoice.setOutputMarkupId(true);
        form.add(selectRolesChoice);

        // Button add role
        AjaxButton add = new AjaxButton("add") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                update(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                update(target);
            }

            private void update(AjaxRequestTarget target) {
                selectRoles.removeAll(selectedNewRoles);
                user.getRoles().addAll(selectedNewRoles);

                target.add(enabledRolesChoice);
                target.add(selectRolesChoice);
            }
        };
        form.add(add);

        // Button remove role
        AjaxButton remove = new AjaxButton("remove") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                update(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                log.debug("error remove");
                update(target);
            }

            private void update(AjaxRequestTarget target) {
                selectRoles.addAll(selectedEnabledRoles.getObject());
                user.getRoles().removeAll(selectedEnabledRoles.getObject());

                target.add(enabledRolesChoice);
                target.add(selectRolesChoice);
            }
        };
        form.add(remove);

        // Button update/create user
        AtomicReference<Button> updateOrCreate = new AtomicReference<Button>(new Button("submit") {
            @Override
            public void onSubmit() {

                boolean emptyRequiredField = !checkRequiredField(user.getLogin(), "login");
                if (user.getId() == null && !checkRequiredField(user.getPassword(), "password")) {
                    emptyRequiredField = true;
                }
                if (!checkRequiredField(user.getFirstName(), "firstName")) {
                    emptyRequiredField = true;
                }
                if(!checkRequiredField(user.getLastName(), "lastName")) {
                    emptyRequiredField = true;
                }
                if(!checkRequiredField(user.getEmail(), "email")) {
                    emptyRequiredField = true;
                }

                if (emptyRequiredField) {
                    return;
                }

                if (user.getId() == null && userBean.isLoginExist(user.getLogin())) {
                    error(getString("error_login_exist"));
                    return;
                }

                // Street
                String street = streetModel.getObject();
                if (street != null) {
                    String[] resultSplit = street.split(" ", 2);
                    if (updateStreetType(resultSplit)) {
                        user.setStreetType(resultSplit[0]);
                        user.setStreet(resultSplit[1]);
                    } else {
                        user.setStreet(street);
                    }
                }

                // City
                String city = cityModel.getObject();
                if (city != null) {
                    String[] resultSplit = city.split(" ", 2);
                    if (updateCityType(resultSplit)) {
                        user.setCityType(resultSplit[0]);
                        user.setCity(resultSplit[1]);
                    } else {
                        user.setCity(city);
                    }
                }

                // Organization street
                String organizationStreet = organizationStreetModel.getObject();
                if (organizationStreet != null) {
                    String[] resultSplit = organizationStreet.split(" ", 2);
                    if (updateStreetType(resultSplit)) {
                        user.setOrganizationStreetType(resultSplit[0]);
                        user.setOrganizationStreet(resultSplit[1]);
                    } else {
                        user.setOrganizationStreet(organizationStreet);
                    }
                }

                // Organization city
                String organizationCity = organizationCityModel.getObject();
                if (organizationCity != null) {
                    String[] resultSplit = organizationCity.split(" ", 2);
                    if (updateCityType(resultSplit)) {
                        user.setOrganizationCityType(resultSplit[0]);
                        user.setOrganizationCity(resultSplit[1]);
                    } else {
                        user.setOrganizationCity(organizationCity);
                    }
                }

                fioBean.createFIO(user.getFirstName(), user.getMiddleName(), user.getLastName(), getLocale());

                userBean.save(user);

                info(getString("user_saved"));

                setResponsePage(UserList.class);
            }

            private boolean checkRequiredField(String value, String fieldName) {
                if (value == null) {
                    error(MessageFormat.format(getString("required_field"), getString(fieldName)));
                    return false;
                }
                return true;
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

            @Override
            public boolean isVisible() {
                return true;
            }
        });
        form.add(updateOrCreate.get());

        form.add(new Link("cancel") {

            @Override
            public void onClick() {
                setResponsePage(UserList.class);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private List<String> getSelectRoles() {
        return ListUtils.removeAll(userBean.getAllRoles(), user.getRoles());
    }
}
