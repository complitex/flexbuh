package org.complitex.flexbuh.document.web;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.complitex.flexbuh.common.entity.PersonProfile;
import org.complitex.flexbuh.common.entity.PersonType;
import org.complitex.flexbuh.common.entity.dictionary.Document;
import org.complitex.flexbuh.common.service.PersonProfileBean;
import org.complitex.flexbuh.common.service.dictionary.DocumentBean;
import org.complitex.flexbuh.common.template.FormTemplatePage;
import org.complitex.flexbuh.common.util.DateUtil;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.service.DeclarationService;
import org.complitex.flexbuh.document.web.component.DeclarationPeriodPanel;

import javax.ejb.EJB;
import java.util.Arrays;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.08.11 15:19
 */
public class DeclarationCreate extends FormTemplatePage{
    @EJB
    private DocumentBean documentBean;

    @EJB
    private PersonProfileBean personProfileBean;

    @EJB
    private DeclarationService declarationService;

    public DeclarationCreate() {
        add(new Label("title", getString("title")));
        add(new FeedbackPanel("messages"));

        final Declaration declaration = new Declaration();

        //Default date
        int month = DateUtil.getCurrentMonth() + (DateUtil.getCurrentDay() < 20 ? 0 : 1);
        int year = DateUtil.getCurrentYear();
                
        declaration.getHead().setPeriodMonth(month > 0 ? month : 1);
        declaration.getHead().setPeriodYear(month > 0 ? year : year - 1);

        Form form = new Form("form");
        add(form);

        //Выбранный профиль
        final PersonProfile selectedPersonProfile = personProfileBean.getSelectedPersonProfile(getSessionId());

        //Тип лица
        IModel<PersonType> personModel = new Model<>(selectedPersonProfile != null
                ? selectedPersonProfile.getPersonType() : PersonType.JURIDICAL_PERSON);

        final DropDownChoice<PersonType> person = new DropDownChoice<>("person", personModel,
                Arrays.asList(PersonType.JURIDICAL_PERSON, PersonType.PHYSICAL_PERSON),
                new IChoiceRenderer<PersonType>() {
                    @Override
                    public Object getDisplayValue(PersonType object) {
                        return getString(object.name());
                    }

                    @Override
                    public String getIdValue(PersonType object, int index) {
                        return object.getCode() + "";
                    }
                });
        person.setEnabled(selectedPersonProfile == null);
        person.setOutputMarkupId(true);
        form.add(person);

        //Профиль
        declaration.setPersonProfile(selectedPersonProfile);
        
        final DropDownChoice<PersonProfile> personProfile = new DropDownChoice<>("person_profile",
                new PropertyModel<PersonProfile>(declaration, "personProfile"),
                new LoadableDetachableModel<List<PersonProfile>>() {
                    @Override
                    protected List<PersonProfile> load() {
                        return personProfileBean.getAllPersonProfiles(getSessionId());
                    }
                },
                new IChoiceRenderer<PersonProfile>() {
                    @Override
                    public Object getDisplayValue(PersonProfile object) {
                        return object.getProfileName();
                    }

                    @Override
                    public String getIdValue(PersonProfile object, int index) {
                        return object.getId().toString();
                    }
                });
        personProfile.setEnabled(selectedPersonProfile == null);
        personProfile.setNullValid(true);
        person.setOutputMarkupId(true);
        form.add(personProfile);

        //Отчетный документ
        final DropDownChoice document = new DropDownChoice<>("document",
                new PropertyModel<Document>(declaration, "document"),
                new LoadableDetachableModel<List<Document>>() {
                    @Override
                    protected List<Document> load() {
                        switch (person.getModelObject()) {
                            case JURIDICAL_PERSON:
                                return documentBean.getJuridicalDocuments();
                            case PHYSICAL_PERSON:
                                return documentBean.getPhysicalDocuments();
                        }

                        return null;
                    }
                },
                new IChoiceRenderer<Document>() {
                    @Override
                    public Object getDisplayValue(Document object) {
                        return object.getCDoc() + " " + object.getCDocSub() + " " + object.getName(getLocale());
                    }

                    @Override
                    public String getIdValue(Document object, int index) {
                        return object.getId().toString();
                    }
                }
        );
        document.setOutputMarkupId(true);
        document.setRequired(true);
        form.add(document);

        person.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(document);

                PersonProfile p = personProfile.getModelObject();
                if (p != null && !p.getPersonType().equals(person.getModelObject())){
                    personProfile.setModelObject(null);
                    target.add(personProfile);
                }
            }
        });

        personProfile.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                PersonProfile p = personProfile.getModelObject();

                if (p != null && !p.getPersonType().equals(person.getModelObject())){
                    person.setModelObject(p.getPersonType());
                    target.add(document);
                }

                person.setEnabled(p == null);

                target.add(person);
            }
        });

        //Period
        form.add(new DeclarationPeriodPanel("period_panel", declaration));

        form.add(new Button("submit"){
            @Override
            public void onSubmit() {
                //version
                declaration.updateVersion();

                if (declaration.getHead().getCDocVer() == null) {
                    error(getStringFormat("error_not_version", declaration.getShortName()));

                    return;
                }

                declaration.setSessionId(getSessionId());

                //Person profile
                PersonProfile pp = declaration.getPersonProfile();

                if (pp != null) {
                    declaration.setPersonProfileId(declaration.getPersonProfile().getId());
                    declaration.getHead().setTin(pp.getTin() != null ? pp.getTin() : 0);
                }

                //check duplicate
//                if (declarationService.hasStoredDeclaration(declaration)){
//                    error(getStringFormat("error_same_period", declaration.getShortName()));
//
//                    return;
//                }

                setResponsePage(new DeclarationFormPage(declaration));
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
