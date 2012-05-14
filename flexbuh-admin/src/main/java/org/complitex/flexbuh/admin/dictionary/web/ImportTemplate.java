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
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.util.time.Duration;
import org.complitex.flexbuh.admin.dictionary.service.ImportTemplateService;
import org.complitex.flexbuh.common.entity.template.TemplateXMLType;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.common.template.TemplatePage;
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
public class ImportTemplate extends TemplatePage {
    private final static Logger log = LoggerFactory.getLogger(ImportTemplate.class);

    @EJB
    private ImportTemplateService importTemplateService;

    private IModel<List<TemplateXMLType>> typeModel = new ListModel<>();

    private CheckBoxMultipleChoice<TemplateXMLType> templateXMLTypes;

    private DictionaryImportListener listener = new DictionaryImportListener();

    public ImportTemplate() {
        final WebMarkupContainer container = new WebMarkupContainer("container");
        add(container);

        container.add(new FeedbackPanel("messages"));

        Form form = new Form("form");
        container.add(form);

        //Dictionary types
        templateXMLTypes = new CheckBoxMultipleChoice<>("dataFiles", typeModel,
                Arrays.asList(TemplateXMLType.values()),
                new IChoiceRenderer<TemplateXMLType>() {

                    @Override
                    public Object getDisplayValue(TemplateXMLType type) {
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
                    public String getIdValue(TemplateXMLType object, int index) {
                        return object.ordinal() + "";
                    }
                });
        templateXMLTypes.setOutputMarkupId(true);
        form.add(templateXMLTypes);

        //Кнопка импортировать
        Button process = new Button("process") {
            @Override
            public void onSubmit() {
                listener.clear();

                for (TemplateXMLType type : typeModel.getObject()){
                    importTemplateService.process(type, listener.getChildListener(type, true));
                }

                container.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)){
                    @Override
                    protected void onPostProcessTarget(AjaxRequestTarget target) {
                        target.add(templateXMLTypes);

                        if (listener.isDone()) {
                            typeModel.getObject().clear();

                            info(getStringFormat("complete"));
                            stop();
                        }else {
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
