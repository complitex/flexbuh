package org.complitex.flexbuh.admin.importexport.web;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.complitex.flexbuh.entity.dictionary.Region;
import org.complitex.flexbuh.entity.dictionary.RegionName;
import org.complitex.flexbuh.service.dictionary.RegionBean;
import org.complitex.flexbuh.template.TemplatePage;
import org.complitex.flexbuh.web.component.datatable.DataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 29.08.11 15:25
 */
public class RegionList extends TemplatePage {
	private final static Logger log = LoggerFactory.getLogger(RegionList.class);

	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyyyy");

	@EJB
	RegionBean regionBean;

	public RegionList() {

		final Form form = new Form("filter_form");
        form.setOutputMarkupId(true);
        add(form);

		//Модель
        final DataProvider<Region> dataProvider = new DataProvider<Region>() {

			@SuppressWarnings("unchecked")
            @Override
            protected Iterable<? extends Region> getData(int first, int count) {
                return regionBean.getRegions(first, count);
            }

            @Override
            protected int getSize() {
                return regionBean.getRegionsCount();
            }
        };
        dataProvider.setSort("type", true);
        dataProvider.setSort("sub_type", true);

		//Таблица
        DataView<Region> dataView = new DataView<Region>("dictionaries", dataProvider, 10) {

            @Override
            protected void populateItem(Item<Region> item) {

                item.add(new Label("code", Integer.toString(item.getModelObject().getCode())));
                item.add(new Label("begin_date", getStringDate(item.getModelObject().getBeginDate())));
                item.add(new Label("end_date", getStringDate(item.getModelObject().getEndDate())));
				for (RegionName regionName : item.getModelObject().getNames()) {
					if ("uk".equals(regionName.getLanguage().getLangIsoCode())) {
						item.add(new Label("name_uk", regionName.getValue()));
					}
				}
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
