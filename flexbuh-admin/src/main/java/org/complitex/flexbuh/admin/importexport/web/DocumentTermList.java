package org.complitex.flexbuh.admin.importexport.web;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.complitex.flexbuh.entity.dictionary.DocumentTerm;
import org.complitex.flexbuh.security.SecurityRole;
import org.complitex.flexbuh.service.dictionary.DocumentTermBean;
import org.complitex.flexbuh.template.TemplatePage;
import org.complitex.flexbuh.web.component.datatable.DataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 29.08.11 14:27
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class DocumentTermList extends TemplatePage {
	private final static Logger log = LoggerFactory.getLogger(DocumentTermList.class);

	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyyyy");

	@EJB
	DocumentTermBean documentTermBean;

	public DocumentTermList() {

		final Form form = new Form("filter_form");
        form.setOutputMarkupId(true);
        add(form);

		//Модель
        final DataProvider<DocumentTerm> dataProvider = new DataProvider<DocumentTerm>() {

			@SuppressWarnings("unchecked")
            @Override
            protected Iterable<? extends DocumentTerm> getData(int first, int count) {
                return documentTermBean.getDocumentTerms(first, count);
            }

            @Override
            protected int getSize() {
                return documentTermBean.getAllDocumentTermsCount();
            }
        };
        dataProvider.setSort("type", SortOrder.ASCENDING);

		//Таблица
        DataView<DocumentTerm> dataView = new DataView<DocumentTerm>("dictionaries", dataProvider, 10) {

            @Override
            protected void populateItem(Item<DocumentTerm> item) {

                item.add(new Label("type", item.getModelObject().getCDoc()));
                item.add(new Label("sub_type", item.getModelObject().getCDocSub()));
                item.add(new Label("version", Integer.toString(item.getModelObject().getCDocVer()))); //todo NPE
                item.add(new Label("date_term", getStringDate(item.getModelObject().getDateTerm())));
                item.add(new Label("period_year", Integer.toString(item.getModelObject().getPeriodYear())));
                item.add(new Label("period_month", Integer.toString(item.getModelObject().getPeriodMonth())));
                item.add(new Label("period_type", Integer.toString(item.getModelObject().getPeriodType())));
                item.add(new Label("begin_date", getStringDate(item.getModelObject().getBeginDate())));
                item.add(new Label("end_date", getStringDate(item.getModelObject().getEndDate())));
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
