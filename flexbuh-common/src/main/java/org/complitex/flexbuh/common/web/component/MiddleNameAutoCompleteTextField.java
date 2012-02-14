package org.complitex.flexbuh.common.web.component;

import com.google.common.collect.Lists;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.Strings;
import org.complitex.flexbuh.common.entity.MiddleName;
import org.complitex.flexbuh.common.service.FIOBean;

import javax.ejb.EJB;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.complitex.flexbuh.common.service.FIOBean.SIZE;

/**
 * @author Pavel Sknar
 *         Date: 07.02.12 17:54
 */
public class MiddleNameAutoCompleteTextField extends AutoCompleteTextField<String> {

    @EJB
    private FIOBean fioBean;

    public MiddleNameAutoCompleteTextField(String id) {
        super(id);
    }

    public MiddleNameAutoCompleteTextField(String id, PropertyModel<String> model) {
        super(id, model);
    }

    @Override
    protected Iterator<String> getChoices(String input) {
        if (Strings.isEmpty(input)) {
            List<String> emptyList = Collections.emptyList();
            return emptyList.iterator();
        }

        List<String> choices = Lists.newArrayListWithCapacity(SIZE);

        for (MiddleName middleName : fioBean.getMiddleNames(input, getLocale())) {
            choices.add(middleName.getName(getLocale()));
        }

        return choices.iterator();
    }
}

