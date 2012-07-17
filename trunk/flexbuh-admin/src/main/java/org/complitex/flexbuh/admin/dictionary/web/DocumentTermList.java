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
import org.complitex.flexbuh.common.entity.dictionary.DocumentTerm;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.common.service.dictionary.DocumentTermBean;
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
 *         Date: 29.08.11 14:27
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class DocumentTermList extends TemplatePage {
    private final static Logger log = LoggerFactory.getLogger(DocumentTermList.class);

    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyyyy");

    @EJB
    DocumentTermBean documentTermBean;

    public DocumentTermList() {

        add(new FeedbackPanel("messages"));

        //Фильтр модель
        final IModel<DocumentTerm> filterModel = new CompoundPropertyModel<>(new DocumentTerm());

        final Form<DocumentTerm> filterForm = new Form<>("filter_form", filterModel);
        filterForm.setOutputMarkupId(true);
        add(filterForm);

        Link filter_reset = new Link("filter_reset") {

            @Override
            public void onClick() {
                filterForm.clearInput();
                filterModel.setObject(new DocumentTerm());
            }
        };
        filterForm.add(filter_reset);

        //Document type
        filterForm.add(new TextField<String>("cDoc"));

        //Sub document type
        filterForm.add(new TextField<String>("cDocSub"));

        //Document version
        filterForm.add(new TextField<String>("cDocVer"));

        //Period type
        filterForm.add(new DropDownChoice<>("periodType", documentTermBean.getPeriodTypes(),
                new IChoiceRenderer<Integer>() {

                    @Override
                    public Object getDisplayValue(Integer object) {
                        return getStringOrKey(object.toString());
                    }

                    @Override
                    public String getIdValue(Integer object, int index) {
                        return String.valueOf(object.toString());
                    }
                }));

        //Period month
        filterForm.add(new DropDownChoice<>("periodMonth", documentTermBean.getPeriodMonths(),
                new IChoiceRenderer<Integer>() {

                    @Override
                    public Object getDisplayValue(Integer object) {
                        return getStringOrKey(object.toString());
                    }

                    @Override
                    public String getIdValue(Integer object, int index) {
                        return String.valueOf(object.toString());
                    }
                }));

        //Period month
        filterForm.add(new TextField<String>("periodYear"));

        //Date term
        DatePicker<Date> dateTerm = new DatePicker<>("dateTerm");
        filterForm.add(dateTerm);

        //Begin date
        DatePicker<Date> beginDate = new DatePicker<>("beginDate");
        filterForm.add(beginDate);

        //End date
        DatePicker<Date> endDate = new DatePicker<>("endDate");
        filterForm.add(endDate);

        //Модель
        final DataProvider<DocumentTerm> dataProvider = new DataProvider<DocumentTerm>() {
            @Override
            protected Iterable<? extends DocumentTerm> getData(int first, int count) {
                FilterWrapper<DocumentTerm> filter = FilterWrapper.of(filterModel.getObject());

                filter.setFirst(first);
                filter.setCount(count);
                filter.setSortProperty(getSort().getProperty());
                filter.setAscending(getSort().isAscending());
                return documentTermBean.getDocumentTerms(filter);
            }

            @Override
            protected int getSize() {
                return documentTermBean.getDocumentTermsCount(FilterWrapper.of(filterModel.getObject()));
            }
        };
        dataProvider.setSort("c_doc", SortOrder.ASCENDING);
        dataProvider.setSort("c_doc_sub", SortOrder.ASCENDING);
        dataProvider.setSort("c_doc_ver", SortOrder.ASCENDING);

        //Таблица
        DataView<DocumentTerm> dataView = new DataView<DocumentTerm>("dictionaries", dataProvider, 10) {

            @Override
            protected void populateItem(Item<DocumentTerm> item) {
                DocumentTerm documentTerm = item.getModelObject();

                item.add(new Label("type", documentTerm.getCDoc()));
                item.add(new Label("sub_type", documentTerm.getCDocSub()));
                item.add(new Label("version", Integer.toString(documentTerm.getCDocVer())));
                item.add(new Label("date_term", getStringDate(documentTerm.getDateTerm())));
                item.add(new Label("period_year", Integer.toString(documentTerm.getPeriodYear())));
                item.add(new Label("period_month", Integer.toString(documentTerm.getPeriodMonth())));
                item.add(new Label("period_type", Integer.toString(documentTerm.getPeriodType())));
                item.add(new Label("begin_date", getStringDate(documentTerm.getBeginDate())));
                item.add(new Label("end_date", getStringDate(documentTerm.getEndDate())));

                //edit
                PageParameters pageParameters = new PageParameters();
                pageParameters.add("id", documentTerm.getId());
                pageParameters.add("type", "document_term");

                item.add(new BookmarkablePageLink<>("edit", DictionaryEdit.class, pageParameters));
            }
        };
        filterForm.add(dataView);

        //Сортировка
        filterForm.add(new OrderByBorder("header.c_doc", "c_doc", dataProvider));
        filterForm.add(new OrderByBorder("header.c_doc_sub", "c_doc_sub", dataProvider));
        filterForm.add(new OrderByBorder("header.c_doc_ver", "c_doc_ver", dataProvider));
        filterForm.add(new OrderByBorder("header.date_term", "date_term", dataProvider));
        filterForm.add(new OrderByBorder("header.period_month", "period_month", dataProvider));
        filterForm.add(new OrderByBorder("header.period_year", "period_year", dataProvider));
        filterForm.add(new OrderByBorder("header.period_type", "period_type", dataProvider));
        filterForm.add(new OrderByBorder("header.begin_date", "begin_date", dataProvider));
        filterForm.add(new OrderByBorder("header.end_date", "end_date", dataProvider));

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
                pageParameters.add("type", "document_term");

                setResponsePage(DictionaryEdit.class, pageParameters);
            }
        });
    }
}
