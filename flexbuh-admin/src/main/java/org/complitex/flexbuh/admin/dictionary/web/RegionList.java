package org.complitex.flexbuh.admin.dictionary.web;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.flexbuh.common.entity.FilterWrapper;
import org.complitex.flexbuh.common.entity.dictionary.Region;
import org.complitex.flexbuh.common.logging.Event;
import org.complitex.flexbuh.common.logging.EventCategory;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.common.service.dictionary.RegionBean;
import org.complitex.flexbuh.common.template.TemplatePage;
import org.complitex.flexbuh.common.template.toolbar.AddDocumentButton;
import org.complitex.flexbuh.common.template.toolbar.ToolbarButton;
import org.complitex.flexbuh.common.web.component.datatable.DataProvider;
import org.complitex.flexbuh.common.web.component.paging.PagingNavigator;
import org.odlabs.wiquery.ui.datepicker.DatePicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 29.08.11 15:25
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class RegionList extends TemplatePage {
    private final static Logger log = LoggerFactory.getLogger(RegionList.class);

    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyyyy");

    @EJB
    RegionBean regionBean;

    public RegionList() {

        add(new FeedbackPanel("messages"));

        //Фильтр модель
        final IModel<Region> filterModel = new CompoundPropertyModel<>(new Region());

        final Form<Region> filterForm = new Form<>("filter_form", filterModel);
        filterForm.setOutputMarkupId(true);
        add(filterForm);

        Link filter_reset = new Link("filter_reset") {

            @Override
            public void onClick() {
                filterForm.clearInput();
                filterModel.setObject(new Region());
            }
        };
        filterForm.add(filter_reset);

        //Document type
        filterForm.add(new TextField<String>("code"));

        //Begin date
        DatePicker<Date> beginDate = new DatePicker<>("beginDate");
        filterForm.add(beginDate);

        //End date
        DatePicker<Date> endDate = new DatePicker<>("endDate");
        filterForm.add(endDate);

        //Ukrainian name
        filterForm.add(new TextField<String>("nameUk"));

        //Модель
        final DataProvider<Region> dataProvider = new DataProvider<Region>() {
            @Override
            protected Iterable<? extends Region> getData(int first, int count) {
                FilterWrapper<Region> filter = FilterWrapper.of(filterModel.getObject());

                filter.setFirst(first);
                filter.setCount(count);
                filter.setSortProperty(getSort().getProperty());
                filter.setAscending(getSort().isAscending());

                return regionBean.getRegions(filter);
            }

            @Override
            protected int getSize() {
                return regionBean.getRegionsCount(FilterWrapper.of(filterModel.getObject()));
            }
        };
        dataProvider.setSort("code", SortOrder.ASCENDING);

        //Таблица
        DataView<Region> dataView = new DataView<Region>("dictionaries", dataProvider, 10) {

            @Override
            protected void populateItem(Item<Region> item) {
                final Region region = item.getModelObject();

                item.add(new Label("code", Integer.toString(region.getCode())));
                item.add(new Label("begin_date", getStringDate(region.getBeginDate())));
                item.add(new Label("end_date", getStringDate(region.getEndDate())));
                item.add(new Label("name_uk", region.getNameUk()));

                //edit
                PageParameters pageParameters = new PageParameters();
                pageParameters.add("id", region.getId());
                pageParameters.add("type", "region");

                item.add(new BookmarkablePageLink<>("edit", DictionaryEdit.class, pageParameters));

                //delete
                item.add(new Link("delete"){

                    @Override
                    public void onClick() {
                        regionBean.delete(region.getId());

                        info(getStringFormat("deleted", region.getNameUk()));
                        log.info("Объект удален", new Event(EventCategory.REMOVE, region));
                    }
                });
            }
        };
        filterForm.add(dataView);

        //Сортировка
        filterForm.add(new OrderByBorder("header.code", "code", dataProvider));
        filterForm.add(new OrderByBorder("header.begin_date", "begin_date", dataProvider));
        filterForm.add(new OrderByBorder("header.end_date", "end_date", dataProvider));
        filterForm.add(new OrderByBorder("header.name_uk", "name_uk", dataProvider));

        //Постраничная навигация
        filterForm.add(new PagingNavigator("paging", dataView, getClass().getName(), filterForm));
    }

    private String getStringDate(Date date) {
        return date != null? DATE_FORMAT.format(date): "";
    }

    @Override
    protected List<? extends ToolbarButton> getToolbarButtons(String id) {
        return Arrays.asList(new AddDocumentButton(id) {
            @Override
            protected void onClick() {
                PageParameters pageParameters = new PageParameters();
                pageParameters.add("type", "region");

                setResponsePage(DictionaryEdit.class, pageParameters);
            }
        });
    }
}
