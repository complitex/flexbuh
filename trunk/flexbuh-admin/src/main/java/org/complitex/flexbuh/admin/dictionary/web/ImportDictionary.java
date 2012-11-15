package org.complitex.flexbuh.admin.dictionary.web;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.util.time.Duration;
import org.complitex.flexbuh.admin.dictionary.service.ImportDictionaryService;
import org.complitex.flexbuh.common.entity.dictionary.DictionaryType;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.common.template.TemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    private DictionaryImportListener listener = new DictionaryImportListener();
    private CheckBoxMultipleChoice<DictionaryType> dictionaryTypes;

    public ImportDictionary() {

        final WebMarkupContainer container = new WebMarkupContainer("container");
        add(container);

        container.add(new FeedbackPanel("messages"));

        Form form = new Form("form");
        container.add(form);

        //Dictionary types
        dictionaryTypes = new CheckBoxMultipleChoice<>("dictionaryTypes", dictionaryModel,
                Arrays.asList(DictionaryType.values()),
                new IChoiceRenderer<DictionaryType>() {

                    @Override
                    public Object getDisplayValue(DictionaryType type) {
                        String info = "";

                        DictionaryImportChildListener childListener = listener.getChildListener(type, false);

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
                listener.clear();

                listener.setExecute(true);
                importDictionaryService.process(listener, dictionaryModel.getObject());

                container.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)){
                    @Override
                    protected void onPostProcessTarget(AjaxRequestTarget target) {
                        target.add(dictionaryTypes);

                        if (listener.isDone()) {
                            log.debug("Import Done");
                            dictionaryModel.getObject().clear();

                            if (listener.getCriticalErrorMessage() != null){
                                error(listener.getCriticalErrorMessage());
                            }

                            info(getStringFormat("complete"));
                            stop();
                        } else {
                            info(getStringFormat("processing"));
                        }
                    }
                });
            }

            @Override
            public boolean isVisible() {
                return listener == null || listener.isDone();
            }
        };
        form.add(process);
    }
}
