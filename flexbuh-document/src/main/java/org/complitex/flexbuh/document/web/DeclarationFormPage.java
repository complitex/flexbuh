package org.complitex.flexbuh.document.web;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.service.DeclarationBean;
import org.complitex.flexbuh.template.TemplatePage;

import javax.ejb.EJB;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.08.11 15:25
 */
public class DeclarationFormPage extends TemplatePage{
    @EJB
    private DeclarationBean declarationBean;

    public DeclarationFormPage(PageParameters pageParameters) {
        add(CSSPackageResource.getHeaderContribution(DeclarationFormPage.class, "declaration.css"));

        add(new FeedbackPanel("feedback"));

        String name = pageParameters.getString("name");

        add(new Label("title", name));

        Form form = new Form("form");
        add(form);

        final Declaration declaration = new Declaration();

        DeclarationFormComponent declarationComponent = new DeclarationFormComponent("declaration", name, declaration);
        form.add(declarationComponent);

        form.add(new Button("submit"){
            @Override
            public void onSubmit() {
                declaration.setSessionId(-1L);
                declarationBean.save(declaration);
            }
        });

        form.add(new Button("cancel"){
            @Override
            public void onSubmit() {

            }
        });
    }
}
