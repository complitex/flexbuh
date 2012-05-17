package org.complitex.flexbuh.document.web.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;
import org.complitex.flexbuh.common.entity.PersonProfile;
import org.complitex.flexbuh.common.entity.dictionary.DocumentVersion;
import org.complitex.flexbuh.common.logging.Event;
import org.complitex.flexbuh.common.logging.EventCategory;
import org.complitex.flexbuh.common.service.PersonProfileBean;
import org.complitex.flexbuh.common.service.dictionary.DocumentVersionBean;
import org.complitex.flexbuh.common.template.TemplatePanel;
import org.complitex.flexbuh.common.util.DateUtil;
import org.complitex.flexbuh.common.web.component.IAjaxUpdate;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.entity.DeclarationFilter;
import org.complitex.flexbuh.document.entity.DeclarationHead;
import org.complitex.flexbuh.document.entity.Period;
import org.complitex.flexbuh.document.service.DeclarationBean;
import org.complitex.flexbuh.document.service.DeclarationService;
import org.odlabs.wiquery.ui.dialog.Dialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.Date;
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

    @EJB
    private DeclarationBean declarationBean;

    @EJB
    private DocumentVersionBean documentVersionBean;

    private Dialog dialog;

    public DeclarationUploadDialog(String id, final DeclarationFilter declarationFilter, final IAjaxUpdate update) {
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

                List<PersonProfile> personProfiles = personProfileBean.getAllSharedPersonProfiles(getSessionId());

                for (FileUpload fileUpload : fileUploads){
                    String fileName = fileUpload.getClientFileName();

                    try {
                        Declaration declaration = declarationService.parse(getSessionId(), personProfiles, fileName,
                                fileUpload.getInputStream());

                        //period version check
                        DeclarationHead head = declaration.getHead();
                        DocumentVersion documentVersion = documentVersionBean.getDocumentVersion(head.getCDoc(),
                                head.getCDocSub(), head.getCDocVer());

                        Date periodDate = DateUtil.getLastDayOfMonth(head.getPeriodYear(), head.getPeriodMonth() - 1);

                        if (documentVersion.getEndDate() == null){
                            documentVersion.setEndDate(DateUtil.newDate(31, 11, 2099));
                        }

                        if (!declarationService.checkPeriod(declaration) || documentVersion == null
                                || periodDate.before(documentVersion.getBeginDate())
                                || periodDate.after(documentVersion.getEndDate())){
                            log.error("Ошибка загрузки документа - не корректный период");
                            error(getStringFormat("error_period_date", declaration.getTemplateName(),
                                    head.getPeriodMonth(), head.getPeriodYear(),
                                    documentVersion.getCDocVer(), documentVersion.getBeginDate(),
                                    documentVersion.getEndDate()));
                        }else {
                            //validate structure, check rules and save
                            declarationService.validate(declaration);
                            declarationService.check(declaration);
                            declarationBean.save(declaration);

                            PersonProfile personProfile = declaration.getPersonProfile();

                            info(getStringFormat("info_declaration_upload",
                                    fileName,
                                    getString("period_type_" + declaration.getHead().getPeriodType()),
                                    declaration.getHead().getPeriodMonth(),
                                    declaration.getHead().getPeriodYear(),
                                    personProfile != null ? personProfile.getProfileName() : getString("empty_profile")));

                            info(declaration.isValidated()
                                    ? getString("info_validated")
                                    : getStringFormat("info_validate_error", declaration.getValidateMessage()));

                            info((declaration.isChecked()
                                    ? getString("info_checked")
                                    : getStringFormat("info_check_error", declaration.getCheckMessage())));
                        }

                        //add to filter
                        declarationFilter.getPeriods().clear();
                        declarationFilter.getPeriods().add(new Period(head.getPeriodMonth(), head.getPeriodType(),
                                head.getPeriodYear()));

                        log.info("Документ загружен", new Event(EventCategory.IMPORT, declaration));
                    } catch (NumberFormatException e) {
                        error(getStringFormat("error_filename", fileName));

                        log.error("Ошибка загрузки документа - неверное название файла", e);
                    } catch (Exception e) {
                        error(getStringFormat("error_upload", fileName));

                        log.error("Ошибка загрузки документа", e);
                    }
                }

                update.onUpdate(target);

                dialog.close(target);

                fileUploadModel.getObject().clear();
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
