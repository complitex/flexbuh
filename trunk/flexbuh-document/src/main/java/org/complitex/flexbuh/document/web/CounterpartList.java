package org.complitex.flexbuh.document.web;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.flexbuh.common.template.TemplatePage;
import org.complitex.flexbuh.common.template.toolbar.AddDocumentButton;
import org.complitex.flexbuh.common.template.toolbar.ToolbarButton;
import org.complitex.flexbuh.common.web.component.BookmarkablePageLinkPanel;
import org.complitex.flexbuh.common.web.component.paging.PagingNavigator;
import org.complitex.flexbuh.document.entity.Counterpart;
import org.complitex.flexbuh.document.entity.CounterpartFilter;
import org.complitex.flexbuh.document.service.CounterpartBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 16.11.11 15:37
 */
public class CounterpartList extends TemplatePage{
    private final static Logger log = LoggerFactory.getLogger(CounterpartList.class);

    @EJB
    private CounterpartBean counterpartBean;

    public CounterpartList() {
        add(new Label("title", getString("title")));
        add(new FeedbackPanel("messages"));

        final Form<CounterpartFilter> filterForm = new Form<>("filter_form",
                new CompoundPropertyModel<>(new CounterpartFilter(getSessionId(false))));
        add(filterForm);

        filterForm.add(new TextField<>("hk")); //ИНН
        filterForm.add(new TextField<>("hname")); //Название
        filterForm.add(new TextField<>("hloc")); //Адрес
        filterForm.add(new TextField<>("htel")); //Телефон
        filterForm.add(new TextField<>("hnspdv")); //Страховой номер

        //Все
        filterForm.add(new Button("reset"){
            @Override
            public void onSubmit() {
                filterForm.getModelObject().clear();
            }
        });

        //Модель
        SortableDataProvider<Counterpart> dataProvider = new SortableDataProvider<Counterpart>() {
            @Override
            public Iterator<? extends Counterpart> iterator(int first, int count) {
                CounterpartFilter counterpartFilter = filterForm.getModelObject();

                counterpartFilter.setFirst(first);
                counterpartFilter.setCount(count);
                counterpartFilter.setSortProperty(getSort().getProperty());
                counterpartFilter.setAscending(getSort().isAscending());

                return counterpartBean.getCounterparts(counterpartFilter).iterator();
            }

            @Override
            public int size() {
                return counterpartBean.getCounterpartCount(filterForm.getModelObject());
            }

            @Override
            public IModel<Counterpart> model(Counterpart object) {
                return new Model<>(object);
            }
        };
        dataProvider.setSort("id", SortOrder.DESCENDING);

        //Таблица
        DataView<Counterpart> dataView = new DataView<Counterpart>("list", dataProvider, 10) {
            @Override
            protected void populateItem(Item<Counterpart> item) {
                final Counterpart counterpart = item.getModelObject();

                item.add(new Label("hk", counterpart.getHk()));
                item.add(new Label("hname", counterpart.getHname()));
                item.add(new Label("hloc", counterpart.getHloc()));
                item.add(new Label("htel", counterpart.getHtel()));
                item.add(new Label("hnspdv", counterpart.getHnspdv()));

                PageParameters pageParameters = new PageParameters();
                pageParameters.set("id", counterpart.getId());
                item.add(new BookmarkablePageLinkPanel<CounterpartEdit>("action_edit", getString("action_edit"),
                        CounterpartEdit.class, pageParameters));
                
                item.add(new Link("action_delete") {
                    @Override
                    public void onClick() {
                        counterpartBean.delete(counterpart.getId());
                    }
                });
            }
        };
        dataView.setOutputMarkupId(true);
        filterForm.add(dataView);

        //Постраничная навигация
        filterForm.add(new PagingNavigator("paging", dataView, "CounterpartList"));
    }

    @Override
    protected List<? extends ToolbarButton> getToolbarButtons(String id) {
        List<ToolbarButton> list = new ArrayList<>();

        list.add(new AddDocumentButton(id){
            @Override
            protected void onClick() {
                setResponsePage(CounterpartEdit.class);
            }
        });

        return list;
    }
}
