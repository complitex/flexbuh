package org.complitex.flexbuh.document.web;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.convert.IConverter;
import org.complitex.flexbuh.common.entity.PersonProfile;
import org.complitex.flexbuh.common.entity.PersonType;
import org.complitex.flexbuh.common.entity.dictionary.TaxInspection;
import org.complitex.flexbuh.common.service.PersonProfileBean;
import org.complitex.flexbuh.common.service.dictionary.TaxInspectionBean;
import org.complitex.flexbuh.common.service.user.SessionBean;
import org.complitex.flexbuh.common.template.FormTemplatePage;
import org.complitex.flexbuh.common.web.component.*;
import org.odlabs.wiquery.ui.datepicker.DatePicker;

import javax.ejb.EJB;
import java.util.Arrays;
import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 02.09.11 16:11
 */
public class PersonProfileEdit extends FormTemplatePage {

    private final static String PHONE_MASK = "###-###-####";

    @EJB
    private TaxInspectionBean taxInspectionBean;

    @EJB
    private PersonProfileBean personProfileBean;

    @EJB
    private SessionBean sessionBean;

    private PersonProfile personProfile;

    public PersonProfileEdit() {
        personProfile = new PersonProfile();
        init();
    }

    public PersonProfileEdit(PersonProfile personProfile) {
        this.personProfile = personProfile;
        init();
    }

    public PersonProfileEdit(PageParameters pageParameters) {
        if (!pageParameters.isEmpty()) {
            Long id = pageParameters.get("id").toLongObject();
            personProfile = personProfileBean.getPersonProfile(id);

            if (personProfile != null && !personProfile.getSessionId().equals(getSessionId())){
                // person profile not found
                error(getString("error_person_profile_failed_session"));
                setResponsePage(PersonProfileList.class);
            } if (personProfile != null) {
                init();
            } else {
                // person profile not found
                error(getString("error_person_profile_not_found"));
                setResponsePage(PersonProfileList.class);
            }
        } else {
            personProfile = new PersonProfile();
            init();
        }
    }

