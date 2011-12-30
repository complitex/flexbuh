package org.complitex.flexbuh.document.web;

import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.complitex.flexbuh.common.entity.PersonProfile;
import org.complitex.flexbuh.common.service.PersonProfileBean;
import org.complitex.flexbuh.common.service.dictionary.DocumentBean;
import org.complitex.flexbuh.common.template.TemplatePage;
import org.complitex.flexbuh.common.web.component.PersonProfileChoice;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.entity.LinkedDeclaration;
import org.complitex.flexbuh.document.service.DeclarationBean;
import org.odlabs.wiquery.ui.accordion.Accordion;
import org.odlabs.wiquery.ui.accordion.AccordionActive;
import org.odlabs.wiquery.ui.dialog.Dialog;

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
    
    @EJB
    private PersonProfileBean personProfileBean;

    private final Declaration declaration;

    public DeclarationFormPage(Declaration declaration){
        this.declaration = declaration;

        init();
    }

    public DeclarationFormPage(PageParameters pageParameters) {
        Long id = pageParameters.get("id").toLongObject();

        declaration = declarationBean.getDeclaration(id);

        if (declaration != null){
            init();
        }else{
            //declaration not found
            error(getString("error_declaration_not_found"));
            setResponsePage(DeclarationList.class);
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {        
        super.renderHead(response);
        
        response.renderCSSReference(new PackageResourceReference(DeclarationFormPage.class, "declaration.css"));
    }

    private void init(){
        //security check
        if (declaration.getSessionId() != null && !declaration.getSessionId().equals(getSessionId(false))){
            throw new UnauthorizedInstantiationException(DeclarationFormPage.class);
        }

        add(new FeedbackPanel("messages"));

        add(new Label("title", declaration.getName()));
        add(new Label("templateName", declaration.getTemplateName()));

        Form form = new Form("form");
        add(form);

        //Declaration
        form.add(new DeclarationFormComponent("declaration", declaration, getSessionId(true)));

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

                item.add(new Label("label", declaration.getTemplateName() + " " + declaration.getName()));

                item.add(new DeclarationFormComponent("linked_declaration", declaration, getSessionId(true)));

                item.setRenderBodyOnly(true);
            }
        };
        accordion.add(listView);
        
        //Профиль
        final Dialog profileDialog = new Dialog("profile_dialog");
        profileDialog.setTitle(getString("profile_dialog_title"));
        add(profileDialog);
        
        Form profileForm = new Form("profile_form");
        profileDialog.add(profileForm);

        final PersonProfileChoice profileChoice = new PersonProfileChoice("profile_choice", getSessionId()){
            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return false;
            }
        };
        profileForm.add(profileChoice);

        profileForm.add(new Button("submit"){
            @Override
            public void onSubmit() {
                PersonProfile personProfile = profileChoice.getModelObject();
                declaration.setPersonProfileId(personProfile != null ? personProfile.getId() : null);

                declarationBean.save(getSessionId(true), declaration);

                getSession().info(getString("info_saved"));

                setResponsePage(DeclarationList.class);
            }
        });

        //Submit
        form.add(new Button("submit"){
            @Override
            public void onSubmit() {
                Long selected = personProfileBean.getSelectedPersonProfileId(getSessionId());

                PersonProfile personProfile = declaration.getPersonProfile();

                if ((selected != null && personProfile != null && selected.equals(personProfile.getId()))){
                    declaration.setPersonProfileId(personProfile.getId());

                    declarationBean.save(getSessionId(), declaration);

                    getSession().info(getString("info_saved"));

                    setResponsePage(DeclarationList.class);
                }else {
                    profileDialog.setAutoOpen(true);
                }
            }
        });

        form.add(new Button("cancel"){
            @Override
            public void onSubmit() {
                setResponsePage(DeclarationList.class);
            }
        }.setDefaultFormProcessing(false));
    }
}
