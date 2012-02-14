package org.complitex.flexbuh.common.web.component;

import com.google.common.collect.Lists;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.model.PropertyModel;
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
public class LastNameAutoCompleteTextField extends AutoCompleteTextField<String> {

    @EJB
    private FIOBean fioBean;

    public LastNameAutoCompleteTextField(String id) {
        super(id);
    }

    public LastNameAutoCompleteTextField(String id, PropertyModel<String> model) {
        super(id, model);
    }

    @Override
    protected Iterator<String> getChoices(String input) {
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

