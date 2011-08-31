package org.complitex.flexbuh.admin.importexport.web;

import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.complitex.flexbuh.entity.TemplateFO;
import org.complitex.flexbuh.service.TemplateBean;
import org.complitex.flexbuh.template.TemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.text.SimpleDateFormat;

/**
 * @author Pavel Sknar
 *         Date: 30.08.11 19:42
 */
public class TemplateFOList extends TemplatePage {
	private final static Logger log = LoggerFactory.getLogger(TemplateFOList.class);

	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm");

	@EJB
	TemplateBean templateBean;

	public TemplateFOList() {

		WebMarkupContainer datacontainer = new WebMarkupContainer("data");
        datacontainer.setOutputMarkupId(true);
        add(datacontainer);

        PageableListView<TemplateFO> listview = new PageableListView<TemplateFO>("rows", templateBean.getAllTemplateFO(), 20) {
            @Override
            protected void populateItem(ListItem<TemplateFO> item) {
                item.add(new Label("file_name", item.getModelObject().getName()));
                item.add(new Label("upload_date", DATE_FORMAT.format(item.getModelObject().getUploadDate())));
            }
        };

        datacontainer.add(listview);
        datacontainer.add(new AjaxPagingNavigator("navigator", listview));
        datacontainer.setVersioned(false);
	}
}
