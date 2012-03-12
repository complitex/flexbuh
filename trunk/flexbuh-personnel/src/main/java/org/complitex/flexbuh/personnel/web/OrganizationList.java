package org.complitex.flexbuh.personnel.web;

import com.google.common.collect.Lists;
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
import org.complitex.flexbuh.common.entity.organization.Organization;
import org.complitex.flexbuh.common.entity.organization.OrganizationBase;
import org.complitex.flexbuh.common.entity.organization.OrganizationFilter;
import org.complitex.flexbuh.common.entity.organization.OrganizationType;
import org.complitex.flexbuh.common.entity.user.User;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.common.service.organization.OrganizationBean;
import org.complitex.flexbuh.common.service.organization.OrganizationTypeBean;
import org.complitex.flexbuh.common.service.user.UserBean;
import org.complitex.flexbuh.common.service.user.UserFilter;
import org.complitex.flexbuh.common.template.TemplatePage;
import org.complitex.flexbuh.common.web.component.BookmarkablePageLinkPanel;
import org.complitex.flexbuh.common.web.component.OrganizationTypeAutoCompleteTextField;
import org.complitex.flexbuh.common.web.component.datatable.DataProvider;
import org.complitex.flexbuh.common.web.component.paging.PagingNavigator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 05.03.12 16:55
 */
@AuthorizeInstantiation(SecurityRole.PERSONAL_MANAGER)
public class OrganizationList extends TemplatePage {

    private final static Logger log = LoggerFactory.getLogger(OrganizationList.class);

    @EJB
    private OrganizationBean organizationBean;

    @EJB
    private OrganizationTypeBean organizationTypeBean;

    @EJB
    private UserBean userBean;

    public OrganizationList() {
        add(new Label("title", new ResourceModel("title")));
        add(new FeedbackPanel("messages"));

        //Фильтр
        OrganizationFilter filterObject = new OrganizationFilter();
        initFilter(filterObject);
        final IModel<OrganizationFilter> filterModel = new Model<OrganizationFilter>(filterObject);

        final Form filterForm = new Form("filter_form");
        filterForm.setOutputMarkupId(true);
        add(filterForm);

        Link filterReset = new Link("reset") {

            @Override
            public void onClick() {
                filterForm.clearInput();

                OrganizationFilter filterObject = new OrganizationFilter();
                initFilter(filterObject);

                filterModel.setObject(filterObject);
            }
        };
        filterForm.add(filterReset);

        filterForm.add(new OrganizationTypeAutoCompleteTextField("type", new PropertyModel<String>(filterModel, "type")));
        filterForm.add(new TextField<String>("name", new PropertyModel<String>(filterModel, "name")));
        filterForm.add(new TextField<String>("phone", new PropertyModel<String>(filterModel, "phone")));
        filterForm.add(new TextField<String>("email", new PropertyModel<String>(filterModel, "email")));
        filterForm.add(new TextField<String>("physical_address", new PropertyModel<String>(filterModel, "physicalAddress")));
        filterForm.add(new TextField<String>("juridical_address", new PropertyModel<String>(filterModel, "juridicalAddress")));

        //Модель
        final DataProvider<Organization> dataProvider = new DataProvider<Organization>() {

            @Override
            protected Iterable<? extends Organization> getData(int first, int count) {
                OrganizationFilter filter = filterModel.getObject();
                filter.setFirst(first);
                filter.setCount(count);
                filter.setSortProperty(getSort().getProperty());

                filter.setAscending(getSort().isAscending());
                return organizationBean.getOrganizations(filter);
            }

            @Override
            protected int getSize() {
                return organizationBean.getOrganizationsCount(filterModel.getObject());
            }
        };
        dataProvider.setSort("name", SortOrder.ASCENDING);

        //Таблица
        DataView<Organization> dataView = new DataView<Organization>("organizations", dataProvider, 10) {

            @Override
            protected void populateItem(Item<Organization> item) {
                Organization organization = item.getModelObject();

                item.add(new Label("type", organization.getType()));
                item.add(new Label("name", organization.getName()));
                item.add(new Label("phone", organization.getPhone()));
                item.add(new Label("email", organization.getEmail()));
                item.add(new Label("physical_address", organization.getPhysicalAddress() == null? "":
                        organization.getPhysicalAddress().toWebView()));
                item.add(new Label("juridical_address", organization.getJuridicalAddress() == null? "":
                        organization.getJuridicalAddress().toWebView()));

                PageParameters pageParameters = new PageParameters();

                pageParameters.set("organization_id", organization.getId());
                item.add(new BookmarkablePageLinkPanel<Organization>("action_edit", getString("action_edit"),
                        OrganizationEdit.class, pageParameters));
            }
        };
        filterForm.add(dataView);

        //Названия колонок и сортировка
        filterForm.add(new OrderByBorder("header.type", "type", dataProvider));
        filterForm.add(new OrderByBorder("header.name", "name", dataProvider));
        filterForm.add(new OrderByBorder("header.phone", "phone", dataProvider));
        filterForm.add(new OrderByBorder("header.email", "email", dataProvider));
        filterForm.add(new OrderByBorder("header.physical_address", "physical_address", dataProvider));
        filterForm.add(new OrderByBorder("header.juridical_address", "juridical_address", dataProvider));

        //Постраничная навигация
        filterForm.add(new PagingNavigator("paging", dataView, OrganizationList.class.getName(), filterForm));

    }

    private void initFilter(OrganizationFilter filter) {
        if (getTemplateWebApplication().hasAnyRole(SecurityRole.ADMIN_MODULE_EDIT)) {
            return;
        }
        List<Long> organizationIds = Lists.newArrayList();
        for (OrganizationBase organizationBase : userBean.getCurrentUser().getOrganizations()) {
            organizationIds.add(organizationBase.getId());
        }
        filter.setOrganizationIds(organizationIds);
    }
}
