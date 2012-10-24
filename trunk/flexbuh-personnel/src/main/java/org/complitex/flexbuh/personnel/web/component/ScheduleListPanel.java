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
import org.complitex.flexbuh.personnel.entity.Schedule;
import org.complitex.flexbuh.personnel.entity.ScheduleFilter;
import org.complitex.flexbuh.personnel.service.ScheduleBean;
import org.complitex.flexbuh.personnel.web.OrganizationEdit;
import org.complitex.flexbuh.personnel.web.ScheduleEdit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.complitex.flexbuh.personnel.web.ScheduleEdit.PARAM_SCHEDULE_ID;

/**
 * @author Pavel Sknar
 *         Date: 02.10.12 19:05
 */
public class ScheduleListPanel extends Panel {

    private final static Logger log = LoggerFactory.getLogger(PositionListPanel.class);

    @EJB
    private ScheduleBean scheduleBean;

    private ScheduleFilter filter = new ScheduleFilter();

    private ScheduleFilter oldFilter;

    private ScheduleFilter initialFilter;

    private Form<ScheduleFilter> filterForm;

    private Map<Long, IModel<Boolean>> selectMap = Maps.newHashMap();

    private boolean enabled = true;

    private BookmarkablePageLink<Schedule> addButton;

    private AjaxButton deleteButton;

    public ScheduleListPanel(@NotNull String id, boolean admin, final Long sessionId) {
        super(id);
        init(null, admin, sessionId);
    }

    public ScheduleListPanel(@NotNull String id, Organization organization) {
        super(id);
        init(organization, false, null);
    }

    private void init(final Organization organization, final boolean admin, final Long sessionId) {
        initFilterData(organization, admin, sessionId);
        try {
            initialFilter = (ScheduleFilter) BeanUtils.cloneBean(filter);
        } catch (ReflectiveOperationException e) {
            log.error("Can not create initial filter. Link will use", e);
            initialFilter = filter;
        }
        if (organization != null) {
            enabled = organization.getCompletionDate() == null;
        }

        filterForm = new Form<>("schedule_filter_form", new CompoundPropertyModel<>(filter));
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

        filterForm.add(new TextField<String>("name"));
        filterForm.add(new TextField<String>("comment"));

        //Модель
        final DataProvider<Schedule> dataProvider = new DataProvider<Schedule>() {

            @Override
            protected Iterable<Schedule> getData(int first, int count) {
                filter.setFirst(first);
                filter.setCount(count);
                filter.setSortProperty(getSort().getProperty());

                filter.setAscending(getSort().isAscending());
                return scheduleBean.getTDOObjects(filter);
            }

            @Override
            protected int getSize() {
                return scheduleBean.getSchedulesCount(filter);
            }
        };
        dataProvider.setSort("name", SortOrder.ASCENDING);

        //Таблица
        DataView<Schedule> dataView = new DataView<Schedule>("schedules", dataProvider, 10) {

            @Override
            protected void populateItem(Item<Schedule> item) {
                Schedule schedule = item.getModelObject();

                IModel<Boolean> selectModel = new Model<>(false);

                selectMap.put(schedule.getId(), selectModel);

                item.add(new CheckBox("select", selectModel)
                    .add(new AjaxFormComponentUpdatingBehavior("onchange") {
                        @Override
                        protected void onUpdate(AjaxRequestTarget target) {
                            //update
                        }
                    }).setEnabled(!schedule.isDeleted() && (!admin || schedule.getSessionId() != null || organization == null)
                        && (admin || schedule.getSessionId() != null)));

                item.add(new Label("name", schedule.getName()));
                item.add(new Label("comment", schedule.getComment()));

                PageParameters pageParameters = new PageParameters();

                pageParameters.set(PARAM_SCHEDULE_ID, schedule.getId());
                if (organization != null) {
                    pageParameters.set(OrganizationEdit.PARAM_ORGANIZATION_ID, organization.getId());
                }
                item.add(new BookmarkablePageLinkPanel<Schedule>("action",
                        getString(schedule.isDeleted() ||
                                (admin && schedule.getSessionId() == null && organization != null) ||
                                (!admin && schedule.getSessionId() == null) ? "action_view": "action_edit"),
                        ScheduleEdit.class, pageParameters));
            }

            @Override
            protected Item<Schedule> newItem(String id, int index, final IModel<Schedule> model) {
                return new Item<Schedule>(id, index, model) {
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
        filterForm.add(new OrderByBorder("header.name", "name", dataProvider));
        //filterForm.add(new OrderByBorder("header.comment", "comment", dataProvider));

        //Постраничная навигация
        filterForm.add(new PagingNavigator("paging", dataView, ScheduleListPanel.class.getName(), filterForm).setOutputMarkupId(true));

        PageParameters pageParameters = new PageParameters();
        if (filter.getOrganizationId() != null) {
            pageParameters.set(OrganizationEdit.PARAM_ORGANIZATION_ID, filter.getOrganizationId());
        }

        // Кнопка добавить
        addButton = new BookmarkablePageLink<Schedule>("add", ScheduleEdit.class, pageParameters) {

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
                deleteSelectedSchedules();
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

    private void deleteSelectedSchedules() {
        List<Long> deleted = Lists.newArrayList();
        for (Map.Entry<Long, IModel<Boolean>> entry : selectMap.entrySet()) {
            if (entry.getValue().getObject()) {
                deleted.add(entry.getKey());
                Schedule schedule = scheduleBean.getTDObject(entry.getKey());
                if (schedule != null) {
                    scheduleBean.deleteSchedule(schedule);
                } else {
                    log.error("Could not delete tree item {}: did not find", entry.getKey());
                }
            }
        }
        for (Long id : deleted) {
            selectMap.remove(id);
        }
    }

    private void initFilterData(Organization organization, boolean admin, Long sessionId) {
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
        filter.setAdmin(admin);
        filter.setSessionId(sessionId);
    }

    private ScheduleFilter clearFilter() {
        try {
            return (ScheduleFilter)BeanUtils.cloneBean(initialFilter);
        } catch (ReflectiveOperationException e) {
            log.error("Can not copy schedule filter", e);
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
            oldFilter = (ScheduleFilter)BeanUtils.cloneBean(filter);
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
                setResponsePage(ScheduleEdit.class, filter.getOrganizationId() != null && filter.getOrganizationId() > 0 ? new PageParameters().
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
                deleteSelectedSchedules();
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
