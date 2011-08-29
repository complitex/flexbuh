package org.complitex.flexbuh.admin.importexport.web;

import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.complitex.flexbuh.entity.dictionary.AreaName;
import org.complitex.flexbuh.entity.dictionary.TaxInspection;
import org.complitex.flexbuh.entity.dictionary.TaxInspectionName;
import org.complitex.flexbuh.service.dictionary.TaxInspectionBean;
import org.complitex.flexbuh.template.TemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.text.SimpleDateFormat;

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

		WebMarkupContainer datacontainer = new WebMarkupContainer("data");
        datacontainer.setOutputMarkupId(true);
        add(datacontainer);

        PageableListView<TaxInspection> listview = new PageableListView<TaxInspection>("rows", taxInspectionBean.readAll(), 20) {
            @Override
            protected void populateItem(ListItem<TaxInspection> item) {
                item.add(new Label("code", Integer.toString(item.getModelObject().getCode())));
                item.add(new Label("region_code", Integer.toString(item.getModelObject().getRegionCode())));
                item.add(new Label("area_code", Integer.toString(item.getModelObject().getCodeArea())));
                item.add(new Label("tax_inspection_type_code", Integer.toString(item.getModelObject().getCodeTaxInspectionType())));
                item.add(new Label("begin_date", DATE_FORMAT.format(item.getModelObject().getBeginDate())));
                item.add(new Label("end_date", DATE_FORMAT.format(item.getModelObject().getEndDate())));
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

        datacontainer.add(listview);
        datacontainer.add(new AjaxPagingNavigator("navigator", listview));
        datacontainer.setVersioned(false);
	}
}
