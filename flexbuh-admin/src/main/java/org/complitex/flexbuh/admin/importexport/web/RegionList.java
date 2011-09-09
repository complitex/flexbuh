package org.complitex.flexbuh.admin.importexport.web;

import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.complitex.flexbuh.entity.dictionary.Region;
import org.complitex.flexbuh.entity.dictionary.RegionName;
import org.complitex.flexbuh.service.dictionary.RegionBean;
import org.complitex.flexbuh.template.TemplatePage;
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

		WebMarkupContainer datacontainer = new WebMarkupContainer("data");
        datacontainer.setOutputMarkupId(true);
        add(datacontainer);

        PageableListView<Region> listview = new PageableListView<Region>("rows", regionBean.readAll(), 20) {
            @Override
            protected void populateItem(ListItem<Region> item) {
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

        datacontainer.add(listview);
        datacontainer.add(new AjaxPagingNavigator("navigator", listview));
        datacontainer.setVersioned(false);
	}

	private String getStringDate(Date date) {
		return date != null? DATE_FORMAT.format(date): "";
	}
}
