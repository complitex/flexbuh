package org.complitex.flexbuh.personnel.web.component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.flexbuh.common.template.toolbar.AddItemButton;
import org.complitex.flexbuh.common.template.toolbar.DeleteItemButton;
import org.complitex.flexbuh.common.template.toolbar.ToolbarButton;
import org.complitex.flexbuh.common.web.component.BookmarkablePageLinkPanel;
import org.complitex.flexbuh.common.web.component.datatable.DataProvider;
import org.complitex.flexbuh.common.web.component.paging.PagingNavigator;
import org.complitex.flexbuh.personnel.entity.Organization;
import org.complitex.flexbuh.personnel.entity.Allowance;
import org.complitex.flexbuh.personnel.entity.AllowanceFilter;
import org.complitex.flexbuh.personnel.service.AllowanceBean;
import org.complitex.flexbuh.personnel.web.OrganizationEdit;
import org.complitex.flexbuh.personnel.web.AllowanceEdit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.complitex.flexbuh.personnel.web.AllowanceEdit.PARAM_ALLOWANCE_ID;

/**
 * @author Pavel Sknar
 *         Date: 18.10.12 15:13
 */
public class AllowanceListPanel extends Panel {

    private final static Logger log = LoggerFactory.getLogger(AllowanceListPanel.class);

    @EJB
    private AllowanceBean allowanceBean;

    private AllowanceFilter filter = new AllowanceFilter();

    private AllowanceFilter oldFilter;

    private AllowanceFilter initialFilter;

    private Form<AllowanceFilter> filterForm;

    private Map<Long, IModel<Boolean>> selectMap = Maps.newHashMap();

    private boolean enabled = true;

    private BookmarkablePageLink<Allowance> addButton;

    private AjaxButton deleteButton;

    public AllowanceListPanel(@NotNull String id, final Long sessionId) {
        super(id);
        init(null, sessionId);
    }

    public AllowanceListPanel(@NotNull String id, Organization organization) {
        super(id);
        init(organization, null);
    }

