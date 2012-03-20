package org.complitex.flexbuh.admin.importexport.web;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.util.time.Duration;
import org.complitex.flexbuh.admin.importexport.service.ImportTemplateControlService;
import org.complitex.flexbuh.admin.importexport.service.ImportTemplateFOService;
import org.complitex.flexbuh.admin.importexport.service.ImportTemplateXSDService;
import org.complitex.flexbuh.admin.importexport.service.ImportTemplateXSLService;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.common.template.TemplatePage;
import org.odlabs.wiquery.ui.dialog.Dialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.Arrays;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 30.08.11 10:09
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class ImportFile extends TemplatePage {

    private final static Logger log = LoggerFactory.getLogger(ImportFile.class);

    @EJB
    private ImportTemplateFOService importTemplateFOService;

    @EJB
    private ImportTemplateXSDService importTemplateXSDService;

    @EJB
    private ImportTemplateXSLService importTemplateXSLService;

    @EJB
    private ImportTemplateControlService importTemplateControlService;

    private IModel<List<DataFile>> dataFileModel = new ListModel<>();

    @SuppressWarnings("unchecked")
    public ImportFile() {

        final WebMarkupContainer container = new WebMarkupContainer("container");
        add(container);

        container.add(new Dialog("dialog")); //fix: ajax timer don't work without jquery

        container.add(new FeedbackPanel("messages"));

        Form form = new Form("form");
        container.add(form);

        //Dictionary types
        final CheckBoxMultipleChoice<DataFile> dataFiles =
                new CheckBoxMultipleChoice<>("dataFiles", dataFileModel, Arrays.asList(DataFile.values()),
                        new IChoiceRenderer<DataFile>() {

                            @Override
                            public Object getDisplayValue(DataFile object) {
                                return getString(object.toString());
                            }

                            @Override
                            public String getIdValue(DataFile object, int index) {
                                return object.toString();
                            }
                        });

        form.add(dataFiles);

        //Кнопка импортировать
        Button process = new Button("process") {
            @Override
            public void onSubmit() {
                log.debug("Submit process");

                log.debug("Selected objects: {}", dataFileModel.getObject());

                DictionaryImportListener importListener = new DictionaryImportListener();

                final Long sessionId = getSessionId();

                for (DataFile dataFile : dataFileModel.getObject()) {
                    switch (dataFile) {
                        case CONTROL:
                            log.debug("start import control");
                            importListener.addCountTotal(importTemplateControlService.listImportFiles().length);
                            importTemplateControlService.processFiles(sessionId, importListener, null, null);
                            break;
                        case FO:
                            log.debug("start import fo");
                            importListener.addCountTotal(importTemplateFOService.listImportFiles().length);
                            importTemplateFOService.processFiles(sessionId, importListener, null, null);
                            break;
                        case XSD:
                            log.debug("start import xsd");
                            importListener.addCountTotal(importTemplateXSDService.listImportFiles().length);
                            importTemplateXSDService.processFiles(sessionId, importListener, null, null);
                            break;
                        case XSL:
                            log.debug("start import xsl");
                            importListener.addCountTotal(importTemplateXSLService.listImportFiles().length);
                            importTemplateXSLService.processFiles(sessionId, importListener, null, null);
                            break;
                    }
                }

                container.add(newTimer(importListener));
            }

            @Override
            public boolean isVisible() {
                return true; //listeners.isEmpty();
            }
        };
        form.add(process);

        //Ошибки
        container.add(new Label("error", new LoadableDetachableModel<Object>() {
            @Override
            protected Object load() {
                return null;//addressImportService.getErrorMessage();
            }
        }){
            @Override
            public boolean isVisible() {
                return false;//addressImportService.isError();
            }
        });
    }

    private AjaxSelfUpdatingTimerBehavior newTimer(final DictionaryImportListener listener){

        return new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)){
            @Override
            protected void onPostProcessTarget(AjaxRequestTarget target) {

                log.debug("import file: {}, listener status: {}, count completed: {}, count canceled: {}",
                        new Object[]{listener.currentImportFile(), listener.getStatus(), listener.getCountCompleted(), listener.getCountCanceled()});

                if (listener.isEnded()) {
                    dataFileModel.getObject().clear();

                    info(getStringFormat("complete", listener.getCountCompleted(), listener.getCountCanceled(), listener.getCountTotal()));
                    stop();
                } else {
                    info(getStringFormat("processing", listener.currentImportFile(), listener.getCountCompleted(), listener.getCountCanceled(), listener.getCountTotal()));
                }
            }
        };
    }


    private enum DataFile {
        XSD("xsd"), XSL("xsl"), FO("fo"), CONTROL("control");

        private String fileName;

        private DataFile(String fileName) {
            this.fileName = fileName;
        }

        @Override
        public String toString() {
            return fileName;
        }
    }
}
