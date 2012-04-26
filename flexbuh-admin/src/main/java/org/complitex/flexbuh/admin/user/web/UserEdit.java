package org.complitex.flexbuh.admin.user.web;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.string.Strings;
import org.complitex.flexbuh.common.entity.Address;
import org.complitex.flexbuh.common.entity.CityType;
import org.complitex.flexbuh.common.entity.StreetType;
import org.complitex.flexbuh.common.entity.organization.OrganizationBase;
import org.complitex.flexbuh.common.entity.user.User;
import org.complitex.flexbuh.common.logging.Event;
import org.complitex.flexbuh.common.logging.EventCategory;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.common.service.CityTypeBean;
import org.complitex.flexbuh.common.service.FIOBean;
import org.complitex.flexbuh.common.service.StreetTypeBean;
import org.complitex.flexbuh.common.service.organization.OrganizationBaseBean;
import org.complitex.flexbuh.common.service.user.UserBean;
import org.complitex.flexbuh.common.template.FormTemplatePage;
import org.complitex.flexbuh.common.web.component.FirstNameAutoCompleteTextField;
import org.complitex.flexbuh.common.web.component.LastNameAutoCompleteTextField;
import org.complitex.flexbuh.common.web.component.MiddleNameAutoCompleteTextField;
import org.odlabs.wiquery.ui.accordion.Accordion;
import org.odlabs.wiquery.ui.accordion.AccordionActive;
import org.odlabs.wiquery.ui.datepicker.DatePicker;
import org.odlabs.wiquery.ui.dialog.Dialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.text.MessageFormat;
import java.util.*;

