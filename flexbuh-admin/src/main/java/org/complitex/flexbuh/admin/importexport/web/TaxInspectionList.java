package org.complitex.flexbuh.admin.importexport.web;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.complitex.flexbuh.entity.dictionary.AreaName;
import org.complitex.flexbuh.entity.dictionary.TaxInspection;
import org.complitex.flexbuh.entity.dictionary.TaxInspectionName;
import org.complitex.flexbuh.service.dictionary.TaxInspectionBean;
import org.complitex.flexbuh.template.TemplatePage;
import org.complitex.flexbuh.web.component.datatable.DataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 29.08.11 15:35
 */
public class TaxInspectionList extends TemplatePage {
	private final static Logger log = LoggerFactory.getLogger(TaxInspectionList.class);

	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyyyy");

	@EJB
	TaxInspectionBean taxInspectionBean;

	public TaxInspectionList() {
		
		final Form form = new Form("filter_form");
        form.setOutputMarkupId(true);
        add(form);

		//Модель
        final DataProvider<TaxInspection> dataProvider = new DataProvider<TaxInspection>() {

			@SuppressWarnings("unchecked")
            @Override
            protected Iterable<? extends TaxInspection> getData(int first, int count) {
                return taxInspectionBean.getTaxInspections(first, count);
            }

            @Override
            protected int getSize() {
                return taxInspectionBean.getTaxInspectionsCount();
            }
        };
        dataProvider.setSort("type", true);
        dataProvider.setSort("sub_type", true);

		//Таблица
        DataView<TaxInspection> dataView = new DataView<TaxInspection>("dictionaries", dataProvider, 10) {

            @Override
            protected void populateItem(Item<TaxInspection> item) {

                item.add(new Label("code", Integer.toString(item.getModelObject().getCode())));
                item.add(new Label("region_code", Integer.toString(item.getModelObject().getRegionCode())));
                item.add(new Label("area_code", Integer.toString(item.getModelObject().getCodeArea())));
                item.add(new Label("tax_inspection_type_code", Integer.toString(item.getModelObject().getCodeTaxInspectionType())));
                item.add(new Label("begin_date", getStringDate(item.getModelObject().getBeginDate())));
                item.add(new Label("end_date", getStringDate(item.getModelObject().getEndDate())));
				for (TaxInspectionName taxInspectionName : item.getModelObject().getNames()) {
					if ("uk".equals(taxInspectionName.getLanguage().getLangIsoCode())) {
						item.add(new Label("name_uk", taxInspectionName.getValue()));
					}
				}
				for (AreaName areaName : item.getModelObject().getAreaNames()) {
					if ("uk".equals(areaName.getLanguage().getLangIsoCode())) {
						item.add(new Label("area_name_uk", areaName.getValue()));
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
