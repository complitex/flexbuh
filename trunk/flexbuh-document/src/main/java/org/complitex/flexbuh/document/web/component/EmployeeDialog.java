package org.complitex.flexbuh.document.web.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.complitex.flexbuh.common.util.StringUtil;
import org.complitex.flexbuh.common.web.component.AutocompleteDialogComponent;
import org.complitex.flexbuh.common.web.component.IAutocompleteDialog;
import org.complitex.flexbuh.document.entity.Employee;
import org.complitex.flexbuh.document.entity.EmployeeFilter;
import org.complitex.flexbuh.document.service.EmployeeBean;
import org.odlabs.wiquery.ui.dialog.Dialog;

import javax.ejb.EJB;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 30.11.11 16:02
 */
public class EmployeeDialog extends Panel implements IAutocompleteDialog<Employee>{
    @EJB
    private EmployeeBean employeeBean;

    private AutocompleteDialogComponent<Employee> component;
    
    private Dialog dialog;

    public EmployeeDialog(String id, Long sessionId) {
        super(id);
        
        dialog = new Dialog("dialog");
        add(dialog);

        dialog.setWidth(870);

        //Form
        Form form = new Form<Employee>("form");
        dialog.add(form);
        
        final IModel<Employee> model = new Model<>(new Employee());

        form.add(new AjaxButton("select") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (model.getObject() != null && component != null){
                    component.updateModel(target, model.getObject());
                    component.updateLinked(target, model.getObject());

                    dialog.close(target);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                //no more error
            }
        });

        final RadioGroup<Employee> radioGroup = new RadioGroup<>("radio_group", model);
        form.add(radioGroup);

        List<Employee> list = employeeBean.getEmployees(new EmployeeFilter(sessionId));

        ListView listView = new ListView<Employee>("employees", list) {
            @Override
            protected void populateItem(ListItem<Employee> item) {
                Employee employee = item.getModelObject();

                item.add(new Radio<>("select", new Model<>(employee)));
                item.add(new Label("htin", StringUtil.getString(employee.getHtin())));
                item.add(new Label("hname", employee.getHname()));
                item.add(DateLabel.forDateStyle("hbirthday", new Model<>(employee.getHbirthday()), "M-"));
                item.add(DateLabel.forDateStyle("hdateIn", new Model<>(employee.getHdateIn()), "M-"));
                item.add(DateLabel.forDateStyle("hdateOut", new Model<>(employee.getHdateOut()), "M-"));
            }
        };
        radioGroup.add(listView);
    }

    @Override
    public void open(AjaxRequestTarget target, AutocompleteDialogComponent<Employee> component) {
        this.component = component;

        dialog.open(target);
    }
}
