package org.complitex.flexbuh.admin.importexport.web;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.complitex.flexbuh.common.entity.FilterWrapper;
import org.complitex.flexbuh.common.entity.dictionary.DocumentVersion;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.common.service.dictionary.DocumentVersionBean;
import org.complitex.flexbuh.common.template.TemplatePage;
import org.complitex.flexbuh.common.web.component.datatable.DataProvider;
import org.complitex.flexbuh.common.web.component.paging.PagingNavigator;
import org.odlabs.wiquery.ui.datepicker.DatePicker;
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

		add(new FeedbackPanel("messages"));

		//Фильтр модель
        final IModel<DocumentVersion> filterModel = new CompoundPropertyModel<>(new DocumentVersion());
		
		final Form<DocumentVersion> filterForm = new Form<>("filter_form", filterModel);
        filterForm.setOutputMarkupId(true);
        add(filterForm);

		Link filter_reset = new Link("filter_reset") {

            @Override
            public void onClick() {
                filterForm.clearInput();
                filterModel.setObject(new DocumentVersion());
            }
        };
        filterForm.add(filter_reset);

		//Document type
        filterForm.add(new TextField<String>("cDoc"));

		//Sub document type
        filterForm.add(new TextField<String>("cDocSub"));

		//Document version
        filterForm.add(new TextField<String>("cDocVer"));

		//Begin date
        DatePicker<Date> beginDate = new DatePicker<>("beginDate");
        filterForm.add(beginDate);

		//End date
        DatePicker<Date> endDate = new DatePicker<>("endDate");
        filterForm.add(endDate);

		//Ukrainian name
        filterForm.add(new TextField<String>("nameUk"));

		//Модель
        final DataProvider<DocumentVersion> dataProvider = new DataProvider<DocumentVersion>() {
            @Override
            protected Iterable<? extends DocumentVersion> getData(int first, int count) {
                FilterWrapper<DocumentVersion> filter = FilterWrapper.of(filterModel.getObject());

                filter.setFirst(first);
                filter.setCount(count);
                filter.setSortProperty(getSort().getProperty());
                filter.setAscending(getSort().isAscending());

                return documentVersionBean.getDocumentVersions(filter);
            }

            @Override
            protected int getSize() {
                return documentVersionBean.getDocumentVersionsCount(FilterWrapper.of(filterModel.getObject()));
            }
        };
        dataProvider.setSort("c_doc", SortOrder.ASCENDING);
        dataProvider.setSort("c_doc_sub", SortOrder.ASCENDING);
        dataProvider.setSort("c_doc_ver", SortOrder.ASCENDING);

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
        filterForm.add(dataView);

		//Сортировка
        filterForm.add(new OrderByBorder("header.c_doc", "c_doc", dataProvider));
        filterForm.add(new OrderByBorder("header.c_doc_sub", "c_doc_sub", dataProvider));
        filterForm.add(new OrderByBorder("header.c_doc_ver", "c_doc_ver", dataProvider));
        filterForm.add(new OrderByBorder("header.begin_date", "begin_date", dataProvider));
		filterForm.add(new OrderByBorder("header.end_date", "end_date", dataProvider));
		filterForm.add(new OrderByBorder("header.name_uk", "name_uk", dataProvider));

        //Постраничная навигация
        filterForm.add(new PagingNavigator("paging", dataView, getClass().getName(), filterForm));
	}

	private String getStringDate(Date date) {
		return date != null? DATE_FORMAT.format(date): "";
	}
}
