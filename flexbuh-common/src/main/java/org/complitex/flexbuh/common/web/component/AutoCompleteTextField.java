package org.complitex.flexbuh.common.web.component;

import com.google.common.collect.Lists;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.Strings;
import org.complitex.flexbuh.common.entity.DomainObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * @author Pavel Sknar
 *         Date: 07.03.12 15:05
 */
public abstract class AutoCompleteTextField<T extends DomainObject>
        extends org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField<String> {

    private static final Logger log = LoggerFactory.getLogger(AutoCompleteTextField.class);

    public AutoCompleteTextField(String id) {
        super(id);
    }

    protected AutoCompleteTextField(String id, AutoCompleteSettings settings) {
        super(id, settings);
    }

    public AutoCompleteTextField(String id, PropertyModel<String> model) {
        super(id, model);
    }

    protected AutoCompleteTextField(String id, IModel<String> object, AutoCompleteSettings settings) {
        super(id, object, settings);
    }

    @Override
    protected Iterator<String> getChoices(String input) {
        /*
        if (Strings.isEmpty(input)) {
            List<String> emptyList = Collections.emptyList();
            return emptyList.iterator();
        }*/

        List<String> choices = Lists.newArrayListWithCapacity(getCapacity());

        for (T organizationType : getDomainObjects(input, getLocale())) {
            choices.add(getDomainObjectName(organizationType, getLocale()));
        }

        log.debug("Choices: {}", choices);

        return choices.iterator();
    }

    abstract protected int getCapacity();

    abstract protected List<T> getDomainObjects(String input, Locale locale);

    abstract protected String getDomainObjectName(T object, Locale locale);
}