    private void init(final Organization organization, final Long sessionId) {
        initFilterData(organization, sessionId);
        try {
            initialFilter = (AllowanceFilter) BeanUtils.cloneBean(filter);
        } catch (ReflectiveOperationException e) {
            log.error("Can not create initial filter. Link will use", e);
            initialFilter = filter;
        }
        if (organization != null) {
            enabled = organization.getCompletionDate() == null;
        }

        filterForm = new Form<>("allowance_filter_form", new CompoundPropertyModel<>(filter));
        filterForm.setOutputMarkupId(true);
        add(filterForm);

        Link filterReset = new Link("reset") {

            @Override
            public void onClick() {
                filterForm.clearInput();
                filter = clearFilter();
                filterForm.setModel(new CompoundPropertyModel<>(filter));
            }
        };
        filterForm.add(filterReset);

        filterForm.add(new TextField<String>("type"));

        //Модель
        final DataProvider<Allowance> dataProvider = new DataProvider<Allowance>() {

            @Override
            protected Iterable<Allowance> getData(int first, int count) {
                filter.setFirst(first);
                filter.setCount(count);
                filter.setSortProperty(getSort().getProperty());

                filter.setAscending(getSort().isAscending());
                return allowanceBean.getTDOObjects(filter);
            }

            @Override
            protected int getSize() {
                return allowanceBean.getAllowancesCount(filter);
            }
        };
        dataProvider.setSort("type", SortOrder.ASCENDING);

        //Таблица
        DataView<Allowance> dataView = new DataView<Allowance>("allowances", dataProvider, 10) {

            @Override
            protected void populateItem(Item<Allowance> item) {
                Allowance allowance = item.getModelObject();

                IModel<Boolean> selectModel = new Model<>(false);

                selectMap.put(allowance.getId(), selectModel);

                item.add(new CheckBox("select", selectModel)
                    .add(new AjaxFormComponentUpdatingBehavior("onchange") {
                        @Override
                        protected void onUpdate(AjaxRequestTarget target) {
                            //update
                        }
                    }).setEnabled(!allowance.isDeleted()));

                item.add(new Label("type", allowance.getType()));
                item.add(new Label("value", Float.toString(allowance.getValue())));
                item.add(new Label("calculation_unit", allowance.getCalculationUnit()));

                PageParameters pageParameters = new PageParameters();

                pageParameters.set(PARAM_ALLOWANCE_ID, allowance.getId());
                item.add(new BookmarkablePageLinkPanel<Allowance>("action",
                        getString(allowance.isDeleted() || allowance.getSessionId() == null? "action_view": "action_edit"),
                        AllowanceEdit.class, pageParameters));
            }

            @Override
            protected Item<Allowance> newItem(String id, int index, final IModel<Allowance> model) {
                return new Item<Allowance>(id, index, model) {
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
        //filterForm.add(new OrderByBorder("header.comment", "comment", dataProvider));

        //Постраничная навигация
        filterForm.add(new PagingNavigator("paging", dataView, AllowanceListPanel.class.getName(), filterForm).setOutputMarkupId(true));

        PageParameters pageParameters = new PageParameters();
        if (filter.getOrganizationId() != null) {
            pageParameters.set(OrganizationEdit.PARAM_ORGANIZATION_ID, filter.getOrganizationId());
        }

        // Кнопка добавить
        addButton = new BookmarkablePageLink<Allowance>("add", AllowanceEdit.class, pageParameters) {

            @Override
            public boolean isEnabled() {
                return super.isEnabled() && isEnabledAction();
            }
        };
        addButton.setVisible(isVisibleAction());
        add(addButton);

        // Кнопка удалить
        deleteButton = new AjaxButton("delete") {

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                deleteSelectedAllowances();
                //initFilterData(organization);
                target.add(filterForm);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {

            }

            @Override
            public boolean isEnabled() {
                return super.isEnabled() && isEnabledAction();
            }
        };
        deleteButton.setVisible(isVisibleAction());
        add(deleteButton);
    }

    private void deleteSelectedAllowances() {
        List<Long> deleted = Lists.newArrayList();
        for (Map.Entry<Long, IModel<Boolean>> entry : selectMap.entrySet()) {
            if (entry.getValue().getObject()) {
                deleted.add(entry.getKey());
                Allowance allowance = allowanceBean.getTDObject(entry.getKey());
                if (allowance != null) {
                    allowanceBean.deleteAllowance(allowance);
                } else {
                    log.error("Could not delete tree item {}: did not find", entry.getKey());
                }
            }
        }
        for (Long id : deleted) {
            selectMap.remove(id);
        }
    }

    private void initFilterData(Organization organization, Long sessionId) {
        if (organization == null || organization.getCompletionDate() == null) {
            filter.setCurrentDate(new Date());
        } else {
            filter.setCurrentDate(organization.getCompletionDate());
        }
        if (organization != null) {
            filter.setOrganizationId(organization.getId());
        } else {
            filter.setOrganizationId(0L);
        }
        filter.setSessionId(sessionId);
    }

    private AllowanceFilter clearFilter() {
        try {
            return (AllowanceFilter)BeanUtils.cloneBean(initialFilter);
        } catch (ReflectiveOperationException e) {
            log.error("Can not copy allowance filter", e);
        }
        return filter;
    }

    private boolean isEnabledAction() {
        return enabled;
    }

    private boolean isVisibleAction() {
        return true;
    }

    public void updateState(Date currentDate, boolean enabled) {

        this.enabled = enabled;
        try {
            oldFilter = (AllowanceFilter)BeanUtils.cloneBean(filter);
        } catch (ReflectiveOperationException e) {
            log.error("Can not copy position filter", e);
            return;
        }

        filter.setCurrentDate(currentDate);

        filterForm.setModel(new CompoundPropertyModel<>(filter));
    }

    public ToolbarButton getAddToolbarButton(String id) {
        return new AddItemButton(id) {
            @Override
            protected void onClick() {
                setResponsePage(AllowanceEdit.class, filter.getOrganizationId() != null ? new PageParameters().
                        add(OrganizationEdit.PARAM_ORGANIZATION_ID, filter.getOrganizationId()): null);
            }

            @Override
            public boolean isEnabled() {
                return isEnabledAction();
            }
        };
    }

    public ToolbarButton getDeleteToolbarButton(String id) {
        return new DeleteItemButton(id) {
            @Override
            protected void onClick() {
                deleteSelectedAllowances();
            }

            @Override
            public boolean isEnabled() {
                return isEnabledAction();
            }
        };
    }

    public void invisibleAddButtonInForm() {
        addButton.setVisible(false);
    }

    public void invisibleDeleteButtonInForm() {
        deleteButton.setVisible(false);
    }
}