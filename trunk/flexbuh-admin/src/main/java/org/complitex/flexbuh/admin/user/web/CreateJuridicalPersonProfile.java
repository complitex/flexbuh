package org.complitex.flexbuh.admin.user.web;

import org.apache.commons.lang.Validate;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebResponse;
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

    public CreateJuridicalPersonProfile() {
        add(new Label("title", getString("title")));
		add(new FeedbackPanel("messages"));

        Form form = new Form("form");
        add(form);

		// Имя
		PropertyModel<String> nameModel = new PropertyModel<String>(this.personProfile, "name");
		form.add(new TextField<String>("name", nameModel).setRequired(true));

		// Код ЕДРПОУ
		PropertyModel<String> tinModel = new PropertyModel<String>(this.personProfile, "codeTIN");
		form.add(new TextField<String>("codeTIN", tinModel).setRequired(true));

		// Код основного виду економічної діяльності (за КВЕД)
		PropertyModel<String> kvedModel = new PropertyModel<String>(this.personProfile, "codeKVED");
		form.add(new TextField<String>("codeKVED", tinModel).setRequired(true));

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

				userBean.createCompanyProfile(session, personProfile);

                info(getString("profile_saved"));
            }
        });

        form.add(new Button("cancel"));
    }
}
