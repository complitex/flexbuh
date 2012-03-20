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
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.util.time.Duration;
import org.complitex.flexbuh.admin.importexport.service.ImportDictionaryService;
import org.complitex.flexbuh.common.entity.dictionary.DictionaryType;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.common.service.dictionary.DictionaryTypeBean;
import org.complitex.flexbuh.common.template.TemplatePage;
import org.odlabs.wiquery.ui.datepicker.DatePicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.Date;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 18.08.11 17:26
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class ImportDictionary extends TemplatePage {
    private final static Logger log = LoggerFactory.getLogger(ImportDictionary.class);

    private final static String FORM_DATE_FORMAT = "MM/dd/yyyy";

    @EJB
    private DictionaryTypeBean dictionaryTypeService;

    @EJB
    private ImportDictionaryService importDictionaryService;

    private IModel<List<DictionaryType>> dictionaryModel = new ListModel<>();

    @SuppressWarnings("unchecked")
    public ImportDictionary() {

        final WebMarkupContainer container = new WebMarkupContainer("container");
        add(container);

        container.add(new FeedbackPanel("messages"));

        Form form = new Form("form");
        container.add(form);

        //Dictionary types
        final CheckBoxMultipleChoice<DictionaryType> dictionaryTypes =
                new CheckBoxMultipleChoice<>("dictionaryTypes", dictionaryModel, dictionaryTypeService.getDictionaryTypes(),
                        new IChoiceRenderer<DictionaryType>() {

                            @Override
                            public Object getDisplayValue(DictionaryType object) {
                                return object.getName(getLocale());
                            }

                            @Override
                            public String getIdValue(DictionaryType object, int index) {
                                return object.getCode();
                            }
                        });

        form.add(dictionaryTypes);

        //Begin Date
        final IModel<Date> beginDateModel = new Model<>();
        final DatePicker<Date> beginDatePicker = new DatePicker<Date>("beginDate", beginDateModel, Date.class);
        form.add(beginDatePicker);

        //End Date
        final IModel<Date> endDateModel = new Model<>();
        final DatePicker<Date> endDatePicker = new DatePicker<Date>("endDate", endDateModel, Date.class);
        form.add(endDatePicker);

        //Кнопка импортировать
        Button process = new Button("process") {
            @Override
            public void onSubmit() {
                log.debug("Submit process");

                List<String> fileNames = Lists.newArrayList();
                log.debug("Selected objects: {}", dictionaryModel.getObject());

                log.debug("Begin date {}, end date {}", beginDateModel.getObject(), endDateModel.getObject());

                for (DictionaryType dictionaryType : dictionaryModel.getObject()) {
                    fileNames.add(dictionaryType.getFileName());
                }

                DictionaryImportListener importListener = new DictionaryImportListener();
                importListener.addCountTotal(fileNames.size());

                importDictionaryService.processFiles(getSessionId(), importListener, fileNames,
                        beginDateModel.getObject(), endDateModel.getObject());

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

                    info(getStringFormat("complete", listener.getCountCompleted(), listener.getCountCanceled(), listener.getCountTotal()));
                    stop();
                } else {
                    info(getStringFormat("processing", listener.currentImportFile(), listener.getCountCompleted(), listener.getCountCanceled(), listener.getCountTotal()));
                }
            }
        };
    }
}
