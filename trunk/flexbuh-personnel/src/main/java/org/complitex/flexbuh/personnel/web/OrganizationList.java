package org.complitex.flexbuh.personnel.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.*;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.flexbuh.common.entity.ShowMode;
import org.complitex.flexbuh.common.entity.organization.OrganizationBase;
import org.complitex.flexbuh.common.logging.Event;
import org.complitex.flexbuh.common.logging.EventCategory;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.common.service.organization.OrganizationBaseBean;
import org.complitex.flexbuh.common.service.user.UserBean;
import org.complitex.flexbuh.common.template.TemplatePage;
import org.complitex.flexbuh.common.template.toolbar.AddOrganizationButton;
import org.complitex.flexbuh.common.template.toolbar.DeleteItemButton;
import org.complitex.flexbuh.common.template.toolbar.ToolbarButton;
import org.complitex.flexbuh.common.template.toolbar.search.CollapsibleSearchToolbarButton;
import org.complitex.flexbuh.common.web.component.BookmarkablePageLinkPanel;
import org.complitex.flexbuh.common.web.component.datatable.DataProvider;
import org.complitex.flexbuh.common.web.component.paging.PagingNavigator;
import org.complitex.flexbuh.personnel.entity.Organization;
import org.complitex.flexbuh.personnel.entity.OrganizationFilter;
import org.complitex.flexbuh.personnel.service.OrganizationBean;
import org.complitex.flexbuh.personnel.service.OrganizationTypeBean;
import org.complitex.flexbuh.personnel.web.component.OrganizationTypeAutoCompleteTextField;
import org.complitex.flexbuh.common.web.component.search.ShowModePanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.List;
import java.util.Map;

import static org.complitex.flexbuh.personnel.web.OrganizationEdit.*;

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
    private OrganizationBaseBean organizationBaseBean;

    @EJB
    private UserBean userBean;

    OrganizationFilter filter;

    private Map<Long, IModel<Boolean>> selectMap = Maps.newHashMap();

    private ShowModePanel showModePanel;

    public OrganizationList() {
        add(new Label("title", new ResourceModel("title")));
        add(new FeedbackPanel("messages"));

        //Фильтр
        filter = new OrganizationFilter();
        initFilter(filter);

        final Form<OrganizationFilter> filterForm = new Form<>("filter_form", new CompoundPropertyModel<>(filter));
        filterForm.setOutputMarkupId(true);
        add(filterForm);

        Link filterReset = new Link("reset") {

            @Override
            public void onClick() {
                filterForm.clearInput();

                filter = new OrganizationFilter();
                initFilter(filter);

                filterForm.setModel(new CompoundPropertyModel<>(filter));
            }
        };
        filterForm.add(filterReset);

        filterForm.add(getShowModePanel());
        filterForm.add(new OrganizationTypeAutoCompleteTextField("type"));
        filterForm.add(new TextField<String>("name"));
        filterForm.add(new TextField<String>("phone"));
        filterForm.add(new TextField<String>("email"));
        filterForm.add(new TextField<String>("physicalAddress"));
        filterForm.add(new TextField<String>("juridicalAddress"));

        //Модель
        final DataProvider<Organization> dataProvider = new DataProvider<Organization>() {

            @Override
            protected Iterable<? extends Organization> getData(int first, int count) {
                filter.setFirst(first);
                filter.setCount(count);
                filter.setSortProperty(getSort().getProperty());

                filter.setAscending(getSort().isAscending());
                return organizationBean.getTDOObjects(filter);
            }

            @Override
            protected int getSize() {
                return organizationBean.getOrganizationsCount(filter);
            }
        };
        dataProvider.setSort("name", SortOrder.ASCENDING);

        //Таблица
        DataView<Organization> dataView = new DataView<Organization>("organizations", dataProvider, 10) {

            @Override
            protected void populateItem(Item<Organization> item) {
                Organization organization = item.getModelObject();

                IModel<Boolean> selectModel = new Model<>(false);

                selectMap.put(organization.getId(), selectModel);

                item.add(new CheckBox("select", selectModel)
                    .add(new AjaxFormComponentUpdatingBehavior("onchange") {
                        @Override
                        protected void onUpdate(AjaxRequestTarget target) {
                            //update
                        }
                    }).setEnabled(!organization.isDeleted()));

                item.add(new Label("type", organization.getType()));
                item.add(new Label("name", organization.getName()));
                item.add(new Label("phone", organization.getPhone()));
                item.add(new Label("email", organization.getEmail()));
                item.add(new Label("physical_address", organization.getPhysicalAddress() == null? "":
                        organization.getPhysicalAddress().toWebView()));
                item.add(new Label("juridical_address", organization.getJuridicalAddress() == null? "":
                        organization.getJuridicalAddress().toWebView()));

                PageParameters pageParameters = new PageParameters();

                pageParameters.set(PARAM_ORGANIZATION_ID, organization.getId());
                item.add(new BookmarkablePageLinkPanel<Organization>("action",
                        getString(organization.isDeleted()? "action_view": "action_edit"),
                        OrganizationEdit.class, pageParameters));
            }

            @Override
            protected Item<Organization> newItem(String id, int index, final IModel<Organization> model) {
                return new Item<Organization>(id, index, model) {
                    @Override
                    protected void onComponentTag(ComponentTag tag) {
                        super.onComponentTag(tag);
                        if (model.getObject().isDeleted()) {
                            tag.put("class", "deleted");
                        }
                    }
                };
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

    private ShowModePanel getShowModePanel() {
        if (showModePanel == null) {
            showModePanel = new ShowModePanel("showModePanel");
        }
        return showModePanel;
    }

    private void initFilter(OrganizationFilter filter) {
        filter.setShowMode(ShowMode.ALL);
        if (getTemplateWebApplication().hasAnyRole(SecurityRole.ADMIN_MODULE_EDIT)) {
            return;
        }
        List<Long> organizationIds = Lists.newArrayList();
        for (OrganizationBase organizationBase : organizationBaseBean.getUserOrganizations(userBean.getCurrentUser())) {
            organizationIds.add(organizationBase.getId());
        }
        filter.setOrganizationIds(organizationIds);
    }

    @Override
    protected List<? extends ToolbarButton> getToolbarButtons(String id) {
        List<ToolbarButton> list = Lists.newArrayList();

        if (getTemplateWebApplication().hasAnyRole(SecurityRole.ADMIN_MODULE_EDIT)) {
            list.add(new AddOrganizationButton(id) {
                @Override
                protected void onClick() {
                    setResponsePage(OrganizationCreate.class);
                }
            });

            list.add(new DeleteItemButton(id){
                @Override
                protected void onClick() {
                    for (Map.Entry<Long, IModel<Boolean>> select : selectMap.entrySet()) {
                        if (select.getValue().getObject()) {
                            Long id = select.getKey();
                            Organization organization = organizationBean.getTDObject(id);
                            organizationBean.deleteOrganization(organization);

                            log.debug("Удаление организации", new Event(EventCategory.REMOVE, organization, null));
                        }
                    }
                }
            });
        }

        list.add(new CollapsibleSearchToolbarButton(id, getShowModePanel()));

        return list;
    }
}
