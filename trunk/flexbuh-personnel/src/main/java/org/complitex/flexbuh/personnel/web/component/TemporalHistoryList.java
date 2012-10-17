package org.complitex.flexbuh.personnel.web.component;

import org.apache.wicket.Page;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.convert.IConverter;
import org.complitex.flexbuh.common.entity.TemporalDomainObject;
import org.complitex.flexbuh.common.web.component.BookmarkablePageLinkPanel;
import org.complitex.flexbuh.common.web.component.datatable.DataProvider;
import org.complitex.flexbuh.common.web.component.paging.PagingNavigator;
import org.complitex.flexbuh.personnel.entity.Organization;
import org.complitex.flexbuh.personnel.entity.TemporalDomainObjectHistoryFilter;
import org.odlabs.wiquery.ui.accordion.Accordion;
import org.odlabs.wiquery.ui.accordion.AccordionActive;
import org.odlabs.wiquery.ui.datepicker.DatePicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 10.04.12 10:47
 */

public abstract class TemporalHistoryList<T extends TemporalDomainObject> extends Panel {

    private static final Logger log = LoggerFactory.getLogger(TemporalHistoryList.class);

    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    public TemporalHistoryList(String id, final T currentObject) {
        super(id);

        Accordion history = new Accordion("history");
        history.setCollapsible(true);
        history.setClearStyle(true);
        history.setNavigation(true);
        //history.setActive(new AccordionActive(true));
        history.add(new Label("history_title", getString("legend_history")));

        add(history);

        //Фильтр
        TemporalDomainObjectHistoryFilter filterObject = new TemporalDomainObjectHistoryFilter();
        final IModel<TemporalDomainObjectHistoryFilter> filterModel = new Model<>(filterObject);

        final Form filterForm = new Form("filter_form");
        filterForm.setOutputMarkupId(true);
        history.add(filterForm);

        Link filterReset = new Link("filter_reset") {

            @Override
            public void onClick() {
                filterForm.clearInput();

                TemporalDomainObjectHistoryFilter filterObject = new TemporalDomainObjectHistoryFilter();

                filterModel.setObject(filterObject);
            }
        };
        filterForm.add(filterReset);

        filterForm.add(new DatePicker<Date>("beginDateRange", new PropertyModel<Date>(filterModel, "beginDateRange")) {
            @Override
            public <Date> IConverter<Date> getConverter(Class<Date> type) {
                return (IConverter<Date>) new PatternDateConverter("dd.MM.yyyy", true);
            }
        }.setDateFormat("dd.mm.yy"));
        filterForm.add(new DatePicker<Date>("endDateRange", new PropertyModel<Date>(filterModel, "endDateRange")){
            @Override
			public <Date> IConverter<Date> getConverter(Class<Date> type) {
				return (IConverter<Date>)new PatternDateConverter("dd.MM.yyyy", true);
			}
        }.setDateFormat("dd.mm.yy"));
        /*filterForm.add(new DropDownChoice<>("changedProperty", new PropertyModel<String>(filterModel, "changedProperty"),
                getChangedPropertyKeys(),
                new IChoiceRenderer<String>() {

                    @Override
                    public Object getDisplayValue(String object) {
                        return getChangedPropertyString(object);
                    }

                    @Override
                    public String getIdValue(String object, int index) {
                        return object;
                    }
                }));
                */
        //Модель
        final DataProvider<T> dataProvider = new DataProvider<T>() {

            @Override
            protected Iterable<T> getData(int first, int count) {
                TemporalDomainObjectHistoryFilter filter = filterModel.getObject();
                filter.setFirst(first);
                filter.setCount(count);

                //filter.setSortProperty(getSort().getProperty());
                //filter.setAscending(getSort().isAscending());

                return getObjects(filter);
            }

            @Override
            protected int getSize() {
                return getObjectsCount(filterModel.getObject());
            }
        };
        //dataProvider.setSort("name", SortOrder.ASCENDING);

        //Таблица
        DataView<T> dataView = new DataView<T>("temporalDomainObjects", dataProvider, 10) {

            @Override
            protected void populateItem(Item<T> item) {
                TemporalDomainObject object = item.getModelObject();

                item.add(new Label("entry_into_force_date", getStringDate(object.getEntryIntoForceDate())));
                item.add(new Label("completion_date", getStringDate(object.getCompletionDate())));
                item.add(new Label("deleted", object.isDeleted()? getString("deleted"): ""));
                //item.add(new Label("changed_property", getString()));

                PageParameters pageParameters = new PageParameters();

                pageParameters.set("object_id", object.getId());
                pageParameters.set("object_version", object.getVersion());
                item.add(new BookmarkablePageLinkPanel<TemporalHistoryList>("action_edit", getString("action_edit"),
                        getPageClass(), pageParameters) {
                    @Override
                    protected boolean getStatelessHint() {
                        return true;
                    }
                });
            }

            @Override
            protected Item<T> newItem(String id, int index, final IModel<T> model) {
                return new Item<T>(id, index, model) {
                    @Override
                    protected void onComponentTag(ComponentTag tag) {
                        super.onComponentTag(tag);
                        if (model.getObject().getVersion().equals(currentObject.getVersion())) {
                            tag.put("class", "selected");
                        }
                    }
                };
            }
        };
        filterForm.add(dataView);

        getClass();

        //Названия колонок и сортировка
        //filterForm.add(new OrderByBorder("header.entry_into_force_date", "entry_into_force_date", dataProvider));
        //filterForm.add(new OrderByBorder("header.completion_date", "completion_date", dataProvider));

        //Постраничная навигация
        filterForm.add(new PagingNavigator("paging", dataView, TemporalHistoryList.class.getName(), filterForm));
    }

    private String getStringDate(Date date) {
		return date != null? DATE_FORMAT.format(date): "";
	}

    //protected abstract List<String> getChangedPropertyKeys();

    //protected abstract String getChangedPropertyString(String key);

    protected abstract List<T> getObjects(TemporalDomainObjectHistoryFilter filter);

    protected abstract int getObjectsCount(TemporalDomainObjectHistoryFilter filter);

    protected abstract Class<? extends Page> getPageClass();

}
