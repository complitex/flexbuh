package org.complitex.flexbuh.document.test;

import org.apache.wicket.markup.html.form.Form;
import org.complitex.flexbuh.common.service.dictionary.FieldCodeBean;
import org.complitex.flexbuh.common.template.TemplatePage;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.web.component.CounterpartAutocompleteDialog;
import org.complitex.flexbuh.document.web.component.CounterpartDialog;
import org.complitex.flexbuh.document.web.component.EmployeeAutocompleteDialog;
import org.complitex.flexbuh.document.web.component.EmployeeDialog;
import org.complitex.flexbuh.document.web.model.DeclarationStringModel;

import javax.ejb.EJB;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 18.11.11 17:38
 */
public class AutocompleteDialogTestPage extends TemplatePage{
    @EJB
    private FieldCodeBean fieldCodeBean;

    public AutocompleteDialogTestPage() {
        Form form = new Form("form");
        add(form);

        //Employee

        EmployeeDialog employeeDialog = new EmployeeDialog("e1", getSessionId(true));
        add(employeeDialog);

        form.add(new EmployeeAutocompleteDialog("autocomplete",
                new DeclarationStringModel(null, "T1RXXXXG02", null, null, null,  new Declaration("J0500103")),
                getSessionId(true), employeeDialog));

        //Counterpart

        CounterpartDialog counterpartDialog = new CounterpartDialog("c1", getSessionId(true));
        add(counterpartDialog);
        
        form.add(new CounterpartAutocompleteDialog("a1",
                       new DeclarationStringModel(null, "T1RXXXXG3S", null, null, null,  new Declaration("J0200506")),
                       getSessionId(true), counterpartDialog));
        form.add(new CounterpartAutocompleteDialog("a2",
                               new DeclarationStringModel(null, "T2RXXXXG2S", null, null, null,  new Declaration("J0200506")),
                               getSessionId(true), counterpartDialog));
    }
}
