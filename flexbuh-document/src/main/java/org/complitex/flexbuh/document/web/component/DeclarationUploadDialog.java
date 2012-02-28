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
import org.complitex.flexbuh.document.service.DeclarationService;
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
    private DeclarationService declarationService;

    private Dialog dialog;

    public DeclarationUploadDialog(String id, final IAjaxUpdate update) {
        super(id);

        dialog = new Dialog("dialog");
        dialog.setWidth(500);
        dialog.setHeight(100);

        add(dialog);

        final IModel<List<FileUpload>> fileUploadModel = new ListModel<>();

        Form fileUploadForm = new Form("upload_form");

        fileUploadForm.add(new AjaxButton("upload") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                List<FileUpload> fileUploads = fileUploadModel.getObject();

                List<PersonProfile> personProfiles = personProfileBean.getAllPersonProfiles(getSessionId());

                for (FileUpload fileUpload : fileUploads){
                    String fileName = fileUpload.getClientFileName();

                    PersonProfile personProfile = null;

                    try{
                        Integer tin = Integer.valueOf(fileName.substring(4, 14));

                        for (PersonProfile pp : personProfiles){
                            if (tin.equals(pp.getTin())){
                                personProfile = pp;
                                break;
                            }
                        }
                    }catch (Exception e){
                        //no tin in file name
                    }

                    try {
                        Declaration declaration = declarationService.getDeclaration(fileUpload.getInputStream());

                        declaration.setSessionId(getSessionId());
                        declaration.setPersonProfile(personProfile);

                        declarationService.validate(declaration);
                        declarationService.check(declaration);

                        String info = getStringFormat("info_declaration_upload",
                                fileName,
                                getString("period_type_" + declaration.getHead().getPeriodType()),
                                declaration.getHead().getPeriodMonth(),
                                declaration.getHead().getPeriodYear(),
                                personProfile != null ? personProfile.getProfileName() : getString("empty_profile"));

                        info += ", " + (declaration.isValidated()
                                ? getString("info_validated")
                                : getStringFormat("info_validate_error", declaration.getValidateMessage()));

                        info += ", " + (declaration.isChecked()
                                ? getString("info_checked")
                                : getStringFormat("info_check_error", declaration.getCheckMessage()));

                        getSession().info(info);
                    } catch (NumberFormatException e) {
                        getSession().error(getStringFormat("error_filename", fileName));

                        log.error("Ошибка загрузки файла");
                    } catch (Exception e) {
                        getSession().error(getStringFormat("error_upload", fileName));

                        log.error("Ошибка загрузки файла", e);
                    }
                }

                update.onUpdate(target);

                dialog.close(target);
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
