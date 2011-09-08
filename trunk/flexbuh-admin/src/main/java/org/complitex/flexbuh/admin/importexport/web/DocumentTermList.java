package org.complitex.flexbuh.admin.importexport.web;

import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.complitex.flexbuh.entity.dictionary.DocumentTerm;
import org.complitex.flexbuh.service.dictionary.DocumentTermBean;
import org.complitex.flexbuh.template.TemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.text.SimpleDateFormat;

/**
 * @author Pavel Sknar
 *         Date: 29.08.11 14:27
 */
public class DocumentTermList extends TemplatePage {
	private final static Logger log = LoggerFactory.getLogger(DocumentTermList.class);

	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyyyy");

	@EJB
	DocumentTermBean documentTermBean;

	public DocumentTermList() {

		WebMarkupContainer datacontainer = new WebMarkupContainer("data");
        datacontainer.setOutputMarkupId(true);
        add(datacontainer);

        PageableListView<DocumentTerm> listview = new PageableListView<DocumentTerm>("rows", documentTermBean.readAll(), 20) {
            @Override
            protected void populateItem(ListItem<DocumentTerm> item) {
				item.add(new Label("type", item.getModelObject().getDocumentType()));
                item.add(new Label("sub_type", item.getModelObject().getDocumentSubType()));
                item.add(new Label("version", Integer.toString(item.getModelObject().getDocumentVersion())));
                item.add(new Label("date_term", DATE_FORMAT.format(item.getModelObject().getDateTerm())));
                item.add(new Label("period_year", Integer.toString(item.getModelObject().getPeriodYear())));
                item.add(new Label("period_month", Integer.toString(item.getModelObject().getPeriodMonth())));
                item.add(new Label("period_type", Integer.toString(item.getModelObject().getPeriodType())));
                item.add(new Label("begin_date", item.getModelObject().getBeginDate()==null?"":DATE_FORMAT.format(item.getModelObject().getBeginDate())));
                item.add(new Label("end_date", item.getModelObject().getEndDate()==null?"":DATE_FORMAT.format(item.getModelObject().getEndDate())));
            }
        };

        datacontainer.add(listview);
        datacontainer.add(new AjaxPagingNavigator("navigator", listview));
        datacontainer.setVersioned(false);
	}
}