    public void init() {
        add(new Label("title", getString(personProfile.getId() == null ? "title_create" : "title_edit")));
        add(new Label("header", getString(personProfile.getId() == null ? "title_create" : "title_edit")));

        add(new FeedbackPanel("messages"));

        final Form form = new Form("form");
        form.setOutputMarkupId(true);
        add(form);

        // Название профиля
        form.add(new TextField<>("profile_name", new PropertyModel<String>(personProfile, "profileName")).setRequired(true));

        //Тип
        DropDownChoice personType = new DropDownChoice<>("person_type",
                new PropertyModel<PersonType>(personProfile, "personType"),
                Arrays.asList(PersonType.values()),
                new IChoiceRenderer<PersonType>() {
                    @Override
                    public Object getDisplayValue(PersonType object) {
                        return getString("person_type_" + object.getCode());
                    }

                    @Override
                    public String getIdValue(PersonType object, int index) {
                        return "" + object.getCode();
                    }
                });
        personType.setRequired(true);
        form.add(personType);

        // Налоговая
        final IModel<TaxInspection> taxInspectionModel = new Model<>(taxInspectionBean.getTaxInspection(personProfile.getTaxInspectionId()));

        final TaxInspectionDialog taxInspectionDialog = new TaxInspectionDialog("dialog", taxInspectionModel);
        add(taxInspectionDialog);

        AjaxLink stiLink = new AjaxLink("sti_link") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                taxInspectionDialog.open(target);
            }
        };
        form.add(stiLink);

        final Label stiLabel = new Label("sti_label",
                taxInspectionModel.getObject() != null ?
                        taxInspectionModel.getObject().getCSti() + " " + taxInspectionModel.getObject().getName(getLocale())
                        : getString("sti_label"));
        stiLabel.setOutputMarkupId(true);
        stiLink.add(stiLabel);
        
        taxInspectionDialog.setAjaxUpdate(new IAjaxUpdate() {
            @Override
            public void onUpdate(AjaxRequestTarget target) {
                TaxInspection taxInspection = taxInspectionModel.getObject();

                if (taxInspection != null) {
                    stiLabel.setDefaultModelObject(taxInspection.getCSti() + " " + taxInspection.getName(getLocale()));
                    target.add(stiLabel);
                }
            }
        });

        // Код ЄДРПОУ ДПІ
        form.add(new TextField<>("c_sti_tin", new PropertyModel<Integer>(personProfile, "cStiTin")));

        // Код ЕДРПОУ
        form.add(new TextField<>("tin", new PropertyModel<Integer>(personProfile, "tin")));
        final Label tinLabel = new Label("tin_label", new LoadableDetachableModel<Object>() {
            @Override
            protected Object load() {
                return getString("tin_" + personProfile.getPersonType().getCode());
            }
        });
        tinLabel.setOutputMarkupId(true);
        form.add(tinLabel);

        //Название
        final WebMarkupContainer nameContainer = new WebMarkupContainer("name_container"){
            @Override
            public boolean isVisible() {
                return !PersonType.PHYSICAL_PERSON.equals(personProfile.getPersonType());
            }
        };
        form.add(nameContainer);

        nameContainer.add(new TextField<>("name", new PropertyModel<String>(personProfile, "name")));
        nameContainer.add(new Label("name_label", new LoadableDetachableModel<Object>() {
            @Override
            protected Object load() {
                return getString("name_" + personProfile.getPersonType().getCode());
            }
        }));

        //ФИО
        final WebMarkupContainer physicalNameContainer = new WebMarkupContainer("physical_name_container"){
            @Override
            public boolean isVisible() {
                return PersonType.PHYSICAL_PERSON.equals(personProfile.getPersonType());
            }
        };        
        form.add(physicalNameContainer);

        physicalNameContainer.add(new LastNameAutoCompleteTextField("last_name", new PropertyModel<String>(personProfile, "lastName")));
        physicalNameContainer.add(new FirstNameAutoCompleteTextField("first_name", new PropertyModel<String>(personProfile, "firstName")));
        physicalNameContainer.add(new MiddleNameAutoCompleteTextField("middle_name", new PropertyModel<String>(personProfile, "middleName")));

        // индивидуальный налоговый номер
        form.add(new TextField<>("ipn", new PropertyModel<String>(personProfile, "ipn")));

        // Код ДРФО бухгалтера
        form.add(new TextField<>("num_pdv_svd", new PropertyModel<String>(personProfile, "numPdvSvd")));

        // Код основного вида экономической деятельности (за КВЕД)
        form.add(new TextField<>("kved", new PropertyModel<String>(personProfile, "kved")));

        // КОАТУУ
        form.add(new TextField<>("koatuu", new PropertyModel<String>(personProfile, "koatuu")));

        // Договор об общей (совместной) деятельности
        final WebMarkupContainer contractContainer = new WebMarkupContainer("contract_container");
        form.add(contractContainer);

        contractContainer.add(new DatePicker<>("contract_date", new PropertyModel<Date>(personProfile, "contractDate")));
        contractContainer.add(new TextField<>("contract_number", new PropertyModel<String>(personProfile, "contractNumber")));

        //ajax label update
        personType.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(form);
            }
        });

        // Почтовый индекс
        form.add(new TextField<>("zip_code", new PropertyModel<String>(personProfile, "zipCode")));

        // Адрес
        form.add(new TextField<>("address", new PropertyModel<String>(personProfile, "address")));

        // Телефон
        form.add(new TextField<String>("phone", new PropertyModel<String>(personProfile, "phone")){
            @Override
            public <String> IConverter<String> getConverter(final Class<String> type) {
                return super.getConverter(type);
                // US telephone number mask
//                return new MaskConverter<>(PHONE_MASK);
            }
        });

        // Факс
        form.add(new TextField<String>("fax", new PropertyModel<String>(personProfile, "fax")){
            @Override
            public <String> IConverter<String> getConverter(final Class<String> type) {
                return super.getConverter(type);
                // US telephone number mask
//                return new MaskConverter<>(PHONE_MASK);
            }
        });

        // E-mail
        form.add(new TextField<>("email", new PropertyModel<String>(personProfile, "email")));

        // Код ДРФО директора
        form.add(new TextField<>("d_inn", new PropertyModel<String>(personProfile, "dInn")));

        // ФИО директора предприятия
        form.add(new LastNameAutoCompleteTextField("d_last_name", new PropertyModel<String>(personProfile, "dLastName")));
        form.add(new FirstNameAutoCompleteTextField("d_first_name", new PropertyModel<String>(personProfile, "dFirstName")));
        form.add(new MiddleNameAutoCompleteTextField("d_middle_name", new PropertyModel<String>(personProfile, "dMiddleName")));

        // Код ДРФО бухгалтера
        form.add(new TextField<>("b_inn", new PropertyModel<String>(personProfile, "bInn")));

        // ФИО бухгалтера
        form.add(new LastNameAutoCompleteTextField("b_last_name", new PropertyModel<String>(personProfile, "bLastName")));
        form.add(new FirstNameAutoCompleteTextField("b_first_name", new PropertyModel<String>(personProfile, "bFirstName")));
        form.add(new MiddleNameAutoCompleteTextField("b_middle_name", new PropertyModel<String>(personProfile, "bMiddleName")));

        form.add(new Button("submit"){
            @Override
            public void onSubmit() {
                TaxInspection ti = taxInspectionModel.getObject();

                if (ti != null) {
                    personProfile.setCSti(ti.getCSti());
                    personProfile.setTaxInspectionId(ti.getId());
                }

                personProfile.setSessionId(getSessionId());
                personProfileBean.save(personProfile);

                info(getString("profile_saved"));

                setResponsePage(PersonProfileList.class);
            }
        });

        form.add(new Link("cancel") {

            @Override
            public void onClick() {
                setResponsePage(PersonProfileList.class);
            }
        });
    }
}
