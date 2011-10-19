package org.complitex.flexbuh.document.web;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.entity.LinkedDeclaration;
import org.complitex.flexbuh.document.entity.PersonProfile;
import org.complitex.flexbuh.document.service.PersonProfileBean;
import org.complitex.flexbuh.entity.dictionary.Document;
import org.complitex.flexbuh.entity.dictionary.DocumentVersion;
import org.complitex.flexbuh.service.dictionary.DocumentBean;
import org.complitex.flexbuh.template.FormTemplatePage;
import org.complitex.flexbuh.web.component.declaration.PeriodTypeChoice;

import javax.ejb.EJB;
import java.text.DateFormatSymbols;
import java.util.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.08.11 15:19
 */
public class DeclarationCreate extends FormTemplatePage{
    @EJB
    private DocumentBean documentBean;

    @EJB
    private PersonProfileBean personProfileBean;

    private final DateFormatSymbols dateFormatSymbols = DateFormatSymbols.getInstance(getLocale());

    private final static int MIN_YEAR = 1990;
    private final static int MAX_YEAR = Calendar.getInstance().get(Calendar.YEAR);

    public DeclarationCreate() {
        add(new Label("title", getString("title")));
        add(new FeedbackPanel("messages"));

        final Declaration declaration = new Declaration();

        Form form = new Form("form");
        add(form);

        //Тип лица
        DropDownChoice<String> person = new DropDownChoice<String>("person", new Model<>("Юридическое лицо"),
                Arrays.asList("Физическое лицо", "Юридическое лицо"));
        person.setEnabled(false);
        form.add(person);

        //Профиль
        DropDownChoice personProfile = new DropDownChoice<>("person_profile",
                new PropertyModel<PersonProfile>(declaration, "personProfile"),
                new LoadableDetachableModel<List<PersonProfile>>() {
                    @Override
                    protected List<PersonProfile> load() {
                        return personProfileBean.getAllPersonProfiles(getSessionId(false));
                    }
                },
                new IChoiceRenderer<PersonProfile>() {
                    @Override
                    public Object getDisplayValue(PersonProfile object) {
                        return object.getName();
                    }

                    @Override
                    public String getIdValue(PersonProfile object, int index) {
                        return object.getId().toString();
                    }
                });
        form.add(personProfile);

        //Отчетный документ
        form.add(new DropDownChoice<>("document",
                new PropertyModel<Document>(declaration, "document"),
                documentBean.getJuridicalDocuments(), new IChoiceRenderer<Document>() {
            @Override
            public Object getDisplayValue(Document object) {
                //todo add locale
                return object.getCDoc() +" " + object.getCDocSub() + " " + object.getName(getLocale());
            }

            @Override
            public String getIdValue(Document object, int index) {
                return object.getId().toString();
            }
        }).setRequired(true));

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
        form.add(periodMonthChoice);

        //Период (1-месяц, 2-квартал, 3-полугодие, 4-9 месяцев, 5-год)
        PeriodTypeChoice periodTypeChoice = new PeriodTypeChoice("period_type", new PropertyModel<Integer>(declaration, "head.periodType"));
        periodTypeChoice.setRequired(true);
        periodTypeChoice.setOutputMarkupId(true);
        form.add(periodTypeChoice);
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
        form.add(new DropDownChoice<>("period_year", new PropertyModel<Integer>(declaration, "head.periodYear"), years).setRequired(true));

        form.add(new Button("submit"){
            @Override
            public void onSubmit() {
                final List<LinkedDeclaration> linkedDeclarations = new ArrayList<>();

                List<Document> linkedDocuments = documentBean.getLinkedDocuments(declaration.getHead().getCDoc(), declaration.getHead().getCDocSub());

                for (Document document : linkedDocuments){
                    linkedDeclarations.add(createLinkedDeclaration(document, declaration));
                }

                declaration.setLinkedDeclarations(linkedDeclarations);

                //todo add version
                declaration.getHead().setCDocVer(declaration.getDocument().getDocumentVersions().get(0).getCDocVer());

                setResponsePage(new DeclarationFormPage(declaration));
            }
        });

        form.add(new Button("cancel"));
    }

    private LinkedDeclaration createLinkedDeclaration(Document document, Declaration parent){
        Declaration declaration = new Declaration();

        declaration.setDocument(document);

        //todo add version
        List<DocumentVersion> dv = document.getDocumentVersions();

        if (dv != null && !dv.isEmpty()){
            declaration.getHead().setCDocVer(dv.get(0).getCDocVer());
        }

        declaration.setParent(parent);

        return new LinkedDeclaration(declaration);
    }
}
