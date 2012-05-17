package org.complitex.flexbuh.document.web;

import com.google.common.io.ByteStreams;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.complitex.flexbuh.common.entity.PersonProfile;
import org.complitex.flexbuh.common.logging.Event;
import org.complitex.flexbuh.common.logging.EventCategory;
import org.complitex.flexbuh.common.service.AbstractImportListener;
import org.complitex.flexbuh.common.service.ConfigBean;
import org.complitex.flexbuh.common.service.PersonProfileBean;
import org.complitex.flexbuh.common.service.user.UserBean;
import org.complitex.flexbuh.common.template.TemplatePage;
import org.complitex.flexbuh.common.template.toolbar.*;
import org.complitex.flexbuh.common.util.StringUtil;
import org.complitex.flexbuh.common.util.XmlUtil;
import org.complitex.flexbuh.common.web.component.BookmarkablePageLinkPanel;
import org.complitex.flexbuh.common.web.component.datatable.DataProvider;
import org.complitex.flexbuh.common.web.component.paging.PagingNavigator;
import org.complitex.flexbuh.document.service.ImportPersonProfileXMLService;
import org.odlabs.wiquery.ui.dialog.Dialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
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
    private ImportPersonProfileXMLService importUserProfileXMLService;

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

        //Модель
        final DataProvider<PersonProfile> dataProvider = new DataProvider<PersonProfile>() {
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

                String userName = !profile.getSessionId().equals(getSessionId()) ? "(" + profile.getUserName()  + ") " : "";

                item.add(new Label("name", userName + StringUtil.emptyOnNull(profile.getName())));

                item.add(new Label("tin", StringUtil.getString(profile.getTin())));

                item.add(new Label("tax_inspection_name", StringUtil.getString(profile.getCSti()) + " " +
                        StringUtil.emptyOnNull(profile.getTaxInspectionName())));
            }
        };
        filterForm.add(dataView);

        //Постраничная навигация
        filterForm.add(new PagingNavigator("paging", dataView, "PersonProfileList", filterForm));

        //Загрузка файлов
        uploadDialog = new Dialog("upload_dialog");
        uploadDialog.setTitle(getString("upload_title"));
        uploadDialog.setWidth(500);
        uploadDialog.setHeight(100);

        add(uploadDialog);

        Form fileUploadForm = new Form("upload_form");

        final FileUploadField uploadField = new FileUploadField("upload_field");
        fileUploadForm.add(uploadField);

        fileUploadForm.add(new AjaxButton("upload") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                FileUpload fileUpload = uploadField.getFileUpload();

                if (fileUpload != null) {
                    final ThreadLocal<Boolean> error = new ThreadLocal<>();
                    error.set(false);

                    final AtomicInteger count = new AtomicInteger(0);

                    try {
                        importUserProfileXMLService.process(getSessionId(), new AbstractImportListener<PersonProfile>() {
                            @Override
                            public void processed(PersonProfile object) {
                                count.incrementAndGet();
                            }

                            @Override
                            public void error(String message) {
                                error.set(true);
                            }
                        }, fileUpload.getClientFileName(), fileUpload.getInputStream());
                    } catch (IOException e) {
                        log.error("Ошибка загрузки профиля - ошибка чтения потока", e);
                        getSession().error(getString("error_failed_import"));
                    }

                    if (!error.get()) {
                        getSession().info(getStringFormat("info_profile_imported", count.get()));
                    } else {
                        getSession().error(getString("error_failed_import"));
                    }
                }

                uploadDialog.close(target);

                setResponsePage(PersonProfileList.class);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                //wtf
            }
        });

        uploadDialog.add(fileUploadForm);
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
                final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                try {
                    List<PersonProfile> personProfiles = personProfileBean.getAllPersonProfiles(getSessionId());

                    //Log
                    final List<PersonProfile> logPersonProfiles = personProfileBean.getAllPersonProfiles(getSessionId());

                    Long selectedPersonProfileId = getPreferenceLong(PersonProfile.SELECTED_PERSON_PROFILE_ID);

                    boolean selectedSet = false;

                    for (int i = 0, personProfilesSize = personProfiles.size(); i < personProfilesSize; i++) {
                        PersonProfile personProfile = personProfiles.get(i);
                        personProfile.mergePhysicalNames();

                        //num
                        personProfile.setNum(i + 1);

                        //selected
                        if (personProfile.getId().equals(selectedPersonProfileId)) {
                            personProfile.setSelected(true);
                            selectedSet = true;
                        } else {
                            personProfile.setSelected(false);
                        }

                        //name
                        if (personProfile.getName() == null || personProfile.getName().isEmpty()){
                            personProfile.setName(personProfile.getProfileName());
                        }

                        //empty for null
                        Field[] fields = PersonProfile.class.getDeclaredFields();

                        for (Field field : fields){
                            field.setAccessible(true);

                            if (field.get(personProfile) == null) {
                                if (Integer.class.equals(field.getType())){
                                    field.set(personProfile, 0);
                                }else if (String.class.equals(field.getType())){
                                    field.set(personProfile, "");
                                }else if (Date.class.equals(field.getType())){
                                    field.set(personProfile, new Date(0));
                                }
                            }
                        }

                        //prepare opz format
                        personProfile.setId(null);
                        personProfile.setProfileName(null);
                    }

                    if (!selectedSet && !personProfiles.isEmpty()){
                        personProfiles.get(0).setSelected(true);
                    }

                    XmlUtil.writeXml(PersonProfile.RS .class, new PersonProfile.RS(personProfiles), outputStream, "windows-1251");

                    //Schedule request handler
                    getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(
                            new AbstractResourceStreamWriter() {
                                @Override
                                public void write(final Response output) {
                                    try {
                                        ByteStreams.copy(new ByteArrayInputStream(outputStream.toByteArray()),
                                                ((HttpServletResponse) output.getContainerResponse()).getOutputStream());

                                        for (PersonProfile pp : logPersonProfiles) {
                                            log.info("Профиль выгружен", new Event(EventCategory.EXPORT, pp));
                                        }
                                    } catch (IOException e) {
                                        log.error("Ошибка экспорта профиля", e);
                                    }
                                }

                                @Override
                                public String getContentType() {
                                    return "application/xml";
                                }
                            }, "SETTINGS.XML"));
                } catch (Exception e) {
                    getSession().error(getString("error_export"));
                    log.error("Ошибка экспорта профиля", e);
                }
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
}
