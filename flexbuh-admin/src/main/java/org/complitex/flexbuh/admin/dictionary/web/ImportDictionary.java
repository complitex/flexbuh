package org.complitex.flexbuh.admin.dictionary.web;

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
import org.complitex.flexbuh.admin.dictionary.service.ImportDictionaryService;
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

    private DictionaryImportListener importListener;
    private CheckBoxMultipleChoice<DictionaryType> dictionaryTypes;

    public ImportDictionary() {

        final WebMarkupContainer container = new WebMarkupContainer("container");
        add(container);

        container.add(new FeedbackPanel("messages"));

        Form form = new Form("form");
        container.add(form);

        importListener = new DictionaryImportListener();

        //Dictionary types
        dictionaryTypes = new CheckBoxMultipleChoice<>("dictionaryTypes", dictionaryModel,
                Arrays.asList(DictionaryType.values()),
                new IChoiceRenderer<DictionaryType>() {

                    @Override
                    public Object getDisplayValue(DictionaryType type) {
                        String info = "";

                        DictionaryImportChildListener childListener = importListener.getChildListener(type, false);

                        if (childListener != null && childListener.getStatus() != null){
                            info += " (" + "всего: " + childListener.getTotal();
                            info += ", " + "добавлено: " + childListener.getInserted();
                            info += ", " + "обновлено: " + childListener.getUpdated();
                            info += childListener.getErrorMessage() != null
                                    ? ", ошибка: " + childListener.getErrorMessage() : "";
                            info += ")";
                        }

                        return getString(type.name()) + info;
                    }

                    @Override
                    public String getIdValue(DictionaryType type, int index) {
                        return type.ordinal() + "";
                    }
                });
        dictionaryTypes.setOutputMarkupId(true);
        form.add(dictionaryTypes);

        //Кнопка импортировать
        Button process = new Button("process") {
            @Override
            public void onSubmit() {
                List<DictionaryType> fileNames = Lists.newArrayList();

                for (DictionaryType dictionaryType : dictionaryModel.getObject()) {
                    fileNames.add(dictionaryType);
                }

                importListener.clear();

                importDictionaryService.process(importListener, dictionaryModel.getObject());

                container.add(newTimer());
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

    private AjaxSelfUpdatingTimerBehavior newTimer(){

        return new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)){
            @Override
            protected void onPostProcessTarget(AjaxRequestTarget target) {
                target.add(dictionaryTypes);

                if (importListener.isEnded()) {
                    dictionaryModel.getObject().clear();

                    if (importListener.getCriticalErrorMessage() != null){
                        error(importListener.getCriticalErrorMessage());
                    }

                    info(getStringFormat("complete"));
                    stop();
                } else {
                    DictionaryType dictionaryType = importListener.getProcessing();

                    if (dictionaryType != null) {
                        info(getStringFormat("processing", dictionaryType.getFileName()));
                    }
                }
            }
        };
    }
}
