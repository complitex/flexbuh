package org.complitex.flexbuh.admin.user.web;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.target.resource.ResourceStreamRequestTarget;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.complitex.flexbuh.admin.importexport.service.ImportListener;
import org.complitex.flexbuh.admin.importexport.service.ImportUserProfileXMLService;
import org.complitex.flexbuh.entity.user.PersonProfile;
import org.complitex.flexbuh.entity.user.User;
import org.complitex.flexbuh.service.user.PersonProfileBean;
import org.complitex.flexbuh.service.user.UserBean;
import org.complitex.flexbuh.template.pages.ScrollListPage;
import org.complitex.flexbuh.web.component.datatable.DataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.OutputStream;

/**
 * @author Pavel Sknar
 *         Date: 08.09.11 11:55
 */
public class UserProfileView extends ScrollListPage {

	 private final static Logger log = LoggerFactory.getLogger(UserProfileView.class);

	@EJB
	private UserBean userBean;

	@EJB
	private PersonProfileBean personProfileBean;

	@EJB
	private ImportUserProfileXMLService importUserProfileXMLService;

	private FileUploadField fileUpload;


	public UserProfileView() {
		super();
		init();
	}

	public UserProfileView(PageParameters params) {
		super(params);
		init();
	}

	private void init() {
		add(new Label("title", new ResourceModel("title")));
        add(new FeedbackPanel("messages"));

        final Form filterForm = new Form("filter_form");
        filterForm.setOutputMarkupId(true);
        add(filterForm);

        final Long sessionId = getSessionId(true);

		//Модель
        final DataProvider<PersonProfile> dataProvider = new DataProvider<PersonProfile>() {

			@SuppressWarnings("unchecked")
            @Override
            protected Iterable<? extends PersonProfile> getData(int first, int count) {
                return personProfileBean.getPersonProfiles(sessionId, first, count);
            }

            @Override
            protected int getSize() {
                return personProfileBean.getPersonalProfileCount(sessionId);
            }
        };
        dataProvider.setSort("name", true);

		//Таблица
        DataView<PersonProfile> dataView = new DataView<PersonProfile>("companyProfiles", dataProvider, 10) {

            @Override
            protected void populateItem(Item<PersonProfile> item) {
                PersonProfile profile = item.getModelObject();

                item.add(new Label("name", profile.getName()));

				item.add(new Label("zipCode", profile.getZipCode()));

				item.add(new Label("address", profile.getAddress()));

				item.add(new Label("directorFIO", profile.getDirectorFIO()));

				item.add(new Label("phone", profile.getPhone()));

				item.add(new Label("email", profile.getEmail()));
				/*
                item.add(new BookmarkablePageLinkPanel<PersonProfile>("action_edit", getString("action_edit"),
                        ScrollListBehavior.SCROLL_PREFIX + String.valueOf(profile.getId()),
                        JuridicalPersonProfileEdit.class, new PageParameters("person_profile_id=" + profile.getId())));
                        */
            }
        };
        filterForm.add(dataView);

        //Постраничная навигация
        filterForm.add(new PagingNavigator("paging", dataView));

		// Импортировать профайл из файла пользователя
		Form<?> form = new Form<Void>("import_form") {
			 @Override
			 protected void onSubmit() {

				final FileUpload uploadedFile = fileUpload.getFileUpload();
				if (uploadedFile != null) {


					try {

						final ThreadLocal<Boolean> canceled = new ThreadLocal<Boolean>();
						canceled.set(false);

						importUserProfileXMLService.process(getSessionId(true), new ImportListener() {
							@Override
							public void begin() {
							}

							@Override
							public void completed() {

							}

							@Override
							public void completedWithError() {

							}

							@Override
							public void cancel() {
								canceled.set(true);
							}

							@Override
							public ImportListener getChildImportListener(Object o) {
								return null;
							}
						}, uploadedFile.getClientFileName(), uploadedFile.getInputStream(), null, null);
						if (canceled.get()) {
							log.error("Failed import");
							error(getString("failed_import"));
						} else {
							info(getString("profile_imported"));
						}
					} catch (Exception e) {
						log.error("Failed import", e);
						error(getString("failed_import"));
					}
				 }

			}

		};

		form.setMultiPart(true);

		form.setMaxSize(Bytes.kilobytes(100));

		form.add(fileUpload = new FileUploadField("fileUpload"));

		add(form);

		//Кнопка экспортировать
        Button exportButton = new Button("export") {

            @Override
            public void onSubmit() {

				getRequestCycle().setRequestTarget(new ResourceStreamRequestTarget(new AbstractResourceStreamWriter() {

					@Override
					public void write(OutputStream output) {
						User user = userBean.getUserBySessionId(sessionId);

                        if (user != null){
                            try {
								JAXBContext context = JAXBContext.newInstance(User.class);
								Marshaller marshaller = context.createMarshaller();
								marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
								marshaller.marshal(user, output);
							} catch (Exception e) {
								log.error("Cannot export person profile to xml", e);
							}
                        }
					}

					@Override
					public String getContentType() {
						return "text/xml";
					}
				}, "SETTINGS.XML"));
			}
		};

		filterForm.add(exportButton);
	}
}
