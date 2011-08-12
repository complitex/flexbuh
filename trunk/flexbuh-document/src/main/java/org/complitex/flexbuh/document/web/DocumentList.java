package org.complitex.flexbuh.document.web;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.complitex.flexbuh.document.service.TemplateBean;
import org.complitex.flexbuh.template.FormTemplatePage;

import javax.ejb.EJB;
import java.util.Arrays;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.08.11 15:19
 */
public class DocumentList extends FormTemplatePage{
    @EJB
    private TemplateBean templateBean;

    public DocumentList() {
        add(new Label("title", getString("title")));

        Form form = new Form("form");
        add(form);

        //Тип лица
        DropDownChoice<String> person = new DropDownChoice<String>("person", new Model<String>("Юридическое лицо"),
                Arrays.asList("Физическое лицо", "Юридическое лицо"));
        form.add(person);

        //Отчетный документ
        final DropDownChoice<String> document = new DropDownChoice<String>("document", new Model<String>(""),
                templateBean.getTemplateXSLNames());
        document.setRequired(true);
        form.add(document);

        //Период
        DropDownChoice<String> period = new DropDownChoice<String>("period", new Model<String>("квартал"),
                Arrays.asList("квартал", "полугодие", "9 месяцев", "год"));
        form.add(period);

        //Квартал
        DropDownChoice<String> quarter = new DropDownChoice<String>("quarter", new Model<String>("3"),
                Arrays.asList("1", "2", "3", "4"));
        form.add(quarter);

        //Год
        DropDownChoice<String> year = new DropDownChoice<String>("year", new Model<String>("2011"),
                Arrays.asList("2010", "2011"));
        form.add(year);

        form.add(new Button("submit"){
            @Override
            public void onSubmit() {
                setResponsePage(DeclarationFormPage.class, new PageParameters("name="+document.getModelObject()));
            }
        });

        form.add(new Button("cancel"));
    }
}
