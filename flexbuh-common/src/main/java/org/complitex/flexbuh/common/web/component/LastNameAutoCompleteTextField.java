package org.complitex.flexbuh.common.web.component;

import com.google.common.collect.Lists;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;
import org.complitex.flexbuh.common.entity.LastName;
import org.complitex.flexbuh.common.service.FIOBean;

import javax.ejb.EJB;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.complitex.flexbuh.common.service.FIOBean.SIZE;

/**
 * @author Pavel Sknar
 *         Date: 07.02.12 18:00
 */
public class LastNameAutoCompleteTextField extends Panel {

    @EJB
    private FIOBean fioBean;

    public LastNameAutoCompleteTextField(String id) {
        this(id, (Boolean) null);
    }

    public LastNameAutoCompleteTextField(String id, Boolean required) {
        super(id);

        // Last name
        final AutoCompleteTextField<String> lastNameField = new AutoCompleteTextField<String>("lastName") {
            @Override
            protected Iterator<String> getChoices(String input) {
                return LastNameAutoCompleteTextField.this.getChoices(input);
            }
        };
        if (required != null) {
            lastNameField.setRequired(required);
        }
        add(lastNameField);
    }

    public LastNameAutoCompleteTextField(String id, IModel<String> model) {
        this(id, model, null);
    }

    public LastNameAutoCompleteTextField(String id, IModel<String> model, Boolean required) {
        super(id);

        // Last name
        final AutoCompleteTextField<String> lastNameField = new AutoCompleteTextField<String>("lastName", model) {
            @Override
            protected Iterator<String> getChoices(String input) {
                return LastNameAutoCompleteTextField.this.getChoices(input);
            }
        };
        if (required != null) {
            lastNameField.setRequired(required);
        }
        add(lastNameField);
    }

    private Iterator<String> getChoices(String input) {
        if (Strings.isEmpty(input)) {
            List<String> emptyList = Collections.emptyList();
            return emptyList.iterator();
        }

        List<String> choices = Lists.newArrayListWithCapacity(SIZE);

        for (LastName lastName : fioBean.getLastNames(input, getLocale())) {
            choices.add(lastName.getName(getLocale()));
        }

        return choices.iterator();
    }
}

