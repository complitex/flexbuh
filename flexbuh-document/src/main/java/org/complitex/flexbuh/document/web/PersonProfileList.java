package org.complitex.flexbuh.document.web;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.complitex.flexbuh.document.entity.PersonProfile;
import org.complitex.flexbuh.document.entity.Settings;
import org.complitex.flexbuh.document.service.ImportUserProfileXMLService;
import org.complitex.flexbuh.document.service.PersonProfileBean;
import org.complitex.flexbuh.service.ImportListener;
import org.complitex.flexbuh.service.user.UserBean;
import org.complitex.flexbuh.template.TemplatePage;
import org.complitex.flexbuh.template.toolbar.SaveButton;
import org.complitex.flexbuh.template.toolbar.ToolbarButton;
import org.complitex.flexbuh.web.component.BookmarkablePageLinkPanel;
import org.complitex.flexbuh.web.component.datatable.DataProvider;
import org.complitex.flexbuh.web.component.paging.PagingNavigator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 08.09.11 11:55
 */
public class PersonProfileList extends TemplatePage {

    private final static Logger log = LoggerFactory.getLogger(PersonProfileList.class);

    @EJB
    private UserBean userBean;

    @EJB
    private PersonProfileBean personProfileBean;

    @EJB
    private ImportUserProfileXMLService importUserProfileXMLService;

    private FileUploadField fileUpload;


    public PersonProfileList() {
        super();
        init();
    }

    public PersonProfileList(PageParameters params) {
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
        dataProvider.setSort("name", SortOrder.ASCENDING);

        //Таблица
        DataView<PersonProfile> dataView = new DataView<PersonProfile>("companyProfiles", dataProvider, 10) {

            @Override
            protected void populateItem(Item<PersonProfile> item) {
                PersonProfile profile = item.getModelObject();

                item.add(new Label("name", profile.getName()));

                item.add(new Label("zipCode", profile.getZipCode()));

                item.add(new Label("address", profile.getAddress()));

                item.add(new Label("directorFIO", profile.getDFio()));

                item.add(new Label("phone", profile.getPhone()));

                item.add(new Label("email", profile.getEmail()));

                PageParameters pageParameters = new PageParameters();
                pageParameters.set("id", profile.getId());
                item.add(new BookmarkablePageLinkPanel<PersonProfile>("action_edit", getString("action_edit"),
                        PersonProfileEdit.class, pageParameters));
            }
        };
        filterForm.add(dataView);

        //Постраничная навигация
        filterForm.add(new PagingNavigator("paging", dataView, "PersonProfileList"));

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
    }


    @Override
    protected List<? extends ToolbarButton> getToolbarButtons(String id) {

        return Arrays.asList(new SaveButton(id, "export", false) {
            @Override
            protected void onClick() {
                getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(
                        new AbstractResourceStreamWriter() {
                            @Override
                            public void write(Response output) {
                                List<PersonProfile> personProfiles = personProfileBean.getAllPersonProfiles(getSessionId(false));
                                
                                if (!personProfiles.isEmpty()) {
                                    try {
                                        OutputStream os = ((HttpServletResponse)output.getContainerResponse()).getOutputStream();

                                        JAXBContext context = JAXBContext.newInstance(Settings.class);
                                        Marshaller marshaller = context.createMarshaller();
                                        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

                                        marshaller.marshal(new Settings(personProfiles), os);
                                    } catch (Exception e) {
                                        log.error("Cannot export person profile to xml", e);
                                    }
                                }
                            }

                            @Override
                            public String getContentType() {
                                return "application/xml";
                            }
                        }, "SETTINGS.XML"));
            }
        });
    }
}
