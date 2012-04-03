package org.complitex.flexbuh.document.web.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.complitex.flexbuh.common.util.DateUtil;
import org.complitex.flexbuh.common.web.component.declaration.PeriodTypeChoice;
import org.complitex.flexbuh.document.entity.Declaration;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 03.04.12 13:37
 */
public class DeclarationPeriodPanel extends Panel {
    private final DateFormatSymbols dateFormatSymbols = DateFormatSymbols.getInstance(getLocale());

    private final static int MIN_YEAR = DateUtil.getCurrentYear() - 4;
    private final static int MAX_YEAR = DateUtil.getCurrentYear() + 1;

    public DeclarationPeriodPanel(String id, final Declaration declaration) {
        super(id);

        //Месяц (для 1,2,3,4 кварталов это 3,6,9,12 месяц соответственно, для года - 12)
        final DropDownChoice periodMonthChoice = new DropDownChoice<>("period_month",
                new PropertyModel<Integer>(declaration, "head.periodMonth"),
                new LoadableDetachableModel<List<Integer>>() {
                    @Override
                    protected List<Integer> load() {
                        switch (declaration.getHead().getPeriodType()){
                            case 1: return Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
                            case 2: return Arrays.asList(3, 6, 9, 12);
                            case 3: return Arrays.asList(6);
                            case 4: return Arrays.asList(9);
                            case 5: return Arrays.asList(12);
                        }

                        return Collections.emptyList();
                    }
                }, new IChoiceRenderer<Integer>() {
            @Override
            public Object getDisplayValue(Integer object) {
                return dateFormatSymbols.getMonths()[object - 1];
            }

            @Override
            public String getIdValue(Integer object, int index) {
                return object.toString();
            }
        });
        periodMonthChoice.setOutputMarkupId(true);
        add(periodMonthChoice);

        //Период (1-месяц, 2-квартал, 3-полугодие, 4-9 месяцев, 5-год)
        PeriodTypeChoice periodTypeChoice = new PeriodTypeChoice("period_type", new PropertyModel<Integer>(declaration, "head.periodType"));
        periodTypeChoice.setRequired(true);
        periodTypeChoice.setOutputMarkupId(true);
        add(periodTypeChoice);
        periodTypeChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(periodMonthChoice);
            }
        });

        //Год
        List<Integer> years = new ArrayList<>();
        for (int i = MIN_YEAR; i <= MAX_YEAR; ++i){
            years.add(i);
        }
        add(new DropDownChoice<>("period_year", new PropertyModel<Integer>(declaration, "head.periodYear"), years).setRequired(true));

        //Номер документа
        add(new TextField<>("c_doc_type", new PropertyModel<Integer>(declaration, "head.cDocType")).setRequired(true));

        //Номер однотипного документа
        add(new TextField<>("c_doc_cnt", new PropertyModel<Integer>(declaration, "head.cDocCnt")).setRequired(true));

        //Тип документа
        add(new DropDownChoice<>("c_doc_stan", new PropertyModel<Integer>(declaration, "head.cDocStan"),
                Arrays.asList(1, 2, 3), new IChoiceRenderer<Integer>() {
            @Override
            public Object getDisplayValue(Integer object) {
                return getString("c_doc_stan_" + object);
            }

            @Override
            public String getIdValue(Integer object, int index) {
                return object.toString();
            }
        }));
    }
}
