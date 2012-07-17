package org.complitex.flexbuh.admin.dictionary.web;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.flexbuh.common.entity.FilterWrapper;
import org.complitex.flexbuh.common.entity.dictionary.Document;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.common.service.dictionary.DocumentBean;
import org.complitex.flexbuh.common.template.TemplatePage;
import org.complitex.flexbuh.common.template.toolbar.AddDocumentButton;
import org.complitex.flexbuh.common.template.toolbar.ToolbarButton;
import org.complitex.flexbuh.common.web.component.datatable.DataProvider;
import org.complitex.flexbuh.common.web.component.paging.PagingNavigator;
import org.odlabs.wiquery.ui.datepicker.DatePicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 29.08.11 13:25
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class DocumentList extends TemplatePage {
    private final static Logger log = LoggerFactory.getLogger(DocumentList.class);

    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyyyy");

    @EJB
    DocumentBean documentBean;

    public DocumentList() {

        add(new FeedbackPanel("messages"));

        //Фильтр модель
        final IModel<Document> filterModel = new CompoundPropertyModel<>(new Document());

        final Form<Document> filterForm = new Form<>("filter_form", filterModel);
        filterForm.setOutputMarkupId(true);
        add(filterForm);

        Link filter_reset = new Link("filter_reset") {

            @Override
            public void onClick() {
                filterForm.clearInput();
                filterModel.setObject(new Document());
            }
        };
        filterForm.add(filter_reset);

        //Document type
        filterForm.add(new TextField<String>("cDoc"));

        //Sub document type
        filterForm.add(new TextField<String>("cDocSub"));

        //Может подаваться в отчетном периоде более одного раза
        filterForm.add(new DropDownChoice<>("cntSet", Arrays.asList(Boolean.TRUE, Boolean.FALSE),
                new IChoiceRenderer<Boolean>() {

                    @Override
                    public Object getDisplayValue(Boolean object) {
                        return getStringOrKey(object.toString());
                    }

                    @Override
                    public String getIdValue(Boolean object, int index) {
                        return String.valueOf(object.toString());
                    }
                }));

        //Parent document type
        filterForm.add(new TextField<String>("parentCDoc"));

        //Parent sub document type
        filterForm.add(new TextField<String>("parentCDocSub"));

        //Begin date
        DatePicker<Date> beginDate = new DatePicker<>("beginDate");
        filterForm.add(beginDate);

        //End date
        DatePicker<Date> endDate = new DatePicker<>("endDate");
        filterForm.add(endDate);

        //Ukrainian name
        filterForm.add(new TextField<String>("nameUk"));

        //Модель
        final DataProvider<Document> dataProvider = new DataProvider<Document>() {
            @Override
            protected Iterable<? extends Document> getData(int first, int count) {
                FilterWrapper<Document> filter = FilterWrapper.of(filterModel.getObject());

                filter.setFirst(first);
                filter.setCount(count);
                filter.setSortProperty(getSort().getProperty());
                filter.setAscending(getSort().isAscending());

                return documentBean.getDocuments(filter);
            }

            @Override
            protected int getSize() {
                return documentBean.getDocumentsCount(FilterWrapper.of(filterModel.getObject()));
            }
        };
        dataProvider.setSort("c_doc", SortOrder.ASCENDING);
        dataProvider.setSort("c_doc_sub", SortOrder.ASCENDING);

        //Таблица
        DataView<Document> dataView = new DataView<Document>("dictionaries", dataProvider, 1) {

            @Override
            protected void populateItem(Item<Document> item) {
                Document document = item.getModelObject();

                item.add(new Label("type", document.getCDoc()));
                item.add(new Label("sub_type", document.getCDocSub()));
                item.add(new Label("parent_document_type", document.getParentCDoc()));
                item.add(new Label("parent_document_sub_type", document.getParentCDocSub()));
                item.add(new Label("cnt_set", Boolean.toString(document.getCntSet())));
                //item.add(new Label("selected", Boolean.toString(document.getSelected())));
                item.add(new Label("begin_date", getStringDate(document.getBeginDate())));
                item.add(new Label("end_date", getStringDate(document.getEndDate())));

                item.add(new Label("name_uk", document.getNameUk()));

                //edit
                PageParameters pageParameters = new PageParameters();
                pageParameters.add("id", document.getId());
                pageParameters.add("type", "document");

                item.add(new BookmarkablePageLink<>("edit", DictionaryEdit.class, pageParameters));
            }
        };
        filterForm.add(dataView);

        //Сортировка
        filterForm.add(new OrderByBorder("header.c_doc", "c_doc", dataProvider));
        filterForm.add(new OrderByBorder("header.c_doc_sub", "c_doc_sub", dataProvider));
        filterForm.add(new OrderByBorder("header.parent_c_doc", "parent_c_doc", dataProvider));
        filterForm.add(new OrderByBorder("header.parent_c_doc_sub", "parent_c_doc_sub", dataProvider));
        filterForm.add(new OrderByBorder("header.cnt_set", "cnt_set", dataProvider));
        filterForm.add(new OrderByBorder("header.begin_date", "begin_date", dataProvider));
        filterForm.add(new OrderByBorder("header.end_date", "end_date", dataProvider));
        filterForm.add(new OrderByBorder("header.name_uk", "name_uk", dataProvider));

        //Постраничная навигация
        filterForm.add(new PagingNavigator("paging", dataView, getClass().getName(), filterForm));
    }

    private String getStringDate(Date date) {
        return date != null? DATE_FORMAT.format(date): "";
    }

    @Override
        protected List<? extends ToolbarButton> getToolbarButtons(String id) {
            return Arrays.asList(new AddDocumentButton(id) {
                @Override
                protected void onClick() {
                    PageParameters pageParameters = new PageParameters();
                    pageParameters.add("type", "document");

                    setResponsePage(DictionaryEdit.class, pageParameters);
                }
            });
        }
}
