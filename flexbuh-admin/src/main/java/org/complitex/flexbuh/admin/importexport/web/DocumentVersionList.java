package org.complitex.flexbuh.admin.importexport.web;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.complitex.flexbuh.entity.dictionary.DocumentVersion;
import org.complitex.flexbuh.entity.dictionary.NormativeDocumentName;
import org.complitex.flexbuh.service.dictionary.DocumentVersionBean;
import org.complitex.flexbuh.template.TemplatePage;
import org.complitex.flexbuh.web.component.datatable.DataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 29.08.11 14:55
 */
public class DocumentVersionList extends TemplatePage {
	private final static Logger log = LoggerFactory.getLogger(DocumentVersion.class);

	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyyyy");

	@EJB
	DocumentVersionBean documentVersionBean;

	public DocumentVersionList() {
		
		final Form form = new Form("filter_form");
        form.setOutputMarkupId(true);
        add(form);

		//Модель
        final DataProvider<DocumentVersion> dataProvider = new DataProvider<DocumentVersion>() {

			@SuppressWarnings("unchecked")
            @Override
            protected Iterable<? extends DocumentVersion> getData(int first, int count) {
                return documentVersionBean.read(first, count);
            }

            @Override
            protected int getSize() {
                return documentVersionBean.totalCount();
            }
        };
        dataProvider.setSort("type", true);
        dataProvider.setSort("sub_type", true);

		//Таблица
        DataView<DocumentVersion> dataView = new DataView<DocumentVersion>("dictionaries", dataProvider, 10) {

            @Override
            protected void populateItem(Item<DocumentVersion> item) {

                item.add(new Label("type", item.getModelObject().getDocumentType()));
                item.add(new Label("sub_type", item.getModelObject().getDocumentSubType()));
                item.add(new Label("version", Integer.toString(item.getModelObject().getVersion())));
                item.add(new Label("begin_date", getStringDate(item.getModelObject().getBeginDate())));
                item.add(new Label("end_date",getStringDate(item.getModelObject().getEndDate())));
				for (NormativeDocumentName documentName : item.getModelObject().getNormativeDocumentNames()) {
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
