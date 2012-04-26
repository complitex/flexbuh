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
import org.complitex.flexbuh.admin.importexport.service.ImportTemplateXMLService;
import org.complitex.flexbuh.common.entity.template.TemplateXMLType;
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
    private ImportTemplateXMLService importTemplateService;

    private IModel<List<TemplateXMLType>> typeModel = new ListModel<>();

    public ImportFile() {

        final WebMarkupContainer container = new WebMarkupContainer("container");
        add(container);

        container.add(new Dialog("dialog")); //fix: ajax timer don't work without jquery

        container.add(new FeedbackPanel("messages"));

        Form form = new Form("form");
        container.add(form);

        //Dictionary types
        final CheckBoxMultipleChoice<TemplateXMLType> dataFiles =
                new CheckBoxMultipleChoice<>("dataFiles", typeModel, Arrays.asList(TemplateXMLType.values()),
                        new IChoiceRenderer<TemplateXMLType>() {

                            @Override
                            public Object getDisplayValue(TemplateXMLType object) {
                                return getString(object.name());
                            }

                            @Override
                            public String getIdValue(TemplateXMLType object, int index) {
                                return object.toString();
                            }
                        });

        form.add(dataFiles);

        final DictionaryImportListener importListener = new DictionaryImportListener();

        //Кнопка импортировать
        Button process = new Button("process") {
            @Override
            public void onSubmit() {
                log.debug("Submit process");

                log.debug("Selected objects: {}", typeModel.getObject());

                for (TemplateXMLType type : typeModel.getObject()) {
                    importTemplateService.process(type, importListener);
                }

                container.add(newTimer(importListener));
            }

            @Override
            public boolean isVisible() {
                return importListener.isEnded();
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
                    typeModel.getObject().clear();

                    info(getStringFormat("complete", listener.getCountCompleted(), listener.getCountCanceled(), listener.getCountTotal()));
                    stop();
                } else {
                    info(getStringFormat("processing", listener.currentImportFile(), listener.getCountCompleted(), listener.getCountCanceled(), listener.getCountTotal()));
                }
            }
        };
    }
}