import static com.google.common.collect.Maps.newHashMap;

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

    @EJB
    private OrganizationBaseBean organizationBaseBean;

    private User user;
    private List<OrganizationBase> userOrganizations = Lists.newArrayList();
    private IModel<String> streetModel = new Model<>();
    private IModel<String> cityModel = new Model<>();

    private Dialog addRolesDialog;


    public UserEdit() {
        user = new User();
        user.setAddress(new Address());
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
                if (user.getAddress() == null) {
                    user.setAddress(new Address());
                }
                init();
            } else {
                // user not found
                error(getString("error_user_not_found"));
                setResponsePage(UserList.class);
            }
        } else {
            user = new User();
            user.setAddress(new Address());
            init();
        }
    }

    private void init() {

        if (user.getId() != null) {
            userOrganizations = organizationBaseBean.getUserOrganizations(user);
        }

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

        // Password2
        final PasswordTextField password2 = new PasswordTextField("password2", new Model<>(""));
        password2.setRequired(false);
        form.add(password2);

        //Password validator
        form.add(new EqualPasswordInputValidator(password, password2));

        // First name
        form.add(new FirstNameAutoCompleteTextField("first_name", new PropertyModel<String>(user, "firstName")));

        // Last name
        form.add(new LastNameAutoCompleteTextField("last_name", new PropertyModel<String>(user, "lastName")));

        // E-mail
        TextField<String> emailField = new TextField<>("email", new PropertyModel<String>(user, "email"));
        form.add(emailField);

        // Middle name
        form.add(new MiddleNameAutoCompleteTextField("middle_name", new PropertyModel<String>(user, "middleName")));

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
        userAddress.add(new TextField<>("zipCode", new PropertyModel<String>(user.getAddress(), "zipCode")));

        // Country
        userAddress.add(new TextField<>("country", new PropertyModel<String>(user.getAddress(), "country")));

        // Region
        userAddress.add(new TextField<>("region", new PropertyModel<String>(user.getAddress(), "region")));

        // Area
        userAddress.add(new TextField<>("area", new PropertyModel<String>(user.getAddress(), "area")));

        //form.add(new TextField<>("city", new PropertyModel<String>(user, "city")));

        // City
        if (user.getAddress() != null && StringUtils.isNotEmpty(user.getAddress().getCityType()) &&
                StringUtils.isNotEmpty(user.getAddress().getCity())) {
            cityModel.setObject(user.getAddress().getCityType() + " " + user.getAddress().getCity());
        } else if (user.getAddress() != null && user.getAddress().getCity() != null) {
            cityModel.setObject(user.getAddress().getCity());
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
        if (user.getAddress() != null && StringUtils.isNotEmpty(user.getAddress().getStreetType()) &&
                StringUtils.isNotEmpty(user.getAddress().getStreet())) {
            streetModel.setObject(user.getAddress().getStreetType() + " " + user.getAddress().getStreet());
        } else if (user.getAddress() != null && user.getAddress().getStreet() != null) {
            streetModel.setObject(user.getAddress().getStreet());
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
        userAddress.add(new TextField<>("building", new PropertyModel<String>(user.getAddress(), "building")));

        // Apartment
        userAddress.add(new TextField<>("apartment", new PropertyModel<String>(user.getAddress(), "apartment")));

        // Show enabled user roles
        final WebMarkupContainer rolesContainer = new WebMarkupContainer("rolesContainer");
        rolesContainer.setOutputMarkupId(true);
        form.add(rolesContainer);

        final Map<String, IModel<Boolean>> selectedMap = newHashMap();
        for (String role : user.getRoles()) {
            selectedMap.put(role, new Model<Boolean>(false));
        }
        ListView<String> roles = new ListView<String>("roles", new PropertyModel<List<String>>(user, "roles")) {

            @Override
            protected void populateItem(ListItem<String> item) {
                final String roleName = item.getModelObject();

                AjaxCheckBox selected = new AjaxCheckBox("selected", selectedMap.get(roleName)) {

                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {

                    }
                };
                item.add(selected);
                item.add(new Label("roleName", getStringOrKey(roleName)));

            }
        };
        rolesContainer.add(roles);

        // Button update/create user
        form.add(new SaveUserButton("submit"));

        // Button cancel changes and return to "Users list" page
        form.add(new Link("cancel") {

            @Override
            public void onClick() {
                setResponsePage(UserList.class);
            }
        });

        final WebMarkupContainer userOrganizationContainer = new WebMarkupContainer("userOrganizationContainer") {
            @Override
            public boolean isVisible() {
                return userBean.isPersonalManager(user);
            }
        };
        userOrganizationContainer.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);

        // Dialog add roles
        addRolesDialog = new Dialog("add_roles_dialog");
        addRolesDialog.setTitle(getString("add_roles"));
        //addRolesDialog.setWidth(500);
        //addRolesDialog.setHeight(100);

        add(addRolesDialog);

        Form selectRolesForm = new Form("select_roles_form");

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
        selectRolesForm.add(selectRolesChoice);

        // Button add roles on form. Show dialog
        final AjaxSubmitLink addRoles = new AjaxSubmitLink("addRoles") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                addRolesDialog.open(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {

            }

            @Override
            public boolean isEnabled() {
                return selectRoles.size() > 0;
            }
        };
        addRoles.setDefaultFormProcessing(false);
        form.add(addRoles);

        // Button remove roles on form
        final AjaxButton remove = new AjaxButton("removeRoles") {
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
                for (Map.Entry<String, IModel<Boolean>> entry : selectedMap.entrySet()) {
                    if (entry.getValue().getObject()) {
                        selectRoles.add(entry.getKey());
                        user.getRoles().remove(entry.getKey());
                    }
                }
                selectedMap.clear();
                for (String role : user.getRoles()) {
                    selectedMap.put(role, new Model<Boolean>(false));
                }

                target.add(selectRolesChoice);
                target.add(rolesContainer);
                target.add(this);
                target.add(addRoles);
                //if (!userBean.isPersonalManager(user) && userOrganizationContainer.is) {
                //    userOrganizationContainer.setVisible(false);
                    target.add(userOrganizationContainer);
                //}
            }

            @Override
            public boolean isEnabled() {
                return user.getRoles().size() > 0;
            }
        };
        remove.setDefaultFormProcessing(false);
        form.add(remove);

        // Button add roles on dialog
        selectRolesForm.add(new AjaxButton("add") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                selectRoles.removeAll(selectedNewRoles);
                user.getRoles().addAll(selectedNewRoles);
                for (String newRole : selectedNewRoles) {
                    selectedMap.put(newRole, new Model<Boolean>(false));
                }

                target.add(selectRolesChoice);
                target.add(rolesContainer);
                target.add(remove);
                target.add(addRoles);
                //if (userBean.isPersonalManager(user) && !userOrganizationContainer.isVisible()) {
                //    log.debug("Visible userOrganizationContainer");
                //    userOrganizationContainer.setVisible(true);
                    target.add(userOrganizationContainer);
                //}

                addRolesDialog.close(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {

            }
        });

        addRolesDialog.add(selectRolesForm);

        // Show organizations of user
        form.add(userOrganizationContainer);
        final WebMarkupContainer organizationsContainer = new WebMarkupContainer("organizationsContainer");
        organizationsContainer.setOutputMarkupId(true);
        userOrganizationContainer.add(organizationsContainer);

        final Map<OrganizationBase, IModel<Boolean>> selectedOrganizationsMap = newHashMap();
        for (OrganizationBase organization : userOrganizations) {
            selectedOrganizationsMap.put(organization, new Model<Boolean>(false));
        }
        ListView<OrganizationBase> organizations = new ListView<OrganizationBase>("organizations", userOrganizations) {

            @Override
            protected void populateItem(ListItem<OrganizationBase> item) {
                final OrganizationBase organization = item.getModelObject();

                AjaxCheckBox selectedOrganization = new AjaxCheckBox("selectedOrganization", selectedOrganizationsMap.get(organization)) {

                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {

                    }
                };
                item.add(selectedOrganization);
                item.add(new Label("organizationName", getStringOrKey(organization.getFullName())));
                if (organization.isDeleted()) {
                    item.setVisible(false);
                }
            }
        };
        organizationsContainer.add(organizations);

        // Dialog add organizations
        final Dialog addOrganizationsDialog = new Dialog("add_organizations_dialog");
        addOrganizationsDialog.setTitle(getString("add_organizations"));

        add(addOrganizationsDialog);

        Form selectOrganizationsForm = new Form("select_organizations_form");

        final ArrayList<OrganizationBase> selectedNewOrganizations = Lists.newArrayList();
        final List<OrganizationBase> selectOrganizations = getSelectOrganizations();
        log.debug("Select organization: {}", selectOrganizations);
        final ListMultipleChoice<OrganizationBase> selectOrganizationsChoice =
                new ListMultipleChoice<OrganizationBase>("select_organizations",
                        new Model<>(selectedNewOrganizations), selectOrganizations,
                        new IChoiceRenderer<OrganizationBase>() {

                            @Override
                            public Object getDisplayValue(OrganizationBase object) {
                                return getStringOrKey(object.getFullName());
                            }

                            @Override
                            public String getIdValue(OrganizationBase object, int index) {
                                return String.valueOf(object.getId());
                            }
                        }
                );
        selectOrganizationsChoice.setOutputMarkupId(true);
        selectOrganizationsForm.add(selectOrganizationsChoice);

        // Button add organizations on form. Show dialog
        final AjaxSubmitLink addOrganizations = new AjaxSubmitLink("addOrganizations") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                addOrganizationsDialog.open(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {

            }

            @Override
            public boolean isEnabled() {
                return selectOrganizations.size() > 0;
            }
        };
        userOrganizationContainer.add(addOrganizations);

        // Button remove organizations on form
        final AjaxButton removeOrganizations = new AjaxButton("removeOrganizations") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                update(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                update(target);
            }

            private void update(AjaxRequestTarget target) {
                for (Map.Entry<OrganizationBase, IModel<Boolean>> entry : selectedOrganizationsMap.entrySet()) {
                    if (entry.getValue().getObject()) {
                        selectOrganizations.add(entry.getKey());
                        userOrganizations.remove(entry.getKey());
                    }
                }
                selectedOrganizationsMap.clear();
                for (OrganizationBase organization : userOrganizations) {
                    selectedOrganizationsMap.put(organization, new Model<>(false));
                }

                target.add(selectOrganizationsChoice);
                target.add(organizationsContainer);
                target.add(this);
                target.add(addOrganizations);
            }

            @Override
            public boolean isEnabled() {
                return userOrganizations.size() > 0;
            }
        };
        userOrganizationContainer.add(removeOrganizations);

        // Button add organizations on dialog
        selectOrganizationsForm.add(new AjaxButton("addOrganizations") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                selectOrganizations.removeAll(selectedNewOrganizations);
                userOrganizations.addAll(selectedNewOrganizations);
                for (OrganizationBase newOrganization : selectedNewOrganizations) {
                    selectedOrganizationsMap.put(newOrganization, new Model<>(false));
                }

                target.add(selectOrganizationsChoice);
                target.add(organizationsContainer);
                target.add(removeOrganizations);
                target.add(addOrganizations);

                addOrganizationsDialog.close(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {

            }
        });

        addOrganizationsDialog.add(selectOrganizationsForm);
    }

    private List<String> getSelectRoles() {
        List<String> list = new ArrayList<>(userBean.getAllRoles());

        list.removeAll(user.getRoles());

        return list;
    }

    @SuppressWarnings("unchecked")
    private List<OrganizationBase> getSelectOrganizations() {
        List<OrganizationBase> list = new ArrayList<OrganizationBase>(organizationBaseBean.getAllOrganizations());

        list.removeAll(userOrganizations);

        return list;
    }

    private class SaveUserButton extends Button {
        public SaveUserButton(String id) {
            super(id);
        }

        @Override
        public void onSubmit() {

            boolean emptyRequiredField = !checkRequiredField(user.getLogin(), "login");
            if (user.getId() == null && !checkRequiredField(user.getPassword(), "password")) {
                emptyRequiredField = true;
            }
            if (!checkRequiredField(user.getFirstName(), "first_name")) {
                emptyRequiredField = true;
            }
            if(!checkRequiredField(user.getLastName(), "last_name")) {
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
                    user.getAddress().setStreetType(resultSplit[0]);
                    user.getAddress().setStreet(resultSplit[1]);
                } else {
                    user.getAddress().setStreet(street);
                }
            }

            // City
            String city = cityModel.getObject();
            if (city != null) {
                String[] resultSplit = city.split(" ", 2);
                if (updateCityType(resultSplit)) {
                    user.getAddress().setCityType(resultSplit[0]);
                    user.getAddress().setCity(resultSplit[1]);
                } else {
                    user.getAddress().setCity(city);
                }
            }

            fioBean.createFIO(user.getFirstName(), user.getMiddleName(), user.getLastName(), getLocale());

            boolean createUser = true;
            User oldUser = null;
            if (user.getId() != null) {
                oldUser = userBean.getUser(user.getId());
                createUser = false;
            }
            userBean.save(user);
            if (userBean.isPersonalManager(user)) {
                organizationBaseBean.editUserOrganizationList(userOrganizations, user);
            }
            if (createUser) {
                log.info("Создание пользователя", new Event(EventCategory.CREATE, user));
            } else {
                log.info("Редактирование пользователя", new Event(EventCategory.EDIT, user));
            }

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
    }
}
