package org.complitex.flexbuh.document.web;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.service.DeclarationBean;
import org.complitex.flexbuh.service.dictionary.DocumentBean;
import org.complitex.flexbuh.template.TemplatePage;

import javax.ejb.EJB;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.08.11 15:25
 */
public class DeclarationFormPage extends TemplatePage{
    @EJB
    private DeclarationBean declarationBean;

    @EJB
    private DocumentBean documentBean;

    public DeclarationFormPage(PageParameters pageParameters) {
        add(CSSPackageResource.getHeaderContribution(DeclarationFormPage.class, "declaration.css"));

        add(new FeedbackPanel("feedback"));

        String name = pageParameters.getString("name");
        Long id = pageParameters.getAsLong("id");

        final Declaration declaration;

        if (id != null) {
            declaration = declarationBean.getDeclaration(id);

            //declaration not found
            if (declaration == null){
                error(getString("error_declaration_not_found"));
                setResponsePage(DeclarationList.class);

                return;
            }
        }else{
            declaration = new Declaration();
            declaration.setName(name);
        }

        add(new Label("title", name));

        Form form = new Form("form");
        add(form);

//        List<Document> linkedDocuments = documentBean.getLinkedDocuments()

        DeclarationFormComponent declarationComponent = new DeclarationFormComponent("declaration", declaration);
        form.add(declarationComponent);

        //todo add linked declaration, add wiquery accordion

        form.add(new Button("submit"){
            @Override
            public void onSubmit() {
                declaration.setSessionId(getSessionId(true));
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
