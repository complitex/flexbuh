package org.complitex.flexbuh.document.web;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.apache.wicket.util.time.Time;
import org.complitex.flexbuh.common.entity.user.Share;
import org.complitex.flexbuh.common.entity.user.User;
import org.complitex.flexbuh.common.service.user.ShareBean;
import org.complitex.flexbuh.common.service.user.UserBean;
import org.complitex.flexbuh.common.template.FormTemplatePage;
import org.complitex.flexbuh.common.util.DateUtil;
import org.complitex.flexbuh.document.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 16.03.12 17:20
 */
public class AccountPage extends FormTemplatePage{
    private final static Logger log = LoggerFactory.getLogger(AccountPage.class);

    @EJB
    private AccountService accountService;

    @EJB
    private UserBean userBean;

    @EJB
    private ShareBean shareBean;

    public AccountPage() {
        add(new Label("title", getString("title")));

        final FeedbackPanel messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        add(messages);

        //Share
        WebMarkupContainer shareContainer = new WebMarkupContainer("share_container");
        shareContainer.setVisible(!userBean.isAnonymous());
        add(shareContainer);

        final Form shareForm = new Form<>("form_share");
        shareForm.setOutputMarkupId(true);
        shareContainer.add(shareForm);

        shareForm.add(new ListView<User>("shared_users", new LoadableDetachableModel<List<? extends User>>() {
            @Override
            protected List<? extends User> load() {
                return userBean.getSharedUsers(getSessionId());
            }
        }) {
            @Override
            protected void populateItem(ListItem<User> item) {
                final User user = item.getModelObject();

                item.add(new Label("user_name", user.getFullName()));
                item.add(new Label("user_email", user.getEmail()));

                item.add(new AjaxLink("share_delete") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        shareBean.delete(new Share(getSessionId(), user.getSessionId()));

                        target.add(shareForm);
                        target.add(messages);
                    }
                });
            }
        });

        final IModel<String> shareSearchModel = Model.of("");
        shareForm.add(new TextField<>("share_search", shareSearchModel));
        shareForm.add(new AjaxButton("add_share") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                User user = userBean.findUserByLoginOrEmail(shareSearchModel.getObject());

                if (user != null && user.getSessionId() != null && !getSessionId().equals(user.getSessionId())) {
                    Share share = new Share(getSessionId(), user.getSessionId());

                    if (!shareBean.isExist(share)){
                        shareBean.save(share);

                        shareSearchModel.setObject("");

                        info(getStringFormat("info_user_added", user.getFullName()));

                        target.add(shareForm);
                    }else {
                        error(getStringFormat("error_user_already_added", shareSearchModel.getObject()));
                    }
                } else {
                    error(getStringFormat("error_user_not_found", shareSearchModel.getObject()));
                }

                target.add(messages);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                //no error
            }
        });

        //Upload
        Form uploadForm = new Form<>("form_upload");
        add(uploadForm);

        final FileUploadField fileUploadField = new FileUploadField("upload_field", new ListModel<FileUpload>());
        fileUploadField.setRequired(true);
        uploadForm.add(fileUploadField);

        uploadForm.add(new AjaxButton("upload") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    accountService.readAccountZip(getSessionId(), fileUploadField.getFileUpload().getInputStream());
                    getSession().info(getString("info_uploaded"));
                    setResponsePage(AccountPage.class);
                } catch (Exception e) {
                    log.error("Ошибка чтения архива", e);

                    error(getStringFormat("error_upload", e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                //hello glassfish 3.1.2
            }
        });

        //Download
        Form downloadForm = new Form<>("form_download");
        add(downloadForm);

        downloadForm.add(new Button("download") {
            @Override
            public void onSubmit() {
                final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                accountService.writeAccountZip(getSessionId(), outputStream);

                getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(
                        new AbstractResourceStreamWriter() {

                            @Override
                            public void write(Response output) {
                                output.write(outputStream.toByteArray());
                            }

                            @Override
                            public Bytes length() {
                                return Bytes.bytes(outputStream.size());
                            }

                            @Override
                            public String getContentType() {
                                return "application/zip";
                            }

                            @Override
                            public Time lastModifiedTime() {
                                return Time.now();
                            }
                        }, "flexbuh_" + getSessionId() + "_" + DateUtil.getString(DateUtil.getCurrentDate()).replace(".", "") + ".zip"));

            }
        });
    }
}

