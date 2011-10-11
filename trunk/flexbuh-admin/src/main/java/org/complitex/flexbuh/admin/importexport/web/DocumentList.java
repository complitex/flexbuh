package org.complitex.flexbuh.admin.importexport.web;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.complitex.flexbuh.entity.dictionary.Document;
import org.complitex.flexbuh.service.dictionary.DocumentBean;
import org.complitex.flexbuh.template.TemplatePage;
import org.complitex.flexbuh.web.component.datatable.DataProvider;
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
		
		final Form form = new Form("filter_form");
        form.setOutputMarkupId(true);
        add(form);

		//Модель
        final DataProvider<Document> dataProvider = new DataProvider<Document>() {

			@SuppressWarnings("unchecked")
            @Override
            protected Iterable<? extends Document> getData(int first, int count) {
                return documentBean.getDocuments(first, count);
            }

            @Override
            protected int getSize() {
                return documentBean.getDocumentsCount();
            }
        };
        dataProvider.setSort("type", SortOrder.ASCENDING);
        dataProvider.setSort("sub_type", SortOrder.ASCENDING);

		//Таблица
        DataView<Document> dataView = new DataView<Document>("dictionaries", dataProvider, 10) {

            @Override
            protected void populateItem(Item<Document> item) {
                Document document = item.getModelObject();

                item.add(new Label("type", document.getCDoc()));
                item.add(new Label("sub_type", document.getCDocSub()));
                item.add(new Label("parent_document_type", document.getParentCDoc()));
                item.add(new Label("parent_document_sub_type", document.getParentCDocSub()));
                item.add(new Label("cnt_set", Boolean.toString(document.getCntSet())));
                item.add(new Label("selected", Boolean.toString(document.getSelected())));
                item.add(new Label("begin_date", getStringDate(document.getBeginDate())));
                item.add(new Label("end_date", getStringDate(document.getEndDate())));

                item.add(new Label("name_uk", document.getNameUk()));
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
