package org.complitex.flexbuh.document.web;

import org.apache.wicket.PageParameters;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.entity.DeclarationFilter;
import org.complitex.flexbuh.document.service.DeclarationBean;
import org.complitex.flexbuh.template.TemplatePage;
import org.complitex.flexbuh.util.StringUtil;
import org.complitex.flexbuh.web.component.BookmarkablePageLinkPanel;
import org.complitex.flexbuh.web.component.datatable.ArrowOrderByBorder;
import org.complitex.flexbuh.web.component.declaration.PeriodTypeChoice;
import org.complitex.flexbuh.web.component.paging.PagingNavigator;
import org.odlabs.wiquery.ui.datepicker.DatePicker;

import javax.ejb.EJB;
import java.util.Date;
import java.util.Iterator;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 08.09.11 18:51
 */
public class DeclarationList extends TemplatePage{
    @EJB
    private DeclarationBean declarationBean;

    public DeclarationList() {
        add(new Label("title", getString("title")));
        add(new FeedbackPanel("messages"));

        final DeclarationFilter filter = new DeclarationFilter(getSessionId(false));

        Form filterForm = new Form("filter_form");
        add(filterForm);

        //Фильтр
        DeclarationFilter declarationFilter = new DeclarationFilter();

        filterForm.add(new TextField<>("name", new PropertyModel<String>(declarationFilter, "name")));
        filterForm.add(new PeriodTypeChoice("period_type", new PropertyModel<Integer>(declarationFilter, "periodType")));
        filterForm.add(new TextField<>("period_month", new PropertyModel<Integer>(declarationFilter, "periodMonth")));
        filterForm.add(new TextField<>("period_year", new PropertyModel<Integer>(declarationFilter, "periodYear")));
        filterForm.add(new DatePicker<>("date", new PropertyModel<Date>(declarationFilter, "date")));

        filterForm.add(new Button("reset"));

        //Модель
        SortableDataProvider<Declaration> dataProvider = new SortableDataProvider<Declaration>() {
            @Override
            public Iterator<? extends Declaration> iterator(int first, int count) {
                filter.setFirst(first);
                filter.setCount(count);

                return declarationBean.getDeclarations(filter).iterator();
            }

            @Override
            public int size() {
                return declarationBean.getDeclarationsCount(filter);
            }

            @Override
            public IModel<Declaration> model(Declaration object) {
                return new Model<>(object);
            }
        };

        //Таблица
        DataView<Declaration> dataView = new DataView<Declaration>("declarations", dataProvider) {
            @Override
            protected void populateItem(Item<Declaration> item) {
                Declaration declaration = item.getModelObject();

                item.add(new Label("name", declaration.getName()));
                item.add(new Label("period_type", StringUtil.getString(declaration.getHead().getPeriodType())));
                item.add(new Label("period_month", StringUtil.getString(declaration.getHead().getPeriodMonth())));
                item.add(new Label("period_year", StringUtil.getString(declaration.getHead().getPeriodYear())));
                item.add(DateLabel.forDatePattern("date", new Model<>(declaration.getDate()), "dd.MM.yyyy"));

                item.add(new BookmarkablePageLinkPanel<>("action_edit", getString("edit"), DeclarationFormPage.class,
                        new PageParameters("id=" + declaration.getId())));
                item.add(new EmptyPanel("action_download"));
            }
        };

        filterForm.add(dataView);

        //Названия колонок и сортировка
        filterForm.add(new ArrowOrderByBorder("header.name", "login", dataProvider, dataView, filterForm));
        filterForm.add(new ArrowOrderByBorder("header.period_type", "periodType", dataProvider, dataView, filterForm));
        filterForm.add(new ArrowOrderByBorder("header.period_month", "periodMonth", dataProvider, dataView, filterForm));
        filterForm.add(new ArrowOrderByBorder("header.period_year", "periodYear", dataProvider, dataView, filterForm));
        filterForm.add(new ArrowOrderByBorder("header.date", "date", dataProvider, dataView, filterForm));

        //Постраничная навигация
        filterForm.add(new PagingNavigator("paging", dataView, DeclarationList.class.getName(), filterForm));
    }
}
