package org.complitex.flexbuh.admin.user.web;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
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
public class JuridicalPersonProfileCreate extends FormTemplatePage {
    @EJB
    private TaxInspectionBean taxInspectionBean;

	@EJB
	private PersonTypeBean personTypeBean;

	@EJB
	private PersonProfileBean personProfileBean;

	@EJB
	private SessionBean sessionBean;

	private PersonProfile personProfile = new PersonProfile();

    public JuridicalPersonProfileCreate() {
        add(new Label("title", getString("title")));
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
		final Model<TaxInspection> taxInspectionModel = new Model<TaxInspection>();
        final DropDownChoice<TaxInspection> taxInspection = new DropDownChoice<TaxInspection>("taxInspection", taxInspectionModel,
                taxInspectionBean.getTaxInspections(),
				new IChoiceRenderer<TaxInspection>() {

                    @Override
                    public Object getDisplayValue(TaxInspection object) {
                        return object.getName(getLocale());
                    }

                    @Override
                    public String getIdValue(TaxInspection object, int index) {
                        return Long.toString(object.getId());
                    }
                });
        taxInspection.setRequired(true);
        form.add(taxInspection);

        form.add(new Button("submit"){
            @Override
            public void onSubmit() {

				PersonType personType = personTypeBean.findByCode("1");

				personProfile.setCodeTaxInspection(taxInspectionModel.getObject().getCode());
				personProfile.setPersonType(personType);

                personProfile.setSessionId(getSessionId(true));
                personProfileBean.save(personProfile);

                info(getString("profile_saved"));
            }
		});

        form.add(new Button("cancel"));
    }
}
