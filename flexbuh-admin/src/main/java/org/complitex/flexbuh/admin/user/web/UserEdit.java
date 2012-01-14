package org.complitex.flexbuh.admin.user.web;

import org.apache.commons.collections.CollectionUtils;
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
import org.complitex.flexbuh.common.entity.CityType;
import org.complitex.flexbuh.common.entity.StreetType;
import org.complitex.flexbuh.common.entity.user.User;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.common.service.CityTypeBean;
import org.complitex.flexbuh.common.service.StreetTypeBean;
import org.complitex.flexbuh.common.service.user.UserBean;
import org.complitex.flexbuh.common.template.FormTemplatePage;
import org.odlabs.wiquery.ui.datepicker.DatePicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.*;

/**
 * @author Pavel Sknar
 *         Date: 20.12.11 13:37
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class UserEdit extends FormTemplatePage {

    private final static Logger log = LoggerFactory.getLogger(UserEdit.class);

    private final static String FORM_DATE_FORMAT = "dd.MM.yyyy";

    @EJB
    private UserBean userBean;

    @EJB
    private StreetTypeBean streetTypeBean;

    @EJB
    private CityTypeBean cityTypeBean;

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
        loginField.setRequired(true);
        form.add(loginField);
        if (user.getId() != null) {
            loginField.setEnabled(false);
        }

        // Password
        PasswordTextField password = new PasswordTextField("password", new PropertyModel<String>(user, "password"));
        if (user.getId() != null) {
            password.setRequired(false);
        }
        form.add(password);

        // First name
        TextField<String> firstNameField = new TextField<>("firstName", new PropertyModel<String>(user, "firstName"));
        firstNameField.setRequired(true);
        form.add(firstNameField);

        // Last name
        TextField<String> lastNameField = new TextField<>("lastName", new PropertyModel<String>(user, "lastName"));
        lastNameField.setRequired(true);
        form.add(lastNameField);

        // E-mail
        TextField<String> emailField = new TextField<>("email", new PropertyModel<String>(user, "email"));
        emailField.setRequired(true);
        form.add(emailField);

        // Middle name
        form.add(new TextField<>("middleName", new PropertyModel<String>(user, "middleName")));

        // Birthday
		final DatePicker<Date> birthdayPicker = new DatePicker<Date>("birthday", new PropertyModel<Date>(user, "birthday"), Date.class) {

            @Override
			public <Date> IConverter<Date> getConverter(Class<Date> type) {
				return (IConverter<Date>)new PatternDateConverter(FORM_DATE_FORMAT, true);
			}
		};
		form.add(birthdayPicker);

        // Phone
        form.add(new TextField<>("phone", new PropertyModel<String>(user, "phone")));

        // Zip code
        form.add(new TextField<>("zipCode", new PropertyModel<String>(user, "zipCode")));

        // Country
        form.add(new TextField<>("country", new PropertyModel<String>(user, "country")));

        // Region
        form.add(new TextField<>("region", new PropertyModel<String>(user, "region")));

        // Area
        form.add(new TextField<>("area", new PropertyModel<String>(user, "area")));

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

                List<String> choices = new ArrayList<String>(10);

                for (CityType cityType : cityTypeBean.getCityTypes(input, getLocale())) {
                    choices.add(cityType.getName(getLocale()));
                }

                return choices.iterator();
            }
        };
        form.add(cityField);

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

                List<String> choices = new ArrayList<String>(10);

                for (StreetType streetType : streetTypeBean.getStreetTypes(input, getLocale())) {
                    choices.add(streetType.getName(getLocale()));
                }

                return choices.iterator();
            }
        };
        form.add(streetField);

        // Building
        form.add(new TextField<>("building", new PropertyModel<String>(user, "building")));

        // Apartment
        form.add(new TextField<>("apartment", new PropertyModel<String>(user, "apartment")));

        // Organization name
        form.add(new TextField<>("organizationName", new PropertyModel<String>(user, "organizationName")));

        // Organization phone
        form.add(new TextField<>("organizationPhone", new PropertyModel<String>(user, "organizationPhone")));

        // Organization department
        form.add(new TextField<>("organizationDepartment", new PropertyModel<String>(user, "organizationDepartment")));

        // Organization post
        form.add(new TextField<>("organizationPost", new PropertyModel<String>(user, "organizationPost")));

        // Organization zip code
        form.add(new TextField<>("organizationZipCode", new PropertyModel<String>(user, "organizationZipCode")));

        // Organization country
        form.add(new TextField<>("organizationCountry", new PropertyModel<String>(user, "organizationCountry")));

        // Organization region
        form.add(new TextField<>("organizationRegion", new PropertyModel<String>(user, "organizationRegion")));

        // Organization area
        form.add(new TextField<>("organizationArea", new PropertyModel<String>(user, "organizationArea")));

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

                List<String> choices = new ArrayList<String>(10);

                for (CityType cityType : cityTypeBean.getCityTypes(input, getLocale())) {
                    choices.add(cityType.getName(getLocale()));
                }

                return choices.iterator();
            }
        };
        form.add(organizationCityField);

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

                List<String> choices = new ArrayList<String>(10);

                for (StreetType streetType : streetTypeBean.getStreetTypes(input, getLocale())) {
                    choices.add(streetType.getName(getLocale()));
                }

                return choices.iterator();
            }
        };
        form.add(organizationStreetField);

        // Organization building
        form.add(new TextField<>("organizationBuilding", new PropertyModel<String>(user, "organizationBuilding")));

        // Organization apartment
        form.add(new TextField<>("organizationApartment", new PropertyModel<String>(user, "organizationApartment")));

        final IModel<ArrayList<String>> selectedEnabledRoles = new Model<ArrayList<String>>();
        final ListMultipleChoice<String> enabledRolesChoice = new ListMultipleChoice<String>("enabled_roles", selectedEnabledRoles, new PropertyModel<List<String>>(user, "roles"));
        enabledRolesChoice.setMaxRows(10);
        enabledRolesChoice.setOutputMarkupId(true);
        form.add(enabledRolesChoice);


        final ArrayList<String> selectedNewRoles = new ArrayList<String>();
        final List<String> selectRoles = getSelectRoles();
        final ListMultipleChoice<String> selectRolesChoice = new ListMultipleChoice<String>("select_roles", new Model<ArrayList<String>>(selectedNewRoles), selectRoles);
        selectRolesChoice.setMaxRows(10);
        selectRolesChoice.setOutputMarkupId(true);
        form.add(selectRolesChoice);

        AjaxButton add = new AjaxButton("add") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                update(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                log.error("error add");
                update(target);
            }

            private void update(AjaxRequestTarget target) {
                log.debug("current selectRoles: {}", selectRoles);
                log.debug("current enabledRoles: {}", user.getRoles());
                log.debug("selectedNewRoles: {}", selectedNewRoles);
                log.debug("selectRolesChoice.getModelObject(): {}", selectRolesChoice.getModelObject());

                selectRoles.removeAll(selectedNewRoles);
                user.getRoles().addAll(selectedNewRoles);

                log.debug("add selectRoles: {}", selectRoles);
                log.debug("add enabledRoles: {}", user.getRoles());

                target.add(enabledRolesChoice);
                target.add(selectRolesChoice);
            }
        };
        form.add(add);

        AjaxButton remove = new AjaxButton("remove") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                update(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                log.error("error remove");
                update(target);
            }

            private void update(AjaxRequestTarget target) {
                log.error("current selectRoles: {}", selectRoles);
                log.error("current enabledRoles: {}", user.getRoles());

                selectRoles.addAll(selectedEnabledRoles.getObject());
                user.getRoles().removeAll(selectedEnabledRoles.getObject());

                log.error("remove selectRoles: {}", selectRoles);
                log.error("remove enabledRoles: {}", user.getRoles());

                target.add(enabledRolesChoice);
                target.add(selectRolesChoice);
            }
        };
        form.add(remove);

        // Button update/create user
        Button updateOrCreate = new Button("submit") {
            @Override
            public void onSubmit() {

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

                userBean.save(user);

                info(getString("user_saved"));

                setResponsePage(UserList.class);
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
        };
        form.add(updateOrCreate);

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
