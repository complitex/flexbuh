package org.complitex.flexbuh.document.web;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.entity.LinkedDeclaration;
import org.complitex.flexbuh.document.service.DeclarationBean;
import org.complitex.flexbuh.service.dictionary.DocumentBean;
import org.complitex.flexbuh.template.TemplatePage;
import org.odlabs.wiquery.ui.accordion.Accordion;
import org.odlabs.wiquery.ui.accordion.AccordionActive;

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

    private final Declaration declaration;

    public DeclarationFormPage(Declaration declaration){
        this.declaration = declaration;

        init();
    }

    public DeclarationFormPage(PageParameters pageParameters) {
        Long id = pageParameters.getAsLong("id");

        declaration = declarationBean.getDeclaration(id);

        if (declaration != null){
            init();
        }else{
            //declaration not found
            error(getString("error_declaration_not_found"));
            setResponsePage(DeclarationList.class);
        }
    }

    private void init(){
        add(CSSPackageResource.getHeaderContribution(DeclarationFormPage.class, "declaration.css"));

        add(new FeedbackPanel("feedback"));

        add(new Label("title", declaration.getName()));

        Form form = new Form("form");
        add(form);

        //Declaration
        form.add(new DeclarationFormComponent("declaration", declaration));

        //Linked declaration
        Accordion accordion = new Accordion("accordion");
        accordion.setCollapsible(true);
        accordion.setClearStyle(true);
        accordion.setNavigation(true);
        accordion.setActive(new AccordionActive(false));
        form.add(accordion);

        ListView listView = new ListView<LinkedDeclaration>("declarations", declaration.getLinkedDeclarations()) {

            @Override
            protected void populateItem(ListItem<LinkedDeclaration> item) {
                Declaration declaration = item.getModelObject().getDeclaration();

                item.add(new Label("label", declaration.getTemplateName()));

                item.add(new DeclarationFormComponent("linked_declaration", declaration));

                item.setRenderBodyOnly(true);
            }
        };
        accordion.add(listView);

        form.add(new Button("submit"){
            @Override
            public void onSubmit() {
                declarationBean.save(getSessionId(true), declaration);
            }
        });

        form.add(new Button("cancel"){
            @Override
            public void onSubmit() {

            }
        });
    }
}
