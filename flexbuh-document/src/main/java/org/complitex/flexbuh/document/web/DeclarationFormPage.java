package org.complitex.flexbuh.document.web;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.complitex.flexbuh.common.entity.PersonProfile;
import org.complitex.flexbuh.common.entity.dictionary.DocumentFilter;
import org.complitex.flexbuh.common.service.PersonProfileBean;
import org.complitex.flexbuh.common.service.dictionary.DocumentBean;
import org.complitex.flexbuh.common.template.TemplatePage;
import org.complitex.flexbuh.common.template.toolbar.AddItemButton;
import org.complitex.flexbuh.common.template.toolbar.ToolbarButton;
import org.complitex.flexbuh.common.web.component.IAjaxUpdate;
import org.complitex.flexbuh.common.web.component.PersonProfileChoice;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.entity.LinkedDeclaration;
import org.complitex.flexbuh.document.service.DeclarationBean;
import org.complitex.flexbuh.document.service.DeclarationService;
import org.complitex.flexbuh.document.web.component.AddLinkedDeclarationDialog;
import org.complitex.flexbuh.document.web.component.DeclarationPeriodPanel;
import org.odlabs.wiquery.ui.accordion.Accordion;
import org.odlabs.wiquery.ui.accordion.AccordionActive;
import org.odlabs.wiquery.ui.dialog.Dialog;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.08.11 15:25
 */
public class DeclarationFormPage extends TemplatePage{
    @EJB
    private DeclarationBean declarationBean;

    @EJB
    private DeclarationService declarationService;

    @EJB
    private DocumentBean documentBean;
    
    @EJB
    private PersonProfileBean personProfileBean;

    private Declaration declaration;
    
    private AddLinkedDeclarationDialog addLinkedDeclarationDialog;

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
        final Long sessionId = declaration.getSessionId();

        //security check
        if (sessionId != null && !sessionId.equals(getSessionId())){
            throw new UnauthorizedInstantiationException(DeclarationFormPage.class);
        }

        final FeedbackPanel feedbackPanel = new FeedbackPanel("messages");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);

        add(new Label("title", declaration.getName()));
        add(new Label("templateName", declaration.getTemplateName()));

        Form form = new Form("form");
        add(form);

        //Declaration
        form.add(new DeclarationFormComponent("declaration", declaration));

        form.add(new DeclarationPeriodPanel("period_panel", declaration));

        //Linked declaration
        final Accordion accordion = new Accordion("accordion");
        accordion.setCollapsible(true);
        accordion.setClearStyle(true);
        accordion.setNavigation(true);
        accordion.setActive(new AccordionActive(false));
        accordion.setOutputMarkupId(true);
        form.add(accordion);

        ListView listView = new ListView<LinkedDeclaration>("declarations",
                new LoadableDetachableModel<List<? extends LinkedDeclaration>>() {
                    @Override
                    protected List<? extends LinkedDeclaration> load() {
                        return declaration.getHead().getLinkedDeclarations() != null
                                ? declaration.getHead().getLinkedDeclarations()
                                : new ArrayList<LinkedDeclaration>();
                    }
                }) {

            @Override
            protected void populateItem(ListItem<LinkedDeclaration> item) {
                Declaration declaration = item.getModelObject().getDeclaration();

                item.add(new Label("label", declaration.getTemplateName() + " " + declaration.getName()));

                item.add(new DeclarationFormComponent("linked_declaration", declaration));

                item.add(new DeclarationPeriodPanel("period_panel", declaration));

                item.setRenderBodyOnly(true);
            }
        };
        listView.setReuseItems(true);
        accordion.add(listView);
        
        //Профиль
        final Dialog profileDialog = new Dialog("profile_dialog");
        profileDialog.setTitle(getString("profile_dialog_title"));
        add(profileDialog);
        
        Form profileForm = new Form("profile_form");
        profileDialog.add(profileForm);

        final PersonProfileChoice profileChoice = new PersonProfileChoice("profile_choice"){
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
                declaration.getHead().setTin(personProfile != null && personProfile.getTin() != null
                        ? personProfile.getTin() : 0);

                declarationService.validateAndSave(declaration);

                getSession().info(getStringFormat("info_saved"));

                if (!declaration.isValidated()) {
                    getSession().info(declaration.getValidateMessage());
                }

                if (personProfile != null) {
                    putPreference(PersonProfile.SELECTED_PERSON_PROFILE_ID, personProfile.getId().toString());
                }

                PageParameters pageParameters = new PageParameters();
                pageParameters.add("period_type", declaration.getHead().getPeriodType());
                pageParameters.add("period_month", declaration.getHead().getPeriodMonth());
                pageParameters.add("period_year", declaration.getHead().getPeriodYear());

                setResponsePage(DeclarationList.class, pageParameters);
            }
        });

        //Submit
        form.add(new Button("submit"){
            @Override
            public void onSubmit() {
                Long selected = getPreferenceLong(PersonProfile.SELECTED_PERSON_PROFILE_ID);

                if ((selected != null && selected.equals(declaration.getPersonProfileId()))){
                    declarationService.validateAndSave(declaration);

                    getSession().info(getString("info_saved"));

                    if (!declaration.isValidated()) {
                        getSession().info(declaration.getValidateMessage());
                    }

                    PageParameters pageParameters = new PageParameters();
                    pageParameters.add("period_type", declaration.getHead().getPeriodType());
                    pageParameters.add("period_month", declaration.getHead().getPeriodMonth());
                    pageParameters.add("period_year", declaration.getHead().getPeriodYear());

                    setResponsePage(DeclarationList.class, pageParameters);
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

        //Add linked declaration dialog
        addLinkedDeclarationDialog = new AddLinkedDeclarationDialog("add_dialog", new IAjaxUpdate(){

            @Override
            public void onUpdate(AjaxRequestTarget target) {
                target.add(accordion);
                target.add(feedbackPanel);
            }
        });
        add(addLinkedDeclarationDialog);
    }

    @Override
    protected List<? extends ToolbarButton> getToolbarButtons(String id) {
        List<ToolbarButton> list = new ArrayList<>();

        list.add(new AddItemButton(id, true) {
            @Override
            protected void onClick(AjaxRequestTarget target) {
                addLinkedDeclarationDialog.open(target, declaration);
            }

            @Override
            public boolean isVisible() {
                DocumentFilter filter = new DocumentFilter();
                filter.setParentCDoc(declaration.getHead().getCDoc());
                filter.setParentCDocSub(declaration.getHead().getCDocSub());
                
                return documentBean.getDocumentsCount(filter) > 0;
            }
        });
        
        return list;
    }
}
