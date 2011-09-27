package org.complitex.flexbuh.web.component.declaration;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

import java.util.Arrays;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 27.09.11 16:30
 */
public class PeriodTypeChoice extends DropDownChoice<Integer>{
    public PeriodTypeChoice(String id, IModel<Integer> model) {
        super(id, model, Arrays.asList(1, 2, 3, 4, 5));

        setChoiceRenderer(new IChoiceRenderer<Integer>() {
            @Override
            public Object getDisplayValue(Integer object) {
                //1-месяц, 2-квартал, 3-полугодие, 4-9 месяцев, 5-год
                return getString("period_type_" + object);
            }

            @Override
            public String getIdValue(Integer object, int index) {
                return object.toString();
            }
        });
    }
}
