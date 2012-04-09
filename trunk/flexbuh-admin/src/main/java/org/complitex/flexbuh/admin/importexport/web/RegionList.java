package org.complitex.flexbuh.admin.importexport.web;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.complitex.flexbuh.common.entity.dictionary.Region;
import org.complitex.flexbuh.common.entity.dictionary.RegionFilter;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.common.service.dictionary.RegionBean;
import org.complitex.flexbuh.common.template.TemplatePage;
import org.complitex.flexbuh.common.web.component.datatable.DataProvider;
import org.complitex.flexbuh.common.web.component.paging.PagingNavigator;
import org.odlabs.wiquery.ui.datepicker.DatePicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.text.SimpleDateFormat;
import java.util.Date;

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
		RegionFilter filterObject = new RegionFilter();

		final IModel<RegionFilter> filterModel = new CompoundPropertyModel<>(filterObject);

		final Form<RegionFilter> filterForm = new Form<>("filter_form", filterModel);
        filterForm.setOutputMarkupId(true);
        add(filterForm);

		Link filter_reset = new Link("filter_reset") {

            @Override
            public void onClick() {
                filterForm.clearInput();
                filterModel.setObject(new RegionFilter());
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
				RegionFilter filter = filterModel.getObject();
                filter.setFirst(first);
                filter.setCount(count);
                filter.setSortProperty(getSort().getProperty());
                filter.setAscending(getSort().isAscending());
                return regionBean.getRegions(filter);
            }

            @Override
            protected int getSize() {
                return regionBean.getRegionsCount(filterModel.getObject());
            }
        };
        dataProvider.setSort("code", SortOrder.ASCENDING);

		//Таблица
        DataView<Region> dataView = new DataView<Region>("dictionaries", dataProvider, 10) {

            @Override
            protected void populateItem(Item<Region> item) {
                Region region = item.getModelObject();

                item.add(new Label("code", Integer.toString(region.getCode())));
                item.add(new Label("begin_date", getStringDate(region.getBeginDate())));
                item.add(new Label("end_date", getStringDate(region.getEndDate())));
                item.add(new Label("name_uk", region.getNameUk()));
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
}
