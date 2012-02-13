package org.complitex.flexbuh.common.web.component;

import com.google.common.collect.Lists;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;
import org.complitex.flexbuh.common.entity.FirstName;
import org.complitex.flexbuh.common.service.FIOBean;

import javax.ejb.EJB;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.complitex.flexbuh.common.service.FIOBean.SIZE;

/**
 * @author Pavel Sknar
 *         Date: 06.02.12 16:38
 */
public class FirstNameAutoCompleteTextField extends Panel {

    @EJB
    private FIOBean fioBean;

    public FirstNameAutoCompleteTextField(String id) {
        this(id, (Boolean) null);
    }

    public FirstNameAutoCompleteTextField(String id, Boolean required) {
        super(id);

        // First name
        AutoCompleteTextField<String> firstNameField = new AutoCompleteTextField<String>("firstName") {
            @Override
            protected Iterator<String> getChoices(String input) {
                return FirstNameAutoCompleteTextField.this.getChoices(input);
            }
        };
        if (required != null) {
            firstNameField.setRequired(required);
        }
        add(firstNameField);
    }

    public FirstNameAutoCompleteTextField(String id, IModel<String> model) {
        this(id, model, null);
    }

    public FirstNameAutoCompleteTextField(String id, IModel<String> model, Boolean required) {
        super(id);

        // First name
        AutoCompleteTextField<String> firstNameField = new AutoCompleteTextField<String>("firstName", model) {
            @Override
            protected Iterator<String> getChoices(String input) {
                return FirstNameAutoCompleteTextField.this.getChoices(input);
            }
        };
        if (required != null) {
            firstNameField.setRequired(required);
        }
        add(firstNameField);
    }

    private Iterator<String> getChoices(String input) {
        if (Strings.isEmpty(input)) {
            List<String> emptyList = Collections.emptyList();
            return emptyList.iterator();
        }

        List<String> choices = Lists.newArrayListWithCapacity(SIZE);

        for (FirstName firstName : fioBean.getFirstNames(input, getLocale())) {
            choices.add(firstName.getName(getLocale()));
        }

        return choices.iterator();
    }
}
