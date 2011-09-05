package org.complitex.flexbuh.admin.user.web;

import org.apache.commons.lang.Validate;
import org.apache.wicket.IClusterable;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.convert.MaskConverter;
import org.apache.wicket.util.crypt.Base64;
import org.complitex.flexbuh.entity.dictionary.TaxInspection;
import org.complitex.flexbuh.entity.user.PersonProfile;
import org.complitex.flexbuh.entity.user.PersonType;
import org.complitex.flexbuh.entity.user.Session;
import org.complitex.flexbuh.service.dictionary.TaxInspectionBean;
import org.complitex.flexbuh.service.user.PersonProfileBean;
import org.complitex.flexbuh.service.user.PersonTypeBean;
import org.complitex.flexbuh.service.user.SessionBean;
import org.complitex.flexbuh.service.user.UserBean;
import org.complitex.flexbuh.template.FormTemplatePage;

import javax.ejb.EJB;
import javax.servlet.http.Cookie;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/**
 * @author Pavel Sknar
 *         Date: 02.09.11 16:11
 */
public class CreateJuridicalPersonProfile extends FormTemplatePage {
    @EJB
    private TaxInspectionBean taxInspectionBean;

	@EJB
	private PersonProfileBean personProfileBean;

	@EJB
	private PersonTypeBean personTypeBean;

	@EJB
	private UserBean userBean;

	@EJB
	private SessionBean sessionBean;

	private PersonProfile personProfile = new PersonProfile();

	private PersonProfilePhone phone = new PersonProfilePhone("");
	private PersonProfilePhone fax = new PersonProfilePhone("");


    public CreateJuridicalPersonProfile() {
        add(new Label("title", getString("title")));
		add(new FeedbackPanel("messages"));

        Form form = new Form("form");
        add(form);

		// Имя
		form.add(new TextField<String>("name", new PropertyModel<String>(this.personProfile, "name")).setRequired(true));

		// Код ЕДРПОУ
		form.add(new TextField<String>("codeTIN", new PropertyModel<String>(this.personProfile, "codeTIN")).setRequired(true));

		// Код основного виду економічної діяльності (за КВЕД)
		form.add(new TextField<String>("codeKVED", new PropertyModel<String>(this.personProfile, "codeKVED")).setRequired(true));

		// Поштовий індекс
		form.add(new TextField<String>("zipCode", new PropertyModel<String>(this.personProfile, "zipCode")).setRequired(true));

		// Адрес
		form.add(new TextField<String>("address", new PropertyModel<String>(this.personProfile, "address")).setRequired(true));

		// Телефон
		form.add(new PhoneTextField("phone", new PropertyModel<PersonProfilePhone>(this, "phone")).setRequired(true));

		// Факс
		form.add(new PhoneTextField("fax", new PropertyModel<PersonProfilePhone>(this, "fax")));

		// E-mail
		form.add(new TextField<String>("email", new PropertyModel<String>(this.personProfile, "email")));

		// ФИО директора предприятия
		form.add(new TextField<String>("directorFIO", new PropertyModel<String>(this.personProfile, "directorFIO")).setRequired(true));

		// ФИО бухгалтера
		form.add(new TextField<String>("accountantFIO", new PropertyModel<String>(this.personProfile, "accountantFIO")).setRequired(true));

		// Код ДРФО директора
		form.add(new TextField<String>("directorINN", new PropertyModel<String>(this.personProfile, "directorINN")).setRequired(true));

		// Код ДРФО бухгалтера
		form.add(new TextField<String>("accountantINN", new PropertyModel<String>(this.personProfile, "accountantINN")).setRequired(true));

		// Індивідуальний податковий номер
		form.add(new TextField<String>("ipn", new PropertyModel<String>(this.personProfile, "ipn")).setRequired(true));

		// Код ДРФО бухгалтера
		form.add(new TextField<String>("numSvdPDV", new PropertyModel<String>(this.personProfile, "numSvdPDV")).setRequired(true));

        // Налоговая
		final Model<TaxInspection> taxInspectionModel = new Model<TaxInspection>();
        final DropDownChoice<TaxInspection> taxInspection = new DropDownChoice<TaxInspection>("taxInspection", taxInspectionModel,
                taxInspectionBean.readAll(),
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

				Session session;
				Cookie cookie = ((WebRequest)getRequestCycle().getRequest()).getCookie(UserConstants.USER_COOKIE_NAME);
				if (cookie == null) {
					MessageDigest digest = null;
					try {
						digest = MessageDigest.getInstance("MD5");
					} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
						return;
					}
					digest.update(personProfile.toString().getBytes());

					session = new Session();
					session.setCookie(new String(Base64.encodeBase64(digest.digest())));

					sessionBean.create(session);

					((WebResponse) RequestCycle.get().getResponse()).addCookie(new Cookie(UserConstants.USER_COOKIE_NAME, session.getCookie()));

					//String s = personProfile.get
				} else {
					session = sessionBean.getSessionByCookie(cookie.getValue());
					Validate.isTrue(session != null, "Can not find session");
				}

				personProfile.setPhone(phone.getNumber());
				personProfile.setFax(fax.getNumber());
				userBean.createCompanyProfile(session, personProfile);

                info(getString("profile_saved"));
            }
        });

        form.add(new Button("cancel"));
    }

	private class PhoneTextField extends TextField<PersonProfilePhone> {

		public PhoneTextField(String id) {
			super(id);
		}

		public PhoneTextField(String id, Class<PersonProfilePhone> type) {
			super(id, type);
		}

		public PhoneTextField(String id, IModel<PersonProfilePhone> stringIModel) {
			super(id, stringIModel);
		}

		public PhoneTextField(String id, IModel<PersonProfilePhone> stringIModel, Class<PersonProfilePhone> type) {
			super(id, stringIModel, type);
		}

		@Override
		public IConverter getConverter(Class<?> type) {
			if (PersonProfilePhone.class.isAssignableFrom(type)) {
				// telephone number mask
				return new MaskConverter("###-###-####", PersonProfilePhone.class) {
					@Override
					public Object convertToObject(String value, Locale locale) {
						return new PersonProfilePhone((String)super.convertToObject(value, locale));
					}
				};
			}
			else {
				return super.getConverter(type);
			}
		}
	}

	private class PersonProfilePhone implements IClusterable {
		private String number;

		private PersonProfilePhone(String number) {
			this.number = number;
		}

		public String getNumber() {
			return number;
		}

		public void setNumber(String number) {
			this.number = number;
		}

		public String toString() {
			return number;
		}
	}
}
