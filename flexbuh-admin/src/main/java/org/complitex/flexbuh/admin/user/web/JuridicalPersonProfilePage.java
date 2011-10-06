package org.complitex.flexbuh.admin.user.web;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.flexbuh.entity.dictionary.TaxInspection;
import org.complitex.flexbuh.entity.user.PersonProfile;
import org.complitex.flexbuh.entity.user.PersonType;
import org.complitex.flexbuh.service.dictionary.TaxInspectionBean;
import org.complitex.flexbuh.service.user.PersonProfileBean;
import org.complitex.flexbuh.service.user.PersonTypeBean;
import org.complitex.flexbuh.service.user.SessionBean;
import org.complitex.flexbuh.template.FormTemplatePage;

import javax.ejb.EJB;

/**
 * @author Pavel Sknar
 *         Date: 02.09.11 16:11
 */
public class JuridicalPersonProfilePage extends FormTemplatePage {
    @EJB
    private TaxInspectionBean taxInspectionBean;

	@EJB
	private PersonTypeBean personTypeBean;

	@EJB
	private PersonProfileBean personProfileBean;

	@EJB
	private SessionBean sessionBean;

	private PersonProfile personProfile;

	public JuridicalPersonProfilePage() {
		personProfile = new PersonProfile();
		init();
	}

	public JuridicalPersonProfilePage(PersonProfile personProfile) {
		this.personProfile = personProfile;
		init();
	}

	public JuridicalPersonProfilePage(PageParameters pageParameters) {
		if (!pageParameters.isEmpty()) {
			Long id = pageParameters.get("id").toLongObject();
			personProfile = personProfileBean.getPersonProfile(id);

			if (personProfile != null && !personProfile.getSessionId().equals(getSessionId(false))){
				// person profile not found
				error(getString("error_person_profile_failed_session"));
				setResponsePage(UserProfileView.class);
			} if (personProfile != null) {
				init();
			} else {
				// person profile not found
				error(getString("error_person_profile_not_found"));
				setResponsePage(UserProfileView.class);
			}
		} else {
			personProfile = new PersonProfile();
			init();
		}
	}

	public void init() {
		final Label titleCreate = new Label("title_create", getString("title_create"));
		add(titleCreate);
        final Label titleCreate2 = new Label("title_create2", getString("title_create"));
		add(titleCreate2);
		final Label titleEdit = new Label("title_edit", getString("title_edit"));
		add(titleEdit);
		final Label titleEdit2 = new Label("title_edit2", getString("title_edit"));
		add(titleEdit2);

		if (personProfile.getId() == null) {
        	titleEdit.setVisible(false);
        	titleEdit2.setVisible(false);
		} else {
			titleCreate.setVisible(false);
			titleCreate2.setVisible(false);
		}
		add(new FeedbackPanel("messages"));

        Form form = new Form("form");
        add(form);

		// Имя
		form.add(new TextField<>("name", new PropertyModel<String>(personProfile, "name")).setRequired(true));

		// Код ЕДРПОУ
		form.add(new TextField<>("codeTIN", new PropertyModel<String>(personProfile, "codeTIN")).setRequired(true));

		// Код основного виду економічної діяльності (за КВЕД)
		form.add(new TextField<>("codeKVED", new PropertyModel<String>(personProfile, "codeKVED")).setRequired(true));

		// Поштовий індекс
		form.add(new TextField<>("zipCode", new PropertyModel<String>(personProfile, "zipCode")).setRequired(true));

		// Адрес
		form.add(new TextField<>("address", new PropertyModel<String>(personProfile, "address")).setRequired(true));

		// Телефон
		form.add(new TextField<>("phone", new PropertyModel<String>(personProfile, "phone")).setRequired(true));

		// Факс
		form.add(new TextField<>("fax", new PropertyModel<String>(personProfile, "fax")));

		// E-mail
		form.add(new TextField<>("email", new PropertyModel<String>(personProfile, "email")));

		// ФИО директора предприятия
		form.add(new TextField<>("directorFIO", new PropertyModel<String>(personProfile, "directorFIO")).setRequired(true));

		// ФИО бухгалтера
		form.add(new TextField<>("accountantFIO", new PropertyModel<String>(personProfile, "accountantFIO")).setRequired(true));

		// Код ДРФО директора
		form.add(new TextField<>("directorINN", new PropertyModel<String>(personProfile, "directorINN")).setRequired(true));

		// Код ДРФО бухгалтера
		form.add(new TextField<>("accountantINN", new PropertyModel<String>(personProfile, "accountantINN")).setRequired(true));

		// Індивідуальний податковий номер
		form.add(new TextField<>("ipn", new PropertyModel<String>(personProfile, "ipn")).setRequired(true));

		// Код ДРФО бухгалтера
		form.add(new TextField<>("numSvdPDV", new PropertyModel<String>(personProfile, "numSvdPDV")).setRequired(true));

        // Налоговая
		final Model<TaxInspection> taxInspectionModel = personProfile.getCodeTaxInspection() != null?
				new Model<TaxInspection>(taxInspectionBean.getTaxInspectionByCode(personProfile.getCodeTaxInspection())):
				new Model<TaxInspection>();
        final DropDownChoice<TaxInspection> taxInspection = new DropDownChoice<>("taxInspection", taxInspectionModel,
                taxInspectionBean.getTaxInspectionsUniqueCodeWithName(),
				new IChoiceRenderer<TaxInspection>() {

                    @Override
                    public Object getDisplayValue(TaxInspection object) {
						System.out.println("Locale: " + getLocale() + ", TaxInspection: " + object.toString());
                        return object.getName(getLocale());
                    }

                    @Override
                    public String getIdValue(TaxInspection object, int index) {
                        return Integer.toString(object.getCode());
                    }
                });
        taxInspection.setRequired(true);
        form.add(taxInspection);

        form.add(new Button("submit"){
            @Override
            public void onSubmit() {

				PersonType personType = personTypeBean.findByCode("1");

				personProfile.setCodeTaxInspection(taxInspectionModel.getObject().getCode());
				if (personProfile.getPersonType() == null) {
					personProfile.setPersonType(personType);
				}

				if (personProfile.getId() == null) {
					personProfile.setSessionId(getSessionId(true));
					personProfileBean.save(personProfile);
				} else {
					personProfileBean.update(personProfile);
				}

				titleEdit.setVisible(true);
				titleEdit2.setVisible(true);
				titleCreate.setVisible(false);
				titleCreate2.setVisible(false);

                info(getString("profile_saved"));
				/*
				PageParameters pageParameters = new PageParameters();
                pageParameters.set("id", personProfile.getId());
				setResponsePage(JuridicalPersonProfilePage.class, pageParameters);
				*/
            }
		});

        form.add(new Button("cancel"));
    }
}
