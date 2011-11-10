package org.complitex.flexbuh.admin.importexport.web;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.complitex.flexbuh.entity.dictionary.Currency;
import org.complitex.flexbuh.security.SecurityRole;
import org.complitex.flexbuh.service.dictionary.CurrencyBean;
import org.complitex.flexbuh.template.TemplatePage;
import org.complitex.flexbuh.web.component.datatable.DataProvider;
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
		
		final Form form = new Form("filter_form");
        form.setOutputMarkupId(true);
        add(form);

		//Модель
        final DataProvider<Currency> dataProvider = new DataProvider<Currency>() {

			@SuppressWarnings("unchecked")
            @Override
            protected Iterable<? extends Currency> getData(int first, int count) {
                return currencyBean.getCurrencies(first, count);
            }

            @Override
            protected int getSize() {
                return currencyBean.getCurrenciesCount();
            }
        };
        dataProvider.setSort("name", SortOrder.ASCENDING);

		//Таблица
        DataView<Currency> dataView = new DataView<Currency>("dictionaries", dataProvider, 10) {

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
        form.add(dataView);

        //Постраничная навигация
        form.add(new PagingNavigator("paging", dataView));
	}

	private String getStringDate(Date date) {
		return date != null? DATE_FORMAT.format(date): "";
	}
}
