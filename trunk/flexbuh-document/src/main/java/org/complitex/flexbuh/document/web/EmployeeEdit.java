package org.complitex.flexbuh.document.web;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.flexbuh.common.entity.PersonProfile;
import org.complitex.flexbuh.common.service.PersonProfileBean;
import org.complitex.flexbuh.common.template.FormTemplatePage;
import org.complitex.flexbuh.common.web.component.FirstNameAutoCompleteTextField;
import org.complitex.flexbuh.common.web.component.LastNameAutoCompleteTextField;
import org.complitex.flexbuh.common.web.component.MiddleNameAutoCompleteTextField;
import org.complitex.flexbuh.document.entity.Employee;
import org.complitex.flexbuh.document.service.EmployeeBean;
import org.odlabs.wiquery.ui.datepicker.DatePicker;

import javax.ejb.EJB;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 17.11.11 16:04
 */
public class EmployeeEdit extends FormTemplatePage{
    @EJB
    private EmployeeBean employeeBean;
    
    @EJB
    private PersonProfileBean personProfileBean;

    public EmployeeEdit() {
        init(null);
    }

    public EmployeeEdit(PageParameters pageParameters) {
        init(pageParameters.get("id").toLongObject());
    }

    private void init(Long id){
        Employee employee = id != null ? employeeBean.getEmployee(id) : new Employee(getSessionId());

        if (employee == null){
            throw new WicketRuntimeException("Похоже в базе нет записи по такому id");
        }else if (!employee.getSessionId().equals(getSessionId())){
            throw new UnauthorizedInstantiationException(CounterpartEdit.class);
        }
        
        //Профиль
        employee.setPersonProfileId(getPreferenceLong(PersonProfile.SELECTED_PERSON_PROFILE_ID));
        
        add(new Label("title", getString("title")));
        add(new FeedbackPanel("messages"));

        final Form<Employee> form = new Form<>("form", new CompoundPropertyModel<>(employee));
        add(form);

        form.add(new TextField<>("htin", Integer.class));
        form.add(new LastNameAutoCompleteTextField("lastName").setRequired(true));
        form.add(new FirstNameAutoCompleteTextField("firstName").setRequired(true));
        form.add(new MiddleNameAutoCompleteTextField("middleName"));
        form.add(new DatePicker("hbirthday"));
        form.add(new DatePicker("hdateIn"));
        form.add(new DatePicker("hdateOut"));

        form.add(new Button("submit"){
            @Override
            public void onSubmit() {
                employeeBean.save(form.getModelObject());

                setResponsePage(EmployeeList.class);

                info(getString("info_saved"));
            }
        });

        form.add(new Button("cancel"){
            @Override
            public void onSubmit() {
                setResponsePage(EmployeeList.class);
            }
        }.setDefaultFormProcessing(false));
    }
}