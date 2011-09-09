package org.complitex.flexbuh.admin.importexport.web;

import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.complitex.flexbuh.entity.dictionary.Currency;
import org.complitex.flexbuh.entity.dictionary.CurrencyName;
import org.complitex.flexbuh.service.dictionary.CurrencyBean;
import org.complitex.flexbuh.template.TemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 29.08.11 10:29
 */
public class CurrencyList extends TemplatePage {
	private final static Logger log = LoggerFactory.getLogger(CurrencyList.class);

	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyyyy");

	@EJB
	CurrencyBean currencyBean;

	public CurrencyList() {

		WebMarkupContainer datacontainer = new WebMarkupContainer("data");
        datacontainer.setOutputMarkupId(true);
        add(datacontainer);

        PageableListView<Currency> listview = new PageableListView<Currency>("rows", currencyBean.readAll(), 20) {
            @Override
            protected void populateItem(ListItem<Currency> item) {
                item.add(new Label("code_number", Integer.toString(item.getModelObject().getCodeNumber())));
                item.add(new Label("code_string", item.getModelObject().getCodeString()));
                item.add(new Label("begin_date", getStringDate(item.getModelObject().getBeginDate())));
                item.add(new Label("end_date", getStringDate(item.getModelObject().getEndDate())));
				for (CurrencyName currencyName : item.getModelObject().getNames()) {
					if ("uk".equals(currencyName.getLanguage().getLangIsoCode())) {
						item.add(new Label("name_uk", currencyName.getValue()));
					} else if ("ru".equals(currencyName.getLanguage().getLangIsoCode())) {
						item.add(new Label("name_ru", currencyName.getValue()));
					}
				}
            }
        };

        datacontainer.add(listview);
        datacontainer.add(new AjaxPagingNavigator("navigator", listview));
        datacontainer.setVersioned(false);
	}

	private String getStringDate(Date date) {
		return date != null? DATE_FORMAT.format(date): "";
	}
}
