package org.complitex.flexbuh.document.test;

import org.apache.wicket.markup.html.form.Form;
import org.complitex.flexbuh.common.service.dictionary.FieldCodeBean;
import org.complitex.flexbuh.common.template.TemplatePage;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.web.component.CounterpartAutocompleteDialog;
import org.complitex.flexbuh.document.web.component.EmployeeAutocompleteDialog;
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

        form.add(new EmployeeAutocompleteDialog("autocomplete",
                new DeclarationStringModel(null, "T1RXXXXG02", null, null, new Declaration("J0500103")),
                getSessionId(true)));

        form.add(new CounterpartAutocompleteDialog("a1",
                       new DeclarationStringModel(null, "T1RXXXXG3S", null, null, new Declaration("J0200506")),
                       getSessionId(true)));
        form.add(new CounterpartAutocompleteDialog("a2",
                               new DeclarationStringModel(null, "T2RXXXXG2S", null, null, new Declaration("J0200506")),
                               getSessionId(true)));
    }
}
