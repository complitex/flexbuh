package org.complitex.flexbuh.document.web.component;

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.complitex.flexbuh.common.entity.dictionary.FieldCode;
import org.complitex.flexbuh.common.util.StringUtil;
import org.complitex.flexbuh.common.web.component.AutocompleteDialogComponent;
import org.complitex.flexbuh.common.web.component.IAutocompleteDialog;
import org.complitex.flexbuh.document.entity.Employee;
import org.complitex.flexbuh.document.entity.EmployeeFilter;
import org.complitex.flexbuh.document.service.EmployeeBean;
import org.complitex.flexbuh.document.web.model.DeclarationStringModel;

import javax.ejb.EJB;
import java.text.SimpleDateFormat;
import java.util.Collections;
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

    public EmployeeAutocompleteDialog(String id, final DeclarationStringModel model, Long sessionId, IAutocompleteDialog<Employee> dialog) {
        super(id, model, model.getDeclaration().getTemplateName(), model.getName(), FieldCode.EMPLOYEE_SPR_NAME, dialog);

        this.sessionId = sessionId;
        this.model = model;
    }

    protected void updateModel(Employee employee){
        try {
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
        } catch (Exception e) {
            //ups
        }
    }

    @Override
    protected List<Employee> getValues(String tern) {
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

            return employeeBean.getEmployees(filter);
        } catch (Exception e) {
            //error can happen
        }

        return Collections.emptyList();
    }

    @Override
    public DeclarationStringModel getDeclarationModel() {
        return model;
    }

    @Override
    public String getValue() {
        return getAutocompleteComponent().getValue();
    }

    @Override
    protected IChoiceRenderer<Employee> getChoiceRenderer() {
        return new IChoiceRenderer<Employee>() {
            @Override
            public Object getDisplayValue(Employee object) {
                switch (getAlias()){
                    case "HTIN":
                        return StringUtil.getString(object.getHtin());
                    case "HDATE_IN":
                        return object.getHdateIn() != null ? DATE_FORMAT.format(object.getHdateIn()) : "";
                    case "HDATE_OUT":
                        return object.getHdateOut() != null ? DATE_FORMAT.format(object.getHdateOut()) : "";
                }

                return null;
            }

            @Override
            public String getIdValue(Employee object, int index) {
                return object.getId() != null ? object.getId().toString() : null;
            }
        };
    }
}
