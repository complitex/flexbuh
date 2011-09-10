package org.complitex.flexbuh.admin.importexport.web;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.complitex.flexbuh.entity.dictionary.Document;
import org.complitex.flexbuh.entity.dictionary.DocumentName;
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
                return documentBean.read(first, count);
            }

            @Override
            protected int getSize() {
                return documentBean.totalCount();
            }
        };
        dataProvider.setSort("type", true);
        dataProvider.setSort("sub_type", true);

		//Таблица
        DataView<Document> dataView = new DataView<Document>("dictionaries", dataProvider, 10) {

            @Override
            protected void populateItem(Item<Document> item) {

                item.add(new Label("type", item.getModelObject().getType()));
                item.add(new Label("sub_type", item.getModelObject().getSubType()));
                item.add(new Label("parent_document_type", item.getModelObject().getParentDocumentType()));
                item.add(new Label("parent_document_sub_type", item.getModelObject().getParentDocumentSubType()));
                item.add(new Label("cnt_set", Boolean.toString(item.getModelObject().getCntSet())));
                item.add(new Label("selected", Boolean.toString(item.getModelObject().getSelected())));
                item.add(new Label("begin_date", getStringDate(item.getModelObject().getBeginDate())));
                item.add(new Label("end_date", getStringDate(item.getModelObject().getEndDate())));
				for (DocumentName documentName : item.getModelObject().getNames()) {
					if ("uk".equals(documentName.getLanguage().getLangIsoCode())) {
						item.add(new Label("name_uk", documentName.getValue()));
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
