package org.complitex.flexbuh.document.web;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.convert.IConverter;
import org.complitex.flexbuh.document.entity.PersonProfile;
import org.complitex.flexbuh.document.entity.PersonType;
import org.complitex.flexbuh.document.service.PersonProfileBean;
import org.complitex.flexbuh.entity.dictionary.TaxInspection;
import org.complitex.flexbuh.service.dictionary.TaxInspectionBean;
import org.complitex.flexbuh.service.user.SessionBean;
import org.complitex.flexbuh.template.FormTemplatePage;
import org.odlabs.wiquery.ui.datepicker.DatePicker;

import javax.ejb.EJB;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

            if (personProfile != null && !personProfile.getSessionId().equals(getSessionId(false))){
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

        Form form = new Form("form");
        add(form);

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
        TaxInspection taxInspectionObject = taxInspectionBean.getTaxInspection(personProfile.getTaxInspectionId());

        final DropDownChoice<String> districtName = new DropDownChoice<>("districtName",
                new Model<>(taxInspectionObject != null ? taxInspectionObject.getNameRajUk() : null),
                taxInspectionBean.getTaxInspectionDistrictNames());
        form.add(districtName);
        
        final DropDownChoice<TaxInspection> taxInspection = new DropDownChoice<>("taxInspection",
                new Model<>(taxInspectionObject),
                new LoadableDetachableModel<List<TaxInspection>>() {
                    @Override
                    protected List<TaxInspection> load() {
                        return taxInspectionBean.getTaxInspectionsByDistrictName(districtName.getModelObject());
                    }
                },
                new IChoiceRenderer<TaxInspection>() {

                    @Override
                    public Object getDisplayValue(TaxInspection object) {
                        return object.getCSti() + " " + object.getName(getLocale());
                    }

                    @Override
                    public String getIdValue(TaxInspection object, int index) {
                        return Integer.toString(object.getCSti());
                    }
                });
        taxInspection.setNullValid(true);
        taxInspection.setOutputMarkupId(true);
        form.add(taxInspection);
        
        districtName.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(taxInspection);
            }
        });

        // Код ЄДРПОУ ДПІ
        form.add(new TextField<>("c_sti_tin", new PropertyModel<String>(personProfile, "cStiTin")));

        // Код ЕДРПОУ
        form.add(new TextField<>("tin", new PropertyModel<String>(personProfile, "tin")));
        final Label tinLabel = new Label("tin_label", new LoadableDetachableModel<Object>() {
            @Override
            protected Object load() {
                return getString("tin_" + personProfile.getPersonType().getCode());
            }
        });
        tinLabel.setOutputMarkupId(true);
        form.add(tinLabel);

        // Имя
        form.add(new TextField<>("name", new PropertyModel<String>(personProfile, "name")).setRequired(true));
        final Label nameLabel = new Label("name_label", new LoadableDetachableModel<Object>() {
            @Override
            protected Object load() {
                return getString("name_" + personProfile.getPersonType().getCode());
            }
        });
        nameLabel.setOutputMarkupId(true);
        form.add(nameLabel);

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
                target.add(tinLabel);
                target.add(nameLabel);
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
        form.add(new TextField<>("d_fio", new PropertyModel<String>(personProfile, "dFio")));

        // Код ДРФО бухгалтера
        form.add(new TextField<>("b_inn", new PropertyModel<String>(personProfile, "bInn")));

        // ФИО бухгалтера
        form.add(new TextField<>("b_fio", new PropertyModel<String>(personProfile, "bFio")));

        form.add(new Button("submit"){
            @Override
            public void onSubmit() {
                TaxInspection ti = taxInspection.getModelObject();

                if (ti != null) {
                    personProfile.setCSti(ti.getCSti());
                    personProfile.setTaxInspectionId(ti.getId());
                }

                personProfile.setSessionId(getSessionId(true));
                personProfileBean.save(personProfile);

                info(getString("profile_saved"));

                setResponsePage(PersonProfileList.class);
            }
        });

        form.add(new Link("cancel") {

            @Override
            public void onClick() {
                if (personProfile.getId() != null) {
                    personProfile = personProfileBean.getPersonProfile(personProfile.getId());
                } else {
                    personProfile = new PersonProfile();
                }
            }
        });
    }
}
