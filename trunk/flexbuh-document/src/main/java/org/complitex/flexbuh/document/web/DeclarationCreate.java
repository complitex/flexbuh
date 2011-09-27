package org.complitex.flexbuh.document.web;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.complitex.flexbuh.document.entity.DeclarationFilter;
import org.complitex.flexbuh.entity.dictionary.Document;
import org.complitex.flexbuh.service.dictionary.DocumentBean;
import org.complitex.flexbuh.template.FormTemplatePage;
import org.complitex.flexbuh.web.component.declaration.PeriodTypeChoice;

import javax.ejb.EJB;
import java.util.Arrays;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.08.11 15:19
 */
public class DeclarationCreate extends FormTemplatePage{
    @EJB
    private DocumentBean documentBean;

    public DeclarationCreate() {
        add(new Label("title", getString("title")));
        add(new FeedbackPanel("messages"));

        final DeclarationFilter declarationFilter = new DeclarationFilter();

        Form form = new Form("form");
        add(form);

        //Тип лица
        DropDownChoice<String> person = new DropDownChoice<String>("person", new Model<>("Юридическое лицо"),
                Arrays.asList("Физическое лицо", "Юридическое лицо"));
        form.add(person);

        //Отчетный документ
        final DropDownChoice document = new DropDownChoice<>("document",
                new PropertyModel<Document>(declarationFilter, "document"),
                documentBean.getJuridicalDocuments(), new IChoiceRenderer<Document>() {
            @Override
            public Object getDisplayValue(Document object) {
                return object.getCDoc() +" " + object.getCDocSub() + " " + object.getNames().get(0).getValue();
            }

            @Override
            public String getIdValue(Document object, int index) {
                return object.getId().toString();
            }
        });
        document.setRequired(true);
        form.add(document);

        //Период
        form.add(new PeriodTypeChoice("period_type", new PropertyModel<Integer>(declarationFilter, "periodType")));

        //Квартал
        form.add(new TextField<>("period_month", new PropertyModel<Integer>(declarationFilter, "periodMonth"), Integer.class));

        //Год
        form.add(new TextField<>("period_year", new PropertyModel<Integer>(declarationFilter, "periodMonth"), Integer.class));

        form.add(new Button("submit"){
            @Override
            public void onSubmit() {
                PageParameters pageParameters = new PageParameters();
                //todo version select
                String templateName = declarationFilter.getDocument().getCDoc()
                        + declarationFilter.getDocument().getCDocSub()
                        + "0" + declarationFilter.getDocument().getDocumentVersions().get(0).getCDocVer();

                pageParameters.put("tn", templateName);
                pageParameters.put("pt", declarationFilter.getPeriodType());
                pageParameters.put("pm", declarationFilter.getPeriodMonth());
                pageParameters.put("py", declarationFilter.getPeriodYear());

                setResponsePage(DeclarationFormPage.class, pageParameters);
            }
        });

        form.add(new Button("cancel"));
    }
}
