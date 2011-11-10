package org.complitex.flexbuh.admin.importexport.web;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.complitex.flexbuh.entity.dictionary.DocumentVersion;
import org.complitex.flexbuh.security.SecurityRole;
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
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
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
                return documentVersionBean.getDocumentVersions(first, count);
            }

            @Override
            protected int getSize() {
                return documentVersionBean.getDocumentVersionsCount();
            }
        };
        dataProvider.setSort("type", SortOrder.ASCENDING);

		//Таблица
        DataView<DocumentVersion> dataView = new DataView<DocumentVersion>("dictionaries", dataProvider, 10) {

            @Override
            protected void populateItem(Item<DocumentVersion> item) {
                DocumentVersion documentVersion = item.getModelObject();

                item.add(new Label("type", documentVersion.getCDoc()));
                item.add(new Label("sub_type", documentVersion.getCDocSub()));
                item.add(new Label("version", Integer.toString(documentVersion.getCDocVer())));
                item.add(new Label("begin_date", getStringDate(documentVersion.getBeginDate())));
                item.add(new Label("end_date",getStringDate(documentVersion.getEndDate())));
                item.add(new Label("name_uk", documentVersion.getNameUk()));
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
