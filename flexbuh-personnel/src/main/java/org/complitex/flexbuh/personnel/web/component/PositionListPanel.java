package org.complitex.flexbuh.personnel.web.component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxEventBehavior;
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
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.flexbuh.common.util.StringUtil;
import org.complitex.flexbuh.common.web.component.BookmarkablePageLinkPanel;
import org.complitex.flexbuh.common.web.component.datatable.DataProvider;
import org.complitex.flexbuh.common.web.component.paging.PagingNavigator;
import org.complitex.flexbuh.personnel.entity.*;
import org.complitex.flexbuh.personnel.service.PositionBean;
import org.complitex.flexbuh.personnel.web.DepartmentEdit;
import org.complitex.flexbuh.personnel.web.OrganizationEdit;
import org.complitex.flexbuh.personnel.web.PositionEdit;
import org.odlabs.wiquery.ui.accordion.Accordion;
import org.odlabs.wiquery.ui.accordion.AccordionActive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.complitex.flexbuh.personnel.web.PositionEdit.PARAM_POSITION_ID;

/**
 * @author Pavel Sknar
 *         Date: 25.07.12 15:31
 */
public class PositionListPanel extends Panel {

    private final static Logger log = LoggerFactory.getLogger(PositionListPanel.class);

    @EJB
    private PositionBean positionBean;

    private Accordion position;

    private ClickAjaxBehavior clickBehavior;

    private PositionFilter initialFilter;

    private PositionFilter filter = new PositionFilter();

    private PositionFilter oldFilter;

    private Form<PositionFilter> filterForm;

    private Map<Long, IModel<Boolean>> selectMap = Maps.newHashMap();

    private boolean enabled = true;

    private boolean collapsedPanel = true;

    public PositionListPanel(@NotNull String id, @NotNull Organization organization) {
        this(id, organization, true);
    }

    public PositionListPanel(@NotNull String id, @NotNull Organization organization, boolean collapsedPanel) {
        super(id);
        this.collapsedPanel = collapsedPanel;
        init(organization);
    }

    public PositionListPanel(@NotNull String id, @NotNull Department department) {
        this(id, department, true);
    }

    public PositionListPanel(@NotNull String id, @NotNull Department department, boolean collapsedPanel) {
        super(id);
        this.collapsedPanel = collapsedPanel;
        filter.setDepartmentId(department.getId());
        init(department.getOrganization());
    }

