package org.complitex.flexbuh.document.web.component;

import com.google.common.collect.Ordering;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.complitex.flexbuh.common.entity.FilterWrapper;
import org.complitex.flexbuh.common.entity.dictionary.DocumentTerm;
import org.complitex.flexbuh.common.service.dictionary.DocumentTermBean;
import org.complitex.flexbuh.common.util.DateUtil;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.entity.DeclarationHead;

import javax.ejb.EJB;
import java.text.DateFormatSymbols;
import java.util.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 03.04.12 13:37
 */
public class DeclarationPeriodPanel extends Panel {
    private final DateFormatSymbols dateFormatSymbols = DateFormatSymbols.getInstance(getLocale());

    private final static int MIN_YEAR = DateUtil.getCurrentYear() - 4;
    private final static int MAX_YEAR = DateUtil.getCurrentYear() + 1;

    @EJB
    private DocumentTermBean documentTermBean;

    public DeclarationPeriodPanel(String id, final Declaration declaration) {
        super(id);

        final IModel<List<DocumentTerm>> dtModel = new LoadableDetachableModel<List<DocumentTerm>>() {
            @Override
            protected List<DocumentTerm> load() {
                DeclarationHead head = declaration.getHead();

                if (head.getCDoc() != null && head.getCDocSub() != null) {
                    DocumentTerm documentTerm = new DocumentTerm();
                    documentTerm.setCDoc(head.getCDoc());
                    documentTerm.setCDocSub(head.getCDocSub());

                    return documentTermBean.getDocumentTerms(FilterWrapper.of(documentTerm));
                }

                return Collections.emptyList();
            }
        };
        setDefaultModel(dtModel);

        //Год
        final DropDownChoice periodYearChoice = new DropDownChoice<>("period_year",
                new PropertyModel<Integer>(declaration, "head.periodYear"),
                new LoadableDetachableModel<List<Integer>>() {
                    @Override
                    protected List<Integer> load() {
                        List<DocumentTerm> documentTerms = dtModel.getObject();

                        Set<Integer> years = new HashSet<>();

                        if (!documentTerms.isEmpty()){
                            for (DocumentTerm documentTerm : documentTerms){
                                if (documentTerm.getPeriodType().equals(declaration.getHead().getPeriodType())) {
                                    years.add(documentTerm.getPeriodYear());
                                }
                            }
                        }else {
                            for (int i = MIN_YEAR; i <= MAX_YEAR; ++i){
                                years.add(i);
                            }
                        }

                        return Ordering.natural().sortedCopy(years);
                    }
                });
        periodYearChoice.setRequired(true);
        periodYearChoice.setOutputMarkupId(true);
        add(periodYearChoice);

        //Месяц (для 1,2,3,4 кварталов это 3,6,9,12 месяц соответственно, для года - 12)
        final DropDownChoice periodMonthChoice = new DropDownChoice<>("period_month",
                new PropertyModel<Integer>(declaration, "head.periodMonth"),
                new LoadableDetachableModel<List<Integer>>() {
                    @Override
                    protected List<Integer> load() {
                        List<DocumentTerm> documentTerms = dtModel.getObject();

                        if (!documentTerms.isEmpty()){
                            Set<Integer> months = new HashSet<>();

                            for (DocumentTerm documentTerm : documentTerms){
                                if (documentTerm.getPeriodType().equals(declaration.getHead().getPeriodType())){
                                    months.add(documentTerm.getPeriodMonth());
                                }
                            }

                            return Ordering.natural().sortedCopy(months);
                        }else {
                            switch (declaration.getHead().getPeriodType()){
                                case 1: return Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
                                case 2: return Arrays.asList(3, 6, 9, 12);
                                case 3: return Arrays.asList(6);
                                case 4: return Arrays.asList(9);
                                case 5: return Arrays.asList(12);
                                default: return Collections.emptyList();
                            }
                        }
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
        periodMonthChoice.setRequired(true);
        periodMonthChoice.setOutputMarkupId(true);
        periodMonthChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(periodYearChoice);
            }
        });
        add(periodMonthChoice);

        //Период (1-месяц, 2-квартал, 3-полугодие, 4-9 месяцев, 5-год)
        DropDownChoice periodTypeChoice = new DropDownChoice<>("period_type",
                new PropertyModel<Integer>(declaration, "head.periodType"),
                new LoadableDetachableModel<List<? extends Integer>>() {
                    @Override
                    protected List<? extends Integer> load() {
                        List<DocumentTerm> documentTerms = dtModel.getObject();

                        if (!documentTerms.isEmpty()){
                            Set<Integer> periodTypes = new HashSet<>();

                            for (DocumentTerm documentTerm : documentTerms){
                                periodTypes.add(documentTerm.getPeriodType());
                            }

                            return Ordering.natural().sortedCopy(periodTypes);
                        }else {
                            return Arrays.asList(1, 2, 3, 4, 5);
                        }
                    }
                }, new IChoiceRenderer<Integer>() {
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
        periodTypeChoice.setRequired(true);
        periodTypeChoice.setOutputMarkupId(true);
        periodTypeChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(periodMonthChoice);
                target.add(periodYearChoice);
            }
        });
        add(periodTypeChoice);

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
