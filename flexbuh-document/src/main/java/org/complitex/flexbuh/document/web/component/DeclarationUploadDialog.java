package org.complitex.flexbuh.document.web.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;
import org.complitex.flexbuh.common.entity.PersonProfile;
import org.complitex.flexbuh.common.service.PersonProfileBean;
import org.complitex.flexbuh.common.template.TemplatePanel;
import org.complitex.flexbuh.common.web.component.IAjaxUpdate;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.service.DeclarationBean;
import org.odlabs.wiquery.ui.dialog.Dialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 18.01.12 16:06
 */
public class DeclarationUploadDialog extends TemplatePanel {
    private final static Logger log = LoggerFactory.getLogger(DeclarationUploadDialog.class);
    
    @EJB
    private PersonProfileBean personProfileBean;
    
    @EJB
    private DeclarationBean declarationBean;

    private Dialog dialog;

    public DeclarationUploadDialog(String id, final IAjaxUpdate update) {
        super(id);

        dialog = new Dialog("dialog");
        dialog.setTitle(getString("upload_title"));
        dialog.setWidth(500);
        dialog.setHeight(100);

        add(dialog);

        final IModel<List<FileUpload>> fileUploadModel = new ListModel<>();

        Form fileUploadForm = new Form("upload_form");

        fileUploadForm.add(new AjaxButton("upload") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                List<FileUpload> fileUploads = fileUploadModel.getObject();

                try {
                    List<PersonProfile> personProfiles = personProfileBean.getAllPersonProfiles(getSessionId());

                    for (FileUpload fileUpload : fileUploads){
                        String fileName = fileUpload.getClientFileName();

                        if (fileName.length() > 15) {
                            PersonProfile personProfile = null;

                            String tin;

                            try {
                                tin = Long.valueOf(fileName.substring(4, 14)).toString();
                            } catch (NumberFormatException e) {
                                getSession().error(getStringFormat("error_filename", fileName));

                                break;
                            }

                            for (PersonProfile pp : personProfiles){
                                if (tin.equals(pp.getTin())){
                                    personProfile = pp;
                                    break;
                                }
                            }

                            Declaration declaration = declarationBean.save(getSessionId(true),
                                    personProfile != null ? personProfile.getId() : null,
                                    fileUpload.getInputStream());

                            getSession().info(getStringFormat("info_declaration_upload",
                                    fileName,
                                    getString("period_type_" + declaration.getHead().getPeriodType()),
                                    declaration.getHead().getPeriodMonth(),
                                    declaration.getHead().getPeriodYear(),
                                    personProfile != null ? personProfile.getProfileName() : getString("empty_profile")));
                        }
                    }

                    update.onUpdate(target);

                    dialog.close(target);
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

        dialog.add(fileUploadForm);

        fileUploadForm.add(new FileUploadField("upload_field", fileUploadModel));
    }

    public void open(AjaxRequestTarget target){
        dialog.open(target);
    }
}