    private void init(final Organization organization) {
        enabled = organization.getCompletionDate() == null;
        filter.setOrganizationId(organization.getId());
        initFilterDate(organization);
        try {
            initialFilter = (PositionFilter)BeanUtils.cloneBean(filter);
        } catch (ReflectiveOperationException e) {
            log.error("Can not create initial filter. Link will use", e);
            initialFilter = filter;
        }

        clickBehavior = new ClickAjaxBehavior(false);

        position = new Accordion("position");
        position.add(new Label("position_title", getString("legend_position")).add(clickBehavior));
        position.setCollapsible(true);
        position.setClearStyle(true);
        position.setNavigation(true);
        if (collapsedPanel) {
            position.setActive(new AccordionActive(false));
        }

        add(position);

        position.add(new FeedbackPanel("messages"));

        filterForm = new Form<>("position_filter_form", new CompoundPropertyModel<>(filter));
        filterForm.setOutputMarkupId(true);
        position.add(filterForm);

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
        filterForm.add(new TextField<String>("code"));
        filterForm.add(new TextField<String>("description"));

        //Модель
        final DataProvider<Position> dataProvider = new DataProvider<Position>() {

            @Override
            protected Iterable<Position> getData(int first, int count) {
                filter.setFirst(first);
                filter.setCount(count);
                filter.setSortProperty(getSort().getProperty());

                filter.setAscending(getSort().isAscending());
                return filter.getOrganizationId() != null? positionBean.getTDOObjects(filter): Collections.<Position>emptyList();
            }

            @Override
            protected int getSize() {
                return filter.getOrganizationId() != null? positionBean.getPositionsCount(filter): 0;
            }
        };
        dataProvider.setSort("name", SortOrder.ASCENDING);

        //Таблица
        DataView<Position> dataView = new DataView<Position>("organizations", dataProvider, 10) {

            @Override
            protected void populateItem(Item<Position> item) {
                Position position = item.getModelObject();

                IModel<Boolean> selectModel = new Model<>(false);

                selectMap.put(position.getId(), selectModel);

                item.add(new CheckBox("select", selectModel)
                    .add(new AjaxFormComponentUpdatingBehavior("onchange") {
                        @Override
                        protected void onUpdate(AjaxRequestTarget target) {
                            //update
                        }
                    }).setEnabled(!position.isDeleted()));

                item.add(new Label("name", position.getName()));
                item.add(new Label("code", position.getCode()));

                // Show description (can be defined on department attributes)
                item.add(new Label("description", position.getDepartmentAttributes() != null &&
                        StringUtils.isNotEmpty(position.getDepartmentAttributes().getDescription())?
                        position.getDepartmentAttributes().getDescription() :
                        position.getDescription()));

                // Show schedule (can be defined on department attributes)
                String departmentSchedule = position.getDepartmentAttributes() != null?
                        scheduleToString(position.getDepartmentAttributes().getSchedule()): "";
                item.add(new Label("schedule",
                        StringUtils.equals(departmentSchedule, "")?
                        scheduleToString(position.getSchedule()) : departmentSchedule
                        ));

                // Show payment (can be defined on department attributes)
                String departmentPayment = position.getDepartmentAttributes() != null?
                        paymentToString(position.getDepartmentAttributes().getPayment()): "";
                item.add(new Label("payment",
                        StringUtils.equals(departmentPayment, "")?
                        paymentToString(position.getPayment()) : departmentPayment
                        ));

                PageParameters pageParameters = new PageParameters();

                pageParameters.set(PARAM_POSITION_ID, position.getId());
                if (filter.getDepartmentId() != null) {
                    pageParameters.set(DepartmentEdit.PARAM_DEPARTMENT_ID, filter.getDepartmentId());
                }
                item.add(new BookmarkablePageLinkPanel<Position>("action",
                        getString(position.isDeleted()? "action_view": "action_edit"),
                        PositionEdit.class, pageParameters));
            }

            @Override
            protected Item<Position> newItem(String id, int index, final IModel<Position> model) {
                return new Item<Position>(id, index, model) {
                    @Override
                    protected void onComponentTag(ComponentTag tag) {
                        super.onComponentTag(tag);
                        if (model.getObject().isDeleted()) {
                            tag.put("class", "deleted");
                        }
                    }
                };
            }

            private String paymentToString(Payment payment) {
                return payment == null? "":
                        (payment.getSalary() != null? payment.getSalary() + "\u00A0": "") +
                                StringUtil.emptyOnNull(payment.getCurrencyUnit());
            }

            private String scheduleToString(Schedule schedule) {
                return schedule == null? "": StringUtil.emptyOnNull(schedule.getName());
            }
        };
        filterForm.add(dataView);

        //Названия колонок и сортировка
        filterForm.add(new OrderByBorder("header.name", "name", dataProvider));
        filterForm.add(new OrderByBorder("header.code", "code", dataProvider));
        filterForm.add(new OrderByBorder("header.description", "description", dataProvider));

        //Постраничная навигация
        filterForm.add(new PagingNavigator("paging", dataView, PositionListPanel.class.getName(), filterForm).setOutputMarkupId(true));

        PageParameters pageParameters = new PageParameters();
        if (filter.getDepartmentId() != null) {
            pageParameters.set(DepartmentEdit.PARAM_DEPARTMENT_ID, filter.getDepartmentId());
        }
        if (filter.getOrganizationId() != null) {
            pageParameters.set(OrganizationEdit.PARAM_ORGANIZATION_ID, filter.getOrganizationId());
        }
        position.add(new BookmarkablePageLink<Position>("add", PositionEdit.class, pageParameters) {

            @Override
            public boolean isEnabled() {
                return isEnabledAction();
            }
        }.setVisible(isVisibleAction()));
        /*
        position.add(new Link("add") {

            @Override
            public void onClick() {
                PageParameters parameters = new PageParameters();
                if (filter.getDepartmentId() != null) {
                    parameters.set(DepartmentEdit.PARAM_DEPARTMENT_ID, filter.getDepartmentId());
                }
                if (filter.getOrganizationId() != null) {
                    parameters.set(OrganizationEdit.PARAM_ORGANIZATION_ID, filter.getOrganizationId());
                }
                setResponsePage(PositionEdit.class, parameters);
            }

            @Override
            public boolean isEnabled() {
                return isEnabledAction();
            }
        }.setVisible(isVisibleAction()));
        */
        position.add(new AjaxButton("delete") {

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                List<Long> deleted = Lists.newArrayList();
                for (Map.Entry<Long, IModel<Boolean>> entry : selectMap.entrySet()) {
                    if (entry.getValue().getObject()) {
                        deleted.add(entry.getKey());
                        Position position = positionBean.getTDObject(entry.getKey());
                        if (position != null) {
                            positionBean.deletePosition(position);
                        } else {
                            log.error("Could not delete tree item {}: did not find",  entry.getKey());
                        }
                    }
                }
                for (Long id : deleted) {
                    selectMap.remove(id);
                }
                initFilterDate(organization);
                target.add(filterForm);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {

            }

            @Override
            public boolean isEnabled() {
                return isEnabledAction();
            }
        }.setVisible(isVisibleAction()));
    }

    private boolean isEnabledAction() {
        return enabled;
    }

    private boolean isVisibleAction() {
        return true;
    }

    private PositionFilter clearFilter() {
        try {
            return (PositionFilter)BeanUtils.cloneBean(initialFilter);
        } catch (ReflectiveOperationException e) {
            log.error("Can not copy position filter", e);
        }
        return filter;
    }

    public void updateState(Date currentDate, boolean enabled) {

        this.enabled = enabled;
        try {
            oldFilter = (PositionFilter)BeanUtils.cloneBean(filter);
        } catch (ReflectiveOperationException e) {
            log.error("Can not copy position filter", e);
            return;
        }

        filter.setCurrentDate(currentDate);

        filterForm.setModel(new CompoundPropertyModel<>(filter));
        if (clickBehavior.isOpened()) {
            position.setActive(null);
        } else if (position.getActive() == null) {
            position.setActive(new AccordionActive(false));
        }
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

    private void initFilterDate(Organization organization) {
        if (organization.getCompletionDate() == null) {
            filter.setCurrentDate(new Date());
        } else {
            filter.setCurrentDate(organization.getCompletionDate());
        }
    }
}
