package org.complitex.flexbuh.report.web;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.flexbuh.common.entity.FilterWrapper;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.common.template.TemplatePage;
import org.complitex.flexbuh.common.template.toolbar.AddDocumentButton;
import org.complitex.flexbuh.common.template.toolbar.ToolbarButton;
import org.complitex.flexbuh.common.util.DateUtil;
import org.complitex.flexbuh.common.web.component.datatable.DataProvider;
import org.complitex.flexbuh.common.web.component.paging.PagingNavigator;
import org.complitex.flexbuh.report.entity.Report;
import org.complitex.flexbuh.report.service.ReportBean;
import org.odlabs.wiquery.ui.datepicker.DatePicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 08.06.12 18:35
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class ReportList extends TemplatePage{
    private final static Logger log = LoggerFactory.getLogger(ReportList.class);

    @EJB
    private ReportBean reportBean;

    public ReportList() {
        add(new Label("title", getString("title")));
        add(new FeedbackPanel("messages"));

        final FilterWrapper<Report> filterWrapper = FilterWrapper.of(new Report());

        final Form form = new Form<>("form");
        form.setOutputMarkupId(true);
        add(form);

        Link filter_reset = new Link("filter_reset") {

            @Override
            public void onClick() {
                form.clearInput();
                filterWrapper.setObject(new Report());
            }
        };
        form.add(filter_reset);

        //Фильтр
        form.add(new TextField<>("name", new PropertyModel<>(filterWrapper, "object.name")));
        form.add(new DatePicker<>("updated", new PropertyModel<Date>(filterWrapper, "object.updated")));

        DataProvider<Report> dataProvider = new DataProvider<Report>() {
            @Override
            protected Iterable<? extends Report> getData(int first, int count) {
                filterWrapper.setFirst(first);
                filterWrapper.setCount(count);
                filterWrapper.setSortProperty(getSort().getProperty());
                filterWrapper.setAscending(getSort().isAscending());

                return reportBean.getReportList(filterWrapper);
            }

            @Override
            protected int getSize() {
                return reportBean.getReportListCount(filterWrapper);
            }
        };
        dataProvider.setSort("updated", SortOrder.DESCENDING);

        DataView dataView = new DataView<Report>("reports", dataProvider) {
            @Override
            protected void populateItem(Item<Report> item) {
                Report report = item.getModelObject();

                PageParameters pageParameters = new PageParameters();
                pageParameters.add("id", report.getId());

                BookmarkablePageLink link = new BookmarkablePageLink<Void>("link", ReportEdit.class, pageParameters);
                item.add(link);

                link.add(new Label("name", report.getName()));

                item.add(new Label("updated", DateUtil.getStringDateTime(report.getUpdated())));
            }
        };
        form.add(dataView);

        //Сортировка
        form.add(new OrderByBorder("header.name", "name", dataProvider));
        form.add(new OrderByBorder("header.updated", "updated", dataProvider));

        //Постраничная навигация
        form.add(new PagingNavigator("paging", dataView, getClass().getName(), form));
    }

    @Override
    protected List<? extends ToolbarButton> getToolbarButtons(String id) {
        return Arrays.asList(new AddDocumentButton(id){
            @Override
            protected void onClick() {
                setResponsePage(ReportEdit.class);
            }
        });
    }
}
