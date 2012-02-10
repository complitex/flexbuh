package org.complitex.flexbuh.document.web;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.complitex.flexbuh.common.entity.ApplicationConfig;
import org.complitex.flexbuh.common.entity.PersonProfile;
import org.complitex.flexbuh.common.entity.PersonType;
import org.complitex.flexbuh.common.logging.EventCategory;
import org.complitex.flexbuh.common.service.AbstractImportListener;
import org.complitex.flexbuh.common.service.ConfigBean;
import org.complitex.flexbuh.common.service.ImportListener;
import org.complitex.flexbuh.common.service.PersonProfileBean;
import org.complitex.flexbuh.common.service.user.UserBean;
import org.complitex.flexbuh.common.template.TemplatePage;
import org.complitex.flexbuh.common.template.toolbar.*;
import org.complitex.flexbuh.common.web.component.BookmarkablePageLinkPanel;
import org.complitex.flexbuh.common.web.component.datatable.DataProvider;
import org.complitex.flexbuh.common.web.component.paging.PagingNavigator;
import org.complitex.flexbuh.document.entity.Settings;
import org.complitex.flexbuh.document.service.ImportUserProfileXMLService;
import org.odlabs.wiquery.ui.dialog.Dialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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

    @EJB
    private ConfigBean configBean;

    private Map<Long, IModel<Boolean>> selectMap = new HashMap<>();

    private Dialog uploadDialog;

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

        final Long sessionId = getSessionId();

        //Текущий профиль
        add(new Label("selected_profile", new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                PersonProfile selectedProfile = personProfileBean.getSelectedPersonProfile(sessionId);

                return selectedProfile != null ? selectedProfile.getProfileName() : getString("no_selected_profile");
            }
        }));

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
                final PersonProfile profile = item.getModelObject();

                IModel<Boolean> selectModel = new Model<>(false);

                selectMap.put(profile.getId(), selectModel);

                item.add(new CheckBox("select", selectModel)
                        .add(new AjaxFormComponentUpdatingBehavior("onchange") {
                            @Override
                            protected void onUpdate(AjaxRequestTarget target) {
                                //update
                            }
                        }));

                PageParameters pageParameters = new PageParameters();
                pageParameters.set("id", profile.getId());
                item.add(new BookmarkablePageLinkPanel<PersonProfile>("profile_name", profile.getProfileName(),
                        PersonProfileEdit.class, pageParameters));

                item.add(new Label("name", profile.getName()));

                item.add(new Label("tin", profile.getTin() + ""));

                Link select = new Link("action_select") {
                    @Override
                    public void onClick() {
                        if (!profile.isSelected()) {
                            personProfileBean.setSelectedPersonProfile(profile);
                        }else{
                            personProfileBean.deselectAllPersonProfile(sessionId);
                        }
                    }
                };
                item.add(select);

                select.add(new Label("action_select_label", getString(!profile.isSelected() ? "action_select"
                        : "action_deselect")));
            }
        };
        filterForm.add(dataView);

        //Постраничная навигация
        filterForm.add(new PagingNavigator("paging", dataView, "PersonProfileList", filterForm));

        //Загрузка файлов
        uploadDialog = new Dialog("upload_dialog");
        uploadDialog.setTitle(getString("upload_title"));
        uploadDialog.setWidth(270);
        uploadDialog.setHeight(130);

        add(uploadDialog);

        final IModel<List<FileUpload>> fileUploadModel = new ListModel<>();
        final IModel<String> fileLocaleModel = new Model<>(getIsoCodeSystemLocale());

        Form fileUploadForm = new Form("upload_form");

        fileUploadForm.add(new AjaxButton("upload") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                List<FileUpload> fileUploads = fileUploadModel.getObject();

                try {
                    for (FileUpload fileUpload : fileUploads){
                        if (fileUpload != null) {
                            try {

                                final ThreadLocal<Boolean> canceled = new ThreadLocal<Boolean>();
                                canceled.set(false);
                                
                                final AtomicInteger count = new AtomicInteger(0);

                                importUserProfileXMLService.process(getSessionId(), new AbstractImportListener() {
                                    @Override
                                    public void cancel() {
                                        canceled.set(true);
                                    }

                                    @Override
                                    public ImportListener getChildImportListener(Object o) {
                                        return new AbstractImportListener() {
                                            @Override
                                            public void completed() {
                                                count.incrementAndGet();
                                            }
                                        };
                                    }
                                }, fileUpload.getClientFileName(), fileUpload.getInputStream(), new Locale(fileLocaleModel.getObject()), null, null);

                                if (canceled.get()) {
                                    log.error("Failed import");
                                    getSession().error(getString("error_failed_import"));
                                } else {
                                    getSession().info(getStringFormat("info_profile_imported", count.get()));
                                }
                            } catch (Exception e) {
                                log.error("Failed import", e);
                                getSession().error(getString("error_failed_import"));
                            }
                        }
                    }

                    uploadDialog.close(target);

                    setResponsePage(PersonProfileList.class);

                    info("Документы успешно загружены");
                } catch (Exception e) {
                    log.error("Ошибка загрузки файла", e);
                    error("Ошибка загрузки файла");
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                //wtf
            }
        });

        uploadDialog.add(fileUploadForm);

        fileUploadForm.add(new FileUploadField("upload_field", fileUploadModel));
        fileUploadForm.add(new DropDownChoice<String>("file_locale", fileLocaleModel, ApplicationConfig.SYSTEM_LOCALE.getAllowedValues()) {
            @Override
            protected boolean localizeDisplayValues() {
                return true;
            }
        });
    }

    @Override
    protected List<? extends ToolbarButton> getToolbarButtons(String id) {
        List<ToolbarButton> list = new ArrayList<>();

        list.add(new UploadButton(id, true){
            @Override
            protected void onClick(AjaxRequestTarget target) {
                uploadDialog.open(target);
            }
        });

        list.add(new SaveButton(id, "export", false) {
            @Override
            protected void onClick() {
                getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(
                        new AbstractResourceStreamWriter() {
                            @Override
                            public void write(Response output) {
                                List<PersonProfile> personProfiles = personProfileBean.getAllPersonProfiles(getSessionId());

                                for(PersonProfile pp : personProfiles){
                                    if (PersonType.PHYSICAL_PERSON.equals(pp.getPersonType())){
                                        pp.mergePhysicalNames();
                                    }
                                }

                                if (!personProfiles.isEmpty()) {
                                    try {
                                        OutputStream os = ((HttpServletResponse) output.getContainerResponse()).getOutputStream();

                                        JAXBContext context = JAXBContext.newInstance(Settings.class);
                                        Marshaller marshaller = context.createMarshaller();
                                        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

                                        marshaller.marshal(new Settings(personProfiles), os);
                                    } catch (Exception e) {
                                        log.error("Cannot export person profile to xml: {}",
                                                new Object[]{e, EventCategory.EXPORT});
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

        list.add(new AddDocumentButton(id){
            @Override
            protected void onClick() {
                setResponsePage(PersonProfileEdit.class);
            }
        });

        list.add(new DeleteItemButton(id){
            @Override
            protected void onClick() {
                for (Long id : selectMap.keySet()){
                    if (selectMap.get(id).getObject()){
                        try {
                            personProfileBean.delete(id);
                        } catch (Exception e) {
                            error(personProfileBean.getPersonProfile(id).getProfileName() + " - " + getString("error_delete"));
                        }
                    }
                }
            }
        });

        return list;
    }

    private String getIsoCodeSystemLocale() {
        return configBean.getString(ApplicationConfig.SYSTEM_LOCALE, true);
    }
}
