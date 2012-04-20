package org.complitex.flexbuh.admin.importexport.web;

import com.google.common.collect.Lists;
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
import org.complitex.flexbuh.admin.importexport.service.ImportDictionaryService;
import org.complitex.flexbuh.common.entity.dictionary.DictionaryType;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.common.template.TemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.Arrays;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 18.08.11 17:26
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class ImportDictionary extends TemplatePage {
    private final static Logger log = LoggerFactory.getLogger(ImportDictionary.class);

    @EJB
    private ImportDictionaryService importDictionaryService;

    private IModel<List<DictionaryType>> dictionaryModel = new ListModel<>();

    public ImportDictionary() {

        final WebMarkupContainer container = new WebMarkupContainer("container");
        add(container);

        container.add(new FeedbackPanel("messages"));

        Form form = new Form("form");
        container.add(form);

        //Dictionary types
        final CheckBoxMultipleChoice<DictionaryType> dictionaryTypes =
                new CheckBoxMultipleChoice<>("dictionaryTypes", dictionaryModel, Arrays.asList(DictionaryType.values()),
                        new IChoiceRenderer<DictionaryType>() {

                            @Override
                            public Object getDisplayValue(DictionaryType type) {
                                return getString(type.name());
                            }

                            @Override
                            public String getIdValue(DictionaryType type, int index) {
                                return type.ordinal() + "";
                            }
                        });

        form.add(dictionaryTypes);

        //Кнопка импортировать
        Button process = new Button("process") {
            @Override
            public void onSubmit() {
                List<DictionaryType> fileNames = Lists.newArrayList();

                for (DictionaryType dictionaryType : dictionaryModel.getObject()) {
                    fileNames.add(dictionaryType);
                }

                DictionaryImportListener importListener = new DictionaryImportListener();
                importListener.addCountTotal(fileNames.size());

                importDictionaryService.process(importListener, dictionaryModel.getObject());

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

                log.debug("listener status: {}, count completed: {}, count canceled: {}",
                        new Object[]{listener.getStatus(), listener.getCountCompleted(), listener.getCountCanceled()});

                if (listener.isEnded()) {
                    dictionaryModel.getObject().clear();

                    if (listener.getErrorMessage() != null){
                        error(listener.getErrorMessage());
                    }

                    info(getStringFormat("complete", listener.getCountCompleted(), listener.getCountCanceled(), listener.getCountTotal()));
                    stop();
                } else {
                    info(getStringFormat("processing", listener.currentImportFile(), listener.getCountCompleted(), listener.getCountCanceled(), listener.getCountTotal()));
                }
            }
        };
    }
}
