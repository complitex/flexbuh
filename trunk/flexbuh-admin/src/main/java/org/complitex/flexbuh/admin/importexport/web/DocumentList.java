package org.complitex.flexbuh.admin.importexport.web;

import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.complitex.flexbuh.entity.dictionary.Document;
import org.complitex.flexbuh.entity.dictionary.DocumentName;
import org.complitex.flexbuh.service.dictionary.DocumentBean;
import org.complitex.flexbuh.template.TemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 29.08.11 13:25
 */
public class DocumentList extends TemplatePage {
	private final static Logger log = LoggerFactory.getLogger(DocumentList.class);

	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyyyy");

	@EJB
	DocumentBean documentBean;

	public DocumentList() {

		WebMarkupContainer datacontainer = new WebMarkupContainer("data");
        datacontainer.setOutputMarkupId(true);
        add(datacontainer);

        PageableListView<Document> listview = new PageableListView<Document>("rows", documentBean.readAll(), 20) {
            @Override
            protected void populateItem(ListItem<Document> item) {
                item.add(new Label("type", item.getModelObject().getType()));
                item.add(new Label("sub_type", item.getModelObject().getSubType()));
                item.add(new Label("parent_document_type", item.getModelObject().getParentDocumentType()));
                item.add(new Label("parent_document_sub_type", item.getModelObject().getParentDocumentSubType()));
                item.add(new Label("cnt_set", Boolean.toString(item.getModelObject().getCntSet())));
                item.add(new Label("selected", Boolean.toString(item.getModelObject().getSelected())));
                item.add(new Label("begin_date", getStringDate(item.getModelObject().getBeginDate())));
                item.add(new Label("end_date", getStringDate(item.getModelObject().getEndDate())));
				for (DocumentName currencyName : item.getModelObject().getNames()) {
					if ("uk".equals(currencyName.getLanguage().getLangIsoCode())) {
						item.add(new Label("name_uk", currencyName.getValue()));
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
