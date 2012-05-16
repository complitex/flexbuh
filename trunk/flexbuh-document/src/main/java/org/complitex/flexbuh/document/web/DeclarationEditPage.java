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
import org.complitex.flexbuh.common.entity.FilterWrapper;
import org.complitex.flexbuh.common.entity.PersonProfile;
import org.complitex.flexbuh.common.entity.dictionary.Document;
import org.complitex.flexbuh.common.entity.user.Share;
import org.complitex.flexbuh.common.logging.Event;
import org.complitex.flexbuh.common.service.PersonProfileBean;
import org.complitex.flexbuh.common.service.dictionary.DocumentBean;
import org.complitex.flexbuh.common.service.user.ShareBean;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.List;

import static org.complitex.flexbuh.common.logging.EventCategory.CREATE;
import static org.complitex.flexbuh.common.logging.EventCategory.EDIT;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.08.11 15:25
 */
public class DeclarationEditPage extends TemplatePage{
    private final Logger log = LoggerFactory.getLogger(DeclarationEditPage.class);

    @EJB
    private DeclarationBean declarationBean;

    @EJB
    private DeclarationService declarationService;

    @EJB
    private DocumentBean documentBean;
    
    @EJB
    private PersonProfileBean personProfileBean;

    @EJB
    private ShareBean shareBean;

    private Declaration declaration;
    private Declaration oldDeclaration;
    
    private AddLinkedDeclarationDialog addLinkedDeclarationDialog;

    public DeclarationEditPage(Declaration declaration){
        this.declaration = declaration;

        init();
    }

    public DeclarationEditPage(PageParameters pageParameters) {
        Long id = pageParameters.get("id").toLongObject();

        declaration = declarationBean.getDeclaration(id);
        oldDeclaration = declarationBean.getDeclaration(id);

        if (declaration != null){
            init();
        }else{
            //declaration not found
            error(getString("error_declaration_not_found"));
            log.error("Документ не найден в базе данных", new Event(EDIT, declaration));
            setResponsePage(DeclarationList.class);
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {        
        super.renderHead(response);
        
        response.renderCSSReference(new PackageResourceReference(DeclarationEditPage.class, "declaration.css"));
    }

    private void init(){
        final Long sessionId = declaration.getSessionId();

        //security check
        if (sessionId != null && !sessionId.equals(getSessionId()) && !shareBean.isExist(new Share(sessionId, getSessionId()))){
            log.error("Доступ запрещен", new Event(EDIT, declaration));

            throw new UnauthorizedInstantiationException(DeclarationEditPage.class);
        }

        final FeedbackPanel feedbackPanel = new FeedbackPanel("messages");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);

        add(new Label("title", declaration.getName()));
        add(new Label("templateName", declaration.getTemplateName()));

        Form form = new Form("form");
        add(form);

        //Declaration
        form.add(new DeclarationEditPanel("declaration", declaration));

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

                item.add(new DeclarationEditPanel("linked_declaration", declaration));

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
                boolean isEdit = declaration.getId() != null;

                PersonProfile personProfile = profileChoice.getModelObject();

                declaration.setPersonProfileId(personProfile != null ? personProfile.getId() : null);
                declaration.getHead().setTin(personProfile != null && personProfile.getTin() != null
                        ? personProfile.getTin() : 0);

                declarationService.validateAndSave(declaration);

                getSession().info(getStringFormat("info_saved", declaration.getFullName()));

                if (!declaration.isValidated()) {
                    getSession().info(getStringFormat("info_validated", declaration.getValidateMessage()));
                }

                if (personProfile != null) {
                    putPreference(PersonProfile.SELECTED_PERSON_PROFILE_ID, personProfile.getId().toString());
                }

                PageParameters pageParameters = new PageParameters();
                pageParameters.add("period_type", declaration.getHead().getPeriodType());
                pageParameters.add("period_month", declaration.getHead().getPeriodMonth());
                pageParameters.add("period_year", declaration.getHead().getPeriodYear());

                setResponsePage(DeclarationList.class, pageParameters);

                log.info("Документ сохранен", new Event(isEdit? EDIT : CREATE, oldDeclaration, declaration));
            }
        });

        //Submit
        form.add(new Button("submit"){
            @Override
            public void onSubmit() {
                Long selectedId = getPreferenceLong(PersonProfile.SELECTED_PERSON_PROFILE_ID);

                boolean isEdit = declaration.getId() != null;

                //todo extract method
                if ((selectedId != null && selectedId.equals(declaration.getPersonProfileId()))){
                    PersonProfile personProfile = personProfileBean.getPersonProfile(selectedId);

                    declaration.getHead().setTin(personProfile != null && personProfile.getTin() != null
                                            ? personProfile.getTin() : 0);

                    declarationService.validateAndSave(declaration);

                    getSession().info(getStringFormat("info_saved", declaration.getFullName()));

                    if (!declaration.isValidated()) {
                        getSession().info(getStringFormat("info_validated", declaration.getValidateMessage()));
                    }

                    PageParameters pageParameters = new PageParameters();
                    pageParameters.add("period_type", declaration.getHead().getPeriodType());
                    pageParameters.add("period_month", declaration.getHead().getPeriodMonth());
                    pageParameters.add("period_year", declaration.getHead().getPeriodYear());

                    setResponsePage(DeclarationList.class, pageParameters);


                    log.info("Документ сохранен", new Event(isEdit ? EDIT : CREATE, oldDeclaration, declaration));
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
                FilterWrapper<Document> filter = new FilterWrapper<>(new Document());

                filter.getObject().setParentCDoc(declaration.getHead().getCDoc());
                filter.getObject().setParentCDocSub(declaration.getHead().getCDocSub());
                
                return documentBean.getDocumentsCount(filter) > 0;
            }
        });
        
        return list;
    }
}
