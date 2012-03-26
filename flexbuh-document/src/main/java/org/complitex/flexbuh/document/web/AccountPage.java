package org.complitex.flexbuh.document.web;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.apache.wicket.util.time.Time;
import org.complitex.flexbuh.common.template.FormTemplatePage;
import org.complitex.flexbuh.common.util.DateUtil;
import org.complitex.flexbuh.document.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.io.ByteArrayOutputStream;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 16.03.12 17:20
 */
public class AccountPage extends FormTemplatePage{
    private final static Logger log = LoggerFactory.getLogger(AccountPage.class);

    @EJB
    private AccountService accountService;

    public AccountPage() {
        add(new Label("title", getString("title")));
        add(new FeedbackPanel("messages"));

        Form upload = new Form<>("form_upload");
        add(upload);

        final FileUploadField fileUploadField = new FileUploadField("upload_field", new ListModel<FileUpload>());
        upload.add(fileUploadField);

        upload.add(new Button("upload") {
            @Override
            public void onSubmit() {
                try {
                    accountService.readAccountZip(getSessionId(), fileUploadField.getFileUpload().getInputStream());
                    getSession().info(getString("info_uploaded"));
                } catch (Exception e) {
                    log.error("Ошибка чтения архива", e);
                    error(getStringFormat("error_upload", e.getCause().getMessage()));
                }
            }

//            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                onSubmit();
                setResponsePage(AccountPage.class);
            }

//            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                //hello glassfish 3.1.2
            }
        });

        Form download = new Form<>("form_download");
        add(download);

        download.add(new Button("download") {
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

