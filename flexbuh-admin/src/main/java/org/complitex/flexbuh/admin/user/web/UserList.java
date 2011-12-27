package org.complitex.flexbuh.admin.user.web;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.flexbuh.common.entity.user.User;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.common.service.user.UserBean;
import org.complitex.flexbuh.common.service.user.UserFilter;
import org.complitex.flexbuh.common.template.TemplatePage;
import org.complitex.flexbuh.common.template.toolbar.AddUserButton;
import org.complitex.flexbuh.common.template.toolbar.ToolbarButton;
import org.complitex.flexbuh.common.web.component.BookmarkablePageLinkPanel;
import org.complitex.flexbuh.common.web.component.datatable.DataProvider;
import org.complitex.flexbuh.common.web.component.paging.PagingNavigator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 19.12.11 12:07
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class UserList extends TemplatePage {

    private final static Logger log = LoggerFactory.getLogger(UserList.class);

    @EJB
    private UserBean userBean;

    public UserList() {

        add(new Label("title", new ResourceModel("title")));
        add(new FeedbackPanel("messages"));

        log.debug("get user: {}", userBean.getUser(1L));

        //Фильтр
        UserFilter filterObject = new UserFilter();
        final IModel<UserFilter> filterModel = new Model<UserFilter>(filterObject);

        final Form filterForm = new Form("filter_form");
        filterForm.setOutputMarkupId(true);
        add(filterForm);

        Link filterReset = new Link("reset") {

            @Override
            public void onClick() {
                filterForm.clearInput();

                filterModel.setObject(new UserFilter());
            }
        };
        filterForm.add(filterReset);

        filterForm.add(new TextField<String>("login", new PropertyModel<String>(filterModel, "login")));
        filterForm.add(new TextField<String>("last_name", new PropertyModel<String>(filterModel, "lastName")));
        filterForm.add(new TextField<String>("first_name", new PropertyModel<String>(filterModel, "firstName")));
        filterForm.add(new TextField<String>("middle_name", new PropertyModel<String>(filterModel, "middleName")));
        filterForm.add(new TextField<String>("address", new PropertyModel<String>(filterModel, "address")));

        filterForm.add(new DropDownChoice<String>("roles",
                new PropertyModel<String>(filterModel, "role"),
                new ListModel<String>(userBean.getAllRoles()),
                new IChoiceRenderer<String>() {

                    @Override
                    public Object getDisplayValue(String object) {
                        return getStringOrKey(object);
                    }

                    @Override
                    public String getIdValue(String object, int index) {
                        return object;
                    }
                }));

        //Модель
        final DataProvider<User> dataProvider = new DataProvider<User>() {

            @Override
            protected Iterable<? extends User> getData(int first, int count) {
                UserFilter filter = filterModel.getObject();
                filter.setFirst(first);
                filter.setCount(count);
                filter.setSortProperty(getSort().getProperty());

                filter.setAscending(getSort().isAscending());
                return userBean.getUsers(filter);
            }

            @Override
            protected int getSize() {
                return userBean.getUsersCount(filterModel.getObject());
            }
        };
        dataProvider.setSort("login", SortOrder.ASCENDING);

        //Таблица
        DataView<User> dataView = new DataView<User>("users", dataProvider, 10) {

            @Override
            protected void populateItem(Item<User> item) {
                User user = item.getModelObject();

                item.add(new Label("login", user.getLogin()));

                item.add(new Label("role", getDisplayRoleNames(user)));

                item.add(new Label("last_name", user.getLastName()));

                item.add(new Label("first_name", user.getLastName()));

                item.add(new Label("middle_name", user.getLastName()));

                item.add(new Label("address", user.getLastName()));

                PageParameters pageParameters = new PageParameters();

                pageParameters.set("user_id", user.getId());
                item.add(new BookmarkablePageLinkPanel<User>("action_edit", getString("action_edit"),
                        UserEdit.class, pageParameters));

//                pageParameters.set("action", "copy");
//                item.add(new BookmarkablePageLinkPanel<User>("action_copy", getString("action_copy"),
//                        UserEdit.class, pageParameters));
            }
        };
        filterForm.add(dataView);

        //Названия колонок и сортировка
        filterForm.add(new OrderByBorder("header.login", "login", dataProvider));
        filterForm.add(new OrderByBorder("header.last_name", "lastName", dataProvider));
        filterForm.add(new OrderByBorder("header.first_name", "firstName", dataProvider));
        filterForm.add(new OrderByBorder("header.middle_name", "middleName", dataProvider));
        filterForm.add(new OrderByBorder("header.address", "address", dataProvider));

        //Постраничная навигация
        filterForm.add(new PagingNavigator("paging", dataView, UserList.class.getName(), filterForm));
    }

    /**
     * Генерирует строку списка групп пользователей для отображения
     * @param user Пользователь
     * @return Список групп
     */
    private String getDisplayRoleNames(User user) {
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            return getString("blocked");
        }

        StringBuilder sb = new StringBuilder();

        for (Iterator<String> it = user.getRoles().iterator();;) {
            sb.append(getString(it.next()));
            if (!it.hasNext()) {
                return sb.toString();
            }
            sb.append(", ");
        }
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        return Arrays.asList((ToolbarButton) new AddUserButton(id) {

            @Override
            protected void onClick() {
                setResponsePage(UserEdit.class);
            }
        });
    }
}
