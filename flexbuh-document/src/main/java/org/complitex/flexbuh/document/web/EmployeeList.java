package org.complitex.flexbuh.document.web;

import org.apache.wicket.datetime.markup.html.basic.DateLabel;
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
import org.complitex.flexbuh.common.util.StringUtil;
import org.complitex.flexbuh.common.web.component.BookmarkablePageLinkPanel;
import org.complitex.flexbuh.common.web.component.paging.PagingNavigator;
import org.complitex.flexbuh.document.entity.Employee;
import org.complitex.flexbuh.document.entity.EmployeeFilter;
import org.complitex.flexbuh.document.service.EmployeeBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 17.11.11 16:05
 */
public class EmployeeList extends TemplatePage{
    private final static Logger log = LoggerFactory.getLogger(CounterpartList.class);

    @EJB
    private EmployeeBean employeeBean;

    public EmployeeList() {
        add(new Label("title", getString("title")));
        add(new FeedbackPanel("messages"));

        final Form<EmployeeFilter> filterForm = new Form<>("filter_form",
                new CompoundPropertyModel<>(new EmployeeFilter(getSessionId(false))));
        add(filterForm);

        filterForm.add(new TextField<>("htin")); //Идентификационный номер
        filterForm.add(new TextField<>("hname")); //ФИО
        filterForm.add(new TextField<>("hbirthday")); //Дата рождения
        filterForm.add(new TextField<>("hdateIn")); //Дата принятия на работу
        filterForm.add(new TextField<>("hdateOut")); //Дата увольнения

        //Все
        filterForm.add(new Button("reset"){
            @Override
            public void onSubmit() {
                filterForm.getModelObject().clear();
            }
        });

        //Модель
        SortableDataProvider<Employee> dataProvider = new SortableDataProvider<Employee>() {
            @Override
            public Iterator<? extends Employee> iterator(int first, int count) {
                EmployeeFilter filter = filterForm.getModelObject();

                filter.setFirst(first);
                filter.setCount(count);
                filter.setSortProperty(getSort().getProperty());
                filter.setAscending(getSort().isAscending());

                return employeeBean.getEmployees(filter).iterator();
            }

            @Override
            public int size() {
                return employeeBean.getEmployeesCount(filterForm.getModelObject());
            }

            @Override
            public IModel<Employee> model(Employee object) {
                return new Model<>(object);
            }
        };
        dataProvider.setSort("id", SortOrder.DESCENDING);

        //Таблица
        DataView<Employee> dataView = new DataView<Employee>("list", dataProvider, 10) {
            @Override
            protected void populateItem(Item<Employee> item) {
                final Employee employee = item.getModelObject();

                item.add(new Label("htin", StringUtil.getString(employee.getHtin())));
                item.add(new Label("hname", employee.getHname()));
                item.add(DateLabel.forDateStyle("hbirthday", new Model<>(employee.getHbirthday()), "M-"));
                item.add(DateLabel.forDateStyle("hdateIn", new Model<>(employee.getHdateIn()), "M-"));
                item.add(DateLabel.forDateStyle("hdateOut", new Model<>(employee.getHdateOut()), "M-"));

                PageParameters pageParameters = new PageParameters();
                pageParameters.set("id", employee.getId());
                item.add(new BookmarkablePageLinkPanel<EmployeeEdit>("action_edit", getString("action_edit"),
                        EmployeeEdit.class, pageParameters));

                item.add(new Link("action_delete") {
                    @Override
                    public void onClick() {
                        employeeBean.delete(employee.getId());
                    }
                });
            }
        };
        dataView.setOutputMarkupId(true);
        filterForm.add(dataView);

        //Постраничная навигация
        filterForm.add(new PagingNavigator("paging", dataView, "EmployeeList"));
    }

    @Override
    protected List<? extends ToolbarButton> getToolbarButtons(String id) {
        List<ToolbarButton> list = new ArrayList<>();

        list.add(new AddDocumentButton(id){
            @Override
            protected void onClick() {
                setResponsePage(EmployeeEdit.class);
            }
        });

        return list;
    }
}
