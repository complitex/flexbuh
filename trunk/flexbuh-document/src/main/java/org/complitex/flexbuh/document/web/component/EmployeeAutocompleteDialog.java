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
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.complitex.flexbuh.common.service.dictionary.FieldCodeBean;
import org.complitex.flexbuh.common.util.StringUtil;
import org.complitex.flexbuh.common.web.component.AutocompleteDialogComponent;
import org.complitex.flexbuh.document.entity.Employee;
import org.complitex.flexbuh.document.entity.EmployeeFilter;
import org.complitex.flexbuh.document.service.EmployeeBean;
import org.complitex.flexbuh.document.web.model.DeclarationStringModel;
import org.odlabs.wiquery.ui.dialog.Dialog;

import javax.ejb.EJB;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.11.11 15:25
 */
public class EmployeeAutocompleteDialog extends AutocompleteDialogComponent<Employee> implements IDeclarationStringComponent{
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    @EJB
    private EmployeeBean employeeBean;

    private Long sessionId;
    private DeclarationStringModel model; 

    public EmployeeAutocompleteDialog(String id, final DeclarationStringModel model, Long sessionId) {
        super(id, model, new Model<>(new Employee()), model.getDeclaration().getTemplateName(), model.getName(),
                FieldCodeBean.EMPLOYEE_SPR_NAME);

        this.sessionId = sessionId;
        this.model = model;

        final Dialog dialog = getDialog();
        dialog.setTitle(getString("title"));
        dialog.setWidth(870);

        //Form
        Form form = new Form<Employee>("form");
        dialog.add(form);

        form.add(new AjaxButton("select") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                updateSelectModel(target);
                updateLinked(target);

                dialog.close(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                //no more error
            }
        });

        final RadioGroup<Employee> radioGroup = new RadioGroup<>("radio_group", getSelectModel());
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

    protected void updateSelectModel(Employee employee, IModel<String> model){
        switch (getAlias()){
            case "HTIN":
                model.setObject(StringUtil.getString(employee.getHtin()));
                break;
            case "HDATE_IN":
                model.setObject(DATE_FORMAT.format(employee.getHdateIn()));
                break;
            case "HDATE_OUT":
                model.setObject(DATE_FORMAT.format(employee.getHdateOut()));
                break;
        }
    }

    @Override
    protected List<String> getValues(String tern) {
        List<String> list = new ArrayList<>();

        try {
            EmployeeFilter filter = new EmployeeFilter(sessionId);

            switch (getAlias()){
                case "HTIN":
                    filter.setHtin(Integer.parseInt(tern));
                    break;
                case "HDATE_IN":
                    filter.setHdateIn(DATE_FORMAT.parse(tern));
                    break;
                case "HDATE_OUT":
                    filter.setHdateIn(DATE_FORMAT.parse(tern));
                    break;
            }

            List<Employee> employees = employeeBean.getEmployees(filter);

            switch (getAlias()){
                case "HTIN":
                    for (Employee employee : employees){
                        list.add(employee.getHtin() + "");
                    }
                    break;
                case "HDATE_IN":
                    for (Employee employee : employees){
                        list.add(DATE_FORMAT.format(employee.getHdateIn()));
                    }
                    break;
                case "HDATE_OUT":
                    for (Employee employee : employees){
                        list.add(DATE_FORMAT.format(employee.getHdateOut()));
                    }
                    break;
            }
        } catch (Exception e) {
            //error can happen
        }

        return list;
    }

    @Override
    public DeclarationStringModel getDeclarationModel() {
        return model;
    }

    @Override
    public String getValue() {
        return getAutocompleteComponent().getValue();
    }
}
