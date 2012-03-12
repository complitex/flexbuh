package org.complitex.flexbuh.common.web.component;

import com.google.common.collect.Lists;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.Strings;
import org.complitex.flexbuh.common.entity.LocalizedDomainObject;

import javax.ejb.EJB;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * @author Pavel Sknar
 *         Date: 07.03.12 15:05
 */
public abstract class AutoCompleteTextField<T extends LocalizedDomainObject>
        extends org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField<String> {


    public AutoCompleteTextField(String id) {
        super(id);
    }

    public AutoCompleteTextField(String id, PropertyModel<String> model) {
        super(id, model);
    }

    @Override
    protected Iterator<String> getChoices(String input) {
        if (Strings.isEmpty(input)) {
            List<String> emptyList = Collections.emptyList();
            return emptyList.iterator();
        }

        List<String> choices = Lists.newArrayListWithCapacity(getCapacity());

        for (T organizationType : getLocalizedDomainObjects(input, getLocale())) {
            choices.add(organizationType.getName(getLocale()));
        }

        return choices.iterator();
    }

    abstract protected int getCapacity();

    abstract protected List<T> getLocalizedDomainObjects(String input, Locale locale);
}
