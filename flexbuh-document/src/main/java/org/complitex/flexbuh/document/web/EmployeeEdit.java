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
import org.complitex.flexbuh.common.entity.user.Share;
import org.complitex.flexbuh.common.logging.Event;
import org.complitex.flexbuh.common.service.PersonProfileBean;
import org.complitex.flexbuh.common.service.user.ShareBean;
import org.complitex.flexbuh.common.template.FormTemplatePage;
import org.complitex.flexbuh.common.web.component.FirstNameAutoCompleteTextField;
import org.complitex.flexbuh.common.web.component.LastNameAutoCompleteTextField;
import org.complitex.flexbuh.common.web.component.MiddleNameAutoCompleteTextField;
import org.complitex.flexbuh.document.entity.Employee;
import org.complitex.flexbuh.document.service.EmployeeBean;
import org.odlabs.wiquery.ui.datepicker.DatePicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;

import static org.complitex.flexbuh.common.entity.PersonProfile.SELECTED_PERSON_PROFILE_ID;
import static org.complitex.flexbuh.common.logging.EventCategory.CREATE;
import static org.complitex.flexbuh.common.logging.EventCategory.EDIT;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 17.11.11 16:04
 */
public class EmployeeEdit extends FormTemplatePage{
    private final static Logger log = LoggerFactory.getLogger(EmployeeEdit.class);

    @EJB
    private EmployeeBean employeeBean;
    
    @EJB
    private PersonProfileBean personProfileBean;

    @EJB
    private ShareBean shareBean;

    public EmployeeEdit() {
        init(null);
    }

    public EmployeeEdit(PageParameters pageParameters) {
        init(pageParameters.get("id").toLongObject());
    }

    private void init(Long id){
        final boolean edit = id != null;
        final Employee oldEmployee = edit ? employeeBean.getEmployee(id) : null;
        final Employee employee = edit ? employeeBean.getEmployee(id) : new Employee(getSessionId());

        //Профиль
        PersonProfile personProfile = personProfileBean.getPersonProfile(getPreferenceLong(SELECTED_PERSON_PROFILE_ID));

        if (id == null && personProfile != null){
            employee.setPersonProfileId(personProfile.getId());
            employee.setSessionId(personProfile.getSessionId());
        }

        if (employee == null){
            log.error("Сотрудник по id={} не найден", id);

            throw new WicketRuntimeException("Похоже в базе нет записи по такому id");
        }else if (!employee.getSessionId().equals(getSessionId())
                && !shareBean.isExist(new Share(employee.getSessionId(), getSessionId()))){
            log.error("Доступ запрещен", new Event(EDIT, employee));

            throw new UnauthorizedInstantiationException(CounterpartEdit.class);
        }
        
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
                employeeBean.save(employee);

                log.info("Сотрудник {}", edit? "изменен":"добавлен", new Event(edit? EDIT:CREATE, oldEmployee, employee));
                info(getStringFormat("info_saved", employee.getHname()));

                setResponsePage(EmployeeList.class);
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