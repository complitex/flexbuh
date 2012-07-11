package org.complitex.flexbuh.admin.dictionary.web;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.flexbuh.common.entity.template.TemplateXML;
import org.complitex.flexbuh.common.entity.template.TemplateXMLType;
import org.complitex.flexbuh.common.logging.Event;
import org.complitex.flexbuh.common.logging.EventCategory;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.common.service.TemplateXMLBean;
import org.complitex.flexbuh.common.template.FormTemplatePage;
import org.complitex.flexbuh.common.util.DateUtil;
import org.complitex.flexbuh.common.web.component.HtmlTextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.07.12 17:41
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class TemplateXMLEdit extends FormTemplatePage{
    private final static Logger log = LoggerFactory.getLogger(TemplateXMLEdit.class);

    @EJB
    private TemplateXMLBean templateXMLBean;

    public TemplateXMLEdit(PageParameters parameters) {
        final String name = parameters.get("name").toString();
        final TemplateXMLType type = parameters.get("type").toEnum(TemplateXMLType.class);

        final TemplateXML oldTemplateXML = templateXMLBean.getTemplateXML(type, name);
        final TemplateXML templateXML = templateXMLBean.getTemplateXML(type, name);

        if (templateXML == null){
            getSession().error(getString("error_not_found"));
            log.error("Шаблон {} типа {} не найден", name, type.name());

            setResponsePage(TemplateXMLList.class);
            return;
        }


        add(new Label("title", getString("title")));
        add(new FeedbackPanel("messages"));

        Form form = new Form("form");
        add(form);

        //Тип
        form.add(new Label("type", templateXML.getType().name()));

        //Название
        form.add(new Label("name", templateXML.getName()));

        //Шаблон
        form.add(new HtmlTextArea<>("data", new PropertyModel<>(templateXML, "data")));

        form.add(new Button("submit"){
            @Override
            public void onSubmit() {
                templateXML.setUploadDate(DateUtil.getCurrentDate());

                templateXMLBean.update(templateXML);

                getSession().info(getStringFormat("info_updated", templateXML.getUploadDate()));
                log.info("Обновлен шаблон", new Event(EventCategory.EDIT, oldTemplateXML, templateXML));
            }
        });

        form.add(new Button("cancel"){
            @Override
            public void onSubmit() {
                PageParameters pageParameters = new PageParameters();
                pageParameters.add("type", templateXML.getType());

                setResponsePage(TemplateXMLList.class, pageParameters);
            }
        });
    }
}
