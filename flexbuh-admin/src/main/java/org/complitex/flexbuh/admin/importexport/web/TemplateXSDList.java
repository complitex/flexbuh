package org.complitex.flexbuh.admin.importexport.web;

import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.complitex.flexbuh.entity.TemplateXSD;
import org.complitex.flexbuh.service.TemplateBean;
import org.complitex.flexbuh.template.TemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.text.SimpleDateFormat;

/**
 * @author Pavel Sknar
 *         Date: 31.08.11 11:13
 */
public class TemplateXSDList extends TemplatePage {
	private final static Logger log = LoggerFactory.getLogger(TemplateXSDList.class);

	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm");

	@EJB
	TemplateBean templateBean;

	public TemplateXSDList() {

		WebMarkupContainer datacontainer = new WebMarkupContainer("data");
        datacontainer.setOutputMarkupId(true);
        add(datacontainer);

        PageableListView<TemplateXSD> listview = new PageableListView<TemplateXSD>("rows", templateBean.getAllTemplateXSD(), 20) {
			@Override
            protected void populateItem(ListItem<TemplateXSD> item) {
                item.add(new Label("file_name", item.getModelObject().getName()));
                item.add(new Label("upload_date", DATE_FORMAT.format(item.getModelObject().getUploadDate())));
            }
        };

        datacontainer.add(listview);
        datacontainer.add(new AjaxPagingNavigator("navigator", listview));
        datacontainer.setVersioned(false);
	}
}