package org.complitex.flexbuh.admin.importexport.web;

import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.complitex.flexbuh.entity.TemplateXSL;
import org.complitex.flexbuh.service.TemplateBean;
import org.complitex.flexbuh.template.TemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 31.08.11 11:19
 */
public class TemplateXSLList extends TemplatePage {
	private final static Logger log = LoggerFactory.getLogger(TemplateXSLList.class);

	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm");

	@EJB
	TemplateBean templateBean;

	public TemplateXSLList() {

		WebMarkupContainer datacontainer = new WebMarkupContainer("data");
        datacontainer.setOutputMarkupId(true);
        add(datacontainer);

        PageableListView<TemplateXSL> listview = new PageableListView<TemplateXSL>("rows", templateBean.getAllTemplateXSL(), 20) {
			@Override
            protected void populateItem(ListItem<TemplateXSL> item) {
                item.add(new Label("file_name", item.getModelObject().getName()));
                item.add(new Label("upload_date", getStringDate(item.getModelObject().getUploadDate())));
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