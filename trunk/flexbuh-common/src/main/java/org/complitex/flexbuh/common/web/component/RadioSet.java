package org.complitex.flexbuh.common.web.component;

import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.ConversionException;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 19.08.11 15:10
 */
public class RadioSet<T> extends RadioGroup<T> {
    private Set<Radio<T>> radioSet = new HashSet<Radio<T>>();

    public RadioSet(String id, IModel<T> tiModel) {
        super(id, tiModel);
        setRenderBodyOnly(false);
    }

    public void addRadio(Radio<T> radio) {
        radioSet.add(radio);
    }

    @Override
    protected T convertValue(String[] input) throws ConversionException {
        if (input != null && input.length > 0) {
            final String value = input[0];

            for (Radio<T> radio : radioSet){
                if (radio.getValue().equals(value)) {
                    return radio.getModelObject();
                }
            }
        }
        return null;
    }
}
