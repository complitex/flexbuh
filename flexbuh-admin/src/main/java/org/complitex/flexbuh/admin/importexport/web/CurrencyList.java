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
import org.complitex.flexbuh.common.entity.dictionary.Currency;
import org.complitex.flexbuh.common.entity.dictionary.CurrencyFilter;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.common.service.dictionary.CurrencyBean;
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
 *         Date: 29.08.11 10:29
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class CurrencyList extends TemplatePage {
	private final static Logger log = LoggerFactory.getLogger(CurrencyList.class);

	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyyyy");

	@EJB
	CurrencyBean currencyBean;

	public CurrencyList() {

		add(new FeedbackPanel("messages"));

		//Фильтр модель
        CurrencyFilter filterObject = new CurrencyFilter();

        final IModel<CurrencyFilter> filterModel = new CompoundPropertyModel<>(filterObject);
		
		final Form<CurrencyFilter> filterForm = new Form<>("filter_form", filterModel);
        filterForm.setOutputMarkupId(true);
        add(filterForm);

		Link filter_reset = new Link("filter_reset") {

            @Override
            public void onClick() {
                filterForm.clearInput();
                filterModel.setObject(new CurrencyFilter());
            }
        };
        filterForm.add(filter_reset);

		//Code number
        filterForm.add(new TextField<String>("codeNumber"));

		//Code string
        filterForm.add(new TextField<String>("codeString"));

		//Begin date
        DatePicker<Date> beginDate = new DatePicker<>("beginDate");
        filterForm.add(beginDate);

		//End date
        DatePicker<Date> endDate = new DatePicker<>("endDate");
        filterForm.add(endDate);

		//Ukrainian name
        filterForm.add(new TextField<String>("nameUk"));

		//Russian name
        filterForm.add(new TextField<String>("nameRu"));

		//Модель
        final DataProvider<Currency> dataProvider = new DataProvider<Currency>() {

			@SuppressWarnings("unchecked")
            @Override
            protected Iterable<? extends Currency> getData(int first, int count) {
				CurrencyFilter filter = filterModel.getObject();
                filter.setFirst(first);
                filter.setCount(count);
                filter.setSortProperty(getSort().getProperty());
                filter.setAscending(getSort().isAscending());
                return currencyBean.getCurrencies(filterModel.getObject());
            }

            @Override
            protected int getSize() {
                return currencyBean.getCurrenciesCount(filterModel.getObject());
            }
        };
        dataProvider.setSort("code_number", SortOrder.ASCENDING);

		//Таблица
        DataView<Currency> dataView = new DataView<Currency>("dictionaries", dataProvider, 1) {

            @Override
            protected void populateItem(Item<Currency> item) {
                Currency currency = item.getModelObject();

                item.add(new Label("code_number", Integer.toString(currency.getCodeNumber())));
                item.add(new Label("code_string", currency.getCodeString()));
                item.add(new Label("begin_date", getStringDate(currency.getBeginDate())));
                item.add(new Label("end_date", getStringDate(currency.getEndDate())));

                item.add(new Label("name_uk", currency.getNameUk()));
                item.add(new Label("name_ru", currency.getNameRu()));
            }
        };
        filterForm.add(dataView);

		//Сортировка
        filterForm.add(new OrderByBorder("header.code_number", "code_number", dataProvider));
        filterForm.add(new OrderByBorder("header.code_string", "code_string", dataProvider));
        filterForm.add(new OrderByBorder("header.begin_date", "begin_date", dataProvider));
        filterForm.add(new OrderByBorder("header.end_date", "end_date", dataProvider));
        filterForm.add(new OrderByBorder("header.name_uk", "name_uk", dataProvider));
        filterForm.add(new OrderByBorder("header.name_ru", "name_ru", dataProvider));

        //Постраничная навигация
        filterForm.add(new PagingNavigator("paging", dataView, getClass().getName(), filterForm));
	}

	private String getStringDate(Date date) {
		return date != null? DATE_FORMAT.format(date): "";
	}
}
