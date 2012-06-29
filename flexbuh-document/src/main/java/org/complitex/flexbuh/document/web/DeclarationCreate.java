package org.complitex.flexbuh.document.web;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.AjaxIndicatorAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.complitex.flexbuh.common.entity.PersonProfile;
import org.complitex.flexbuh.common.entity.PersonType;
import org.complitex.flexbuh.common.entity.dictionary.Document;
import org.complitex.flexbuh.common.logging.Event;
import org.complitex.flexbuh.common.logging.EventCategory;
import org.complitex.flexbuh.common.service.PersonProfileBean;
import org.complitex.flexbuh.common.service.dictionary.DocumentBean;
import org.complitex.flexbuh.common.template.FormTemplatePage;
import org.complitex.flexbuh.common.util.DateUtil;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.service.DeclarationService;
import org.complitex.flexbuh.document.web.component.DeclarationPeriodPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.Arrays;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.08.11 15:19
 */
public class DeclarationCreate extends FormTemplatePage{
    private final static Logger log = LoggerFactory.getLogger(DeclarationCreate.class);

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
        Long selectedPersonProfileId = getPreferenceLong(PersonProfile.SELECTED_PERSON_PROFILE_ID);
        final PersonProfile selectedPersonProfile = selectedPersonProfileId != null
                ? personProfileBean.getPersonProfile(selectedPersonProfileId)
                : null;

        //Тип лица
        IModel<PersonType> personModel = new Model<>(selectedPersonProfile != null
                ? selectedPersonProfile.getPersonType()
                : PersonType.JURIDICAL_PERSON);

        final DropDownChoice<PersonType> person = new DropDownChoice<>("person", personModel,
                Arrays.asList(PersonType.values()),
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
                        return personProfileBean.getAllSharedPersonProfiles(getSessionId());
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

        //Период
        final DeclarationPeriodPanel periodPanel = new DeclarationPeriodPanel("period_panel", declaration);
        periodPanel.setOutputMarkupId(true);
        form.add(periodPanel);

        //Фильтр
        final TextField<String> filter = new TextField<>("filter", Model.of(""));
        filter.setConvertEmptyInputStringToNull(false);
        form.add(filter);

        //Отчетный документ
        final DropDownChoice document = new DropDownChoice<>("document",
                new PropertyModel<Document>(declaration, "document"),
                new LoadableDetachableModel<List<Document>>() {
                    @Override
                    protected List<Document> load() {
                        switch (person.getModelObject()) {
                            case PHYSICAL_PERSON:
                                return documentBean.getPhysicalDocuments(filter.getModelObject());
                            default:
                                return documentBean.getJuridicalDocuments(filter.getModelObject());
                        }
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
        document.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(periodPanel);
            }
        });
        document.add(new AjaxIndicatorAppender());
        form.add(document);

        //Updating Filter
        filter.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(document);
                target.add(periodPanel);
            }
        });

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

        form.add(new Button("submit") {
            @Override
            public void onSubmit() {
                //version
                declaration.updateVersion();

                if (declaration.getHead().getCDocVer() == null) {
                    error(getStringFormat("error_not_version", declaration.getShortName()));
                    log.error("Версия не найдена", new Event(EventCategory.CREATE, declaration));

                    return;
                }

                //check period
                if (!declarationService.checkPeriod(declaration)) {
                    error(getStringFormat("error_check_period", declaration.getShortName()));
                    log.error("Период не найден", new Event(EventCategory.CREATE, declaration));

                    return;
                }

                //Person profile
                PersonProfile pp = declaration.getPersonProfile();

                if (pp != null) {
                    declaration.setPersonProfileId(declaration.getPersonProfile().getId());
                    declaration.getHead().setTin(pp.getTin() != null ? pp.getTin() : 0);
                    declaration.setSessionId(pp.getSessionId());
                } else {
                    declaration.setSessionId(getSessionId());
                }

                setResponsePage(new DeclarationEditPage(declaration));
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
