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
import org.complitex.flexbuh.common.entity.dictionary.TaxInspection;
import org.complitex.flexbuh.common.logging.Event;
import org.complitex.flexbuh.common.logging.EventCategory;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.common.service.dictionary.TaxInspectionBean;
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
 *         Date: 29.08.11 15:35
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class TaxInspectionList extends TemplatePage {
    private final static Logger log = LoggerFactory.getLogger(TaxInspectionList.class);

    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyyyy");

    @EJB
    TaxInspectionBean taxInspectionBean;

    public TaxInspectionList() {

        add(new FeedbackPanel("messages"));

        //Фильтр модель
        final IModel<TaxInspection> filterModel = new CompoundPropertyModel<>(new TaxInspection());

        final Form<TaxInspection> filterForm = new Form<>("filter_form", filterModel);
        filterForm.setOutputMarkupId(true);
        add(filterForm);

        Link filter_reset = new Link("filter_reset") {

            @Override
            public void onClick() {
                filterForm.clearInput();
                filterModel.setObject(new TaxInspection());
            }
        };
        filterForm.add(filter_reset);

        //Tax inspection code
        filterForm.add(new TextField<String>("cSti"));

        //Region code
        filterForm.add(new TextField<String>("cReg"));

        //Area code
        filterForm.add(new TextField<String>("cRaj"));

        //Tax inspection type
        filterForm.add(new TextField<String>("tSti"));

        //Begin date
        DatePicker<Date> beginDate = new DatePicker<>("beginDate");
        filterForm.add(beginDate);

        //End date
        DatePicker<Date> endDate = new DatePicker<>("endDate");
        filterForm.add(endDate);

        //Ukrainian name
        filterForm.add(new TextField<String>("nameUk"));

        //Ukrainian region name
        filterForm.add(new TextField<String>("nameRajUk"));

        //Модель
        final DataProvider<TaxInspection> dataProvider = new DataProvider<TaxInspection>() {
            @Override
            protected Iterable<? extends TaxInspection> getData(int first, int count) {
                FilterWrapper<TaxInspection> filter = FilterWrapper.of(filterModel.getObject());

                filter.setFirst(first);
                filter.setCount(count);
                filter.setSortProperty(getSort().getProperty());
                filter.setAscending(getSort().isAscending());
                return taxInspectionBean.getTaxInspections(filter);
            }

            @Override
            protected int getSize() {
                return taxInspectionBean.getTaxInspectionsCount(FilterWrapper.of(filterModel.getObject()));
            }
        };
        dataProvider.setSort("c_sti", SortOrder.ASCENDING);

        //Таблица
        DataView<TaxInspection> dataView = new DataView<TaxInspection>("dictionaries", dataProvider, 10) {

            @Override
            protected void populateItem(Item<TaxInspection> item) {
                final TaxInspection taxInspection = item.getModelObject();

                item.add(new Label("code", Integer.toString(taxInspection.getCSti())));
                item.add(new Label("region_code", Integer.toString(taxInspection.getCReg())));
                item.add(new Label("area_code", Integer.toString(taxInspection.getCRaj())));
                item.add(new Label("tax_inspection_type_code", Integer.toString(taxInspection.getTSti())));
                item.add(new Label("begin_date", getStringDate(taxInspection.getBeginDate())));
                item.add(new Label("end_date", getStringDate(taxInspection.getEndDate())));

                item.add(new Label("name_uk",taxInspection.getNameUk()));
                item.add(new Label("area_name_uk", taxInspection.getNameRajUk()));

                //edit
                PageParameters pageParameters = new PageParameters();
                pageParameters.add("id", taxInspection.getId());
                pageParameters.add("type", "tax_inspection");

                item.add(new BookmarkablePageLink<>("edit", DictionaryEdit.class, pageParameters));

                //delete
                item.add(new Link("delete"){

                    @Override
                    public void onClick() {
                        taxInspectionBean.delete(taxInspection.getId());

                        info(getStringFormat("deleted", taxInspection.getNameUk()));
                        log.info("Объект удален", new Event(EventCategory.REMOVE, taxInspection));
                    }
                });
            }
        };
        filterForm.add(dataView);

        //Сортировка
        filterForm.add(new OrderByBorder("header.c_sti", "c_sti", dataProvider));
        filterForm.add(new OrderByBorder("header.c_reg", "c_reg", dataProvider));
        filterForm.add(new OrderByBorder("header.c_raj", "c_raj", dataProvider));
        filterForm.add(new OrderByBorder("header.t_sti", "t_sti", dataProvider));
        filterForm.add(new OrderByBorder("header.begin_date", "begin_date", dataProvider));
        filterForm.add(new OrderByBorder("header.end_date", "end_date", dataProvider));
        filterForm.add(new OrderByBorder("header.name_uk", "name_uk", dataProvider));
        filterForm.add(new OrderByBorder("header.name_raj_uk", "name_raj_uk", dataProvider));

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
                pageParameters.add("type", "tax_inspection");

                setResponsePage(DictionaryEdit.class, pageParameters);
            }
        });
    }
}
