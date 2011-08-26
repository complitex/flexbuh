package org.complitex.flexbuh.document.web;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.*;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
import org.apache.wicket.validation.IValidator;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.service.TemplateService;
import org.complitex.flexbuh.document.web.model.DeclarationBooleanModel;
import org.complitex.flexbuh.document.web.model.DeclarationChoiceModel;
import org.complitex.flexbuh.document.web.model.DeclarationStringModel;
import org.complitex.flexbuh.document.web.validation.Restriction;
import org.complitex.flexbuh.util.XmlUtil;
import org.complitex.flexbuh.web.component.RadioSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.wiquery.plugin.jquertytools.tooltip.TooltipBehavior;
import org.xml.sax.SAXException;

import javax.ejb.EJB;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.08.11 15:58
 */
public class DeclarationFormComponent extends WebMarkupContainer implements IMarkupResourceStreamProvider{
    private final static Logger log = LoggerFactory.getLogger(DeclarationFormComponent.class);

    @EJB
    private TemplateService templateService;

    private Map<String, RadioSet<String>> radioSetMap = new HashMap<String, RadioSet<String>>();

    private transient MarkupResourceStream markupResourceStream;
    private transient Document commonTypes;
    private transient XPath schemaXPath;

    private static Map<String, IValidator> validatorMap = new ConcurrentHashMap<>();

    public DeclarationFormComponent(String id, String templateName, Declaration declaration){
        super(id);

        try {
            init(templateName, declaration);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void init(String templateName, Declaration declaration)
            throws TransformerException, JAXBException, ParserConfigurationException, IOException, SAXException, XPathFactoryConfigurationException {
        add(CSSPackageResource.getHeaderContribution(TooltipBehavior.CSS));

        Document template = templateService.getDocument(templateName, declaration);

        if (template == null){
            return;
        }

        Document schema = templateService.getSchema(templateName);

        //Schema XPath
        schemaXPath = XmlUtil.newSchemaXPath();

        //Common Types
        commonTypes = templateService.getSchema("common_types");

        NodeList bodyNodeList = template.getElementsByTagName("body");

        //Form
        Element div = (Element) template.renameNode(bodyNodeList.item(0), null, "div");
        div.setAttribute("xmlns:wicket", "http://wicket.apache.org/dtds.data/wicket-xhtml1.4-strict.dtd");
        div.setAttribute("wicket:id", "container");

        WebMarkupContainer container = new WebMarkupContainer("container");
        add(container);

        //Input
        NodeList inputList = div.getElementsByTagName("input");

        for (int i=0; i < inputList.getLength(); ++i){
            addFormComponent((Element) inputList.item(i), declaration, container, template, schema);
        }

        //Markup
        StringResourceStream stringResourceStream = new StringResourceStream(XmlUtil.getString(div), "text/html");
        stringResourceStream.setCharset(Charset.forName("UTF-8"));

        markupResourceStream = new MarkupResourceStream(stringResourceStream);
    }

    @Override
    protected void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
        //todo cache
        try {
            Markup markup = getApplication().getMarkupSettings().getMarkupParserFactory().newMarkupParser(markupResourceStream).parse();

            MarkupStream associatedMarkupStream = new MarkupStream(markup);

            renderComponentTagBody(associatedMarkupStream, (ComponentTag) associatedMarkupStream.get());
        } catch (Exception e) {
            log.error("Ошибка создания страницы", e);

            throw new WicketRuntimeException(e);
        }
    }

    @Override
    public IResourceStream getMarkupResourceStream(MarkupContainer container, Class<?> containerClass) {
        return markupResourceStream;
    }

    private void addFormComponent(Element input, Declaration declaration, WebMarkupContainer container, Document template, Document schema){
        try {
            final String id = input.getAttribute("id");

            //set wicket id markup
            input.setAttribute("wicket:id", id);

            //lookup schema rules
            Element schemaElement = XmlUtil.getElementByName(id, schema, schemaXPath);
            String schemaType = schemaElement.getAttribute("type");

            if ("DGchk".equals(schemaType)){
                Element schemaChoice = (Element) schemaElement.getParentNode();

                if ("xs:choice".equals(schemaChoice.getTagName())){
                    //find parent for component
                    RadioSet<String> radioSet = radioSetMap.get(id);

                    //create choice
                    if (radioSet == null){
                        String rId = "radio_"+id;

                        //create radio set markup
                        Element radioSetElement = template.createElement("span");
                        radioSetElement.setAttribute("wicket:id", rId);
                        input.getParentNode().appendChild(radioSetElement);

                        //add radio set component
                        radioSet = new RadioSet<>(rId, new DeclarationChoiceModel(id, declaration));
                        container.add(radioSet);

                        //child nodes to radio set
                        NodeList children = schemaChoice.getElementsByTagName("xs:element");
                        for (int i=0; i < children.getLength() ; ++i){
                            Element el = (Element) children.item(i);
                            radioSetMap.put(el.getAttribute("name"), radioSet);
                        }
                    }

                    input.setAttribute("type", "radio");

                    Radio<String> radio = new Radio<>(id, new Model<>(id), radioSet);
                    radioSet.addRadio(radio);

                    container.add(radio);
                }else{
                    input.setAttribute("type", "checkbox");

                    CheckBox checkBox = new CheckBox(id, new DeclarationBooleanModel(id, declaration));

                    container.add(checkBox);
                }
            }else{
                //TextField
                input.setAttribute("type", "text");

                final TextField<String> textField = new TextField<>(id, new DeclarationStringModel(id, declaration));
                textField.setMarkupId(id);
                textField.setOutputMarkupId(true);
                container.add(textField);

                //Feedback container
                String feedbackContainerId = "feedback_container_" + id;

                Element feedbackContainerElement = template.createElement("div");
                feedbackContainerElement.setAttribute("wicket:id", feedbackContainerId);
                feedbackContainerElement.setAttribute("style", "display: none; background-color: #f0e68c;");
                input.getParentNode().appendChild(feedbackContainerElement);

                WebMarkupContainer feedbackContainer = new WebMarkupContainer(feedbackContainerId);
                feedbackContainer.setMarkupId(feedbackContainerId);
                feedbackContainer.setOutputMarkupId(true);
                container.add(feedbackContainer);

                //Feedback label
                Element feedbackElement = template.createElement("span");
                feedbackElement.setAttribute("wicket:id", "feedback_" + id);
                feedbackContainerElement.appendChild(feedbackElement);

                final Label feedbackLabel = new Label("feedback_" + id, new Model<String>());
                feedbackLabel.setOutputMarkupId(true);
                feedbackContainer.add(feedbackLabel);

                //Tooltip
                textField.add(new TooltipBehavior().setTip(feedbackContainer));

                //Ajax update
                textField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
                    @Override
                    protected void onError(AjaxRequestTarget target, RuntimeException e) {
                        feedbackLabel.setDefaultModelObject(textField.getFeedbackMessage().getMessage());

                        target.addComponent(feedbackLabel);

                        target.appendJavascript("$('#" + id +"').css('background-color', '#ff9999')");
                    }

                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        String value = textField.getValue();

                        if (value != null && !value.isEmpty()) {
                            target.appendJavascript("$('#" + id +"').css('background-color', '#99ff99')");
                        }else{
                            target.appendJavascript("$('#" + id +"').css('background-color', '#cccccc')");
                        }

                        feedbackLabel.setDefaultModelObject("");
                        target.addComponent(feedbackLabel);
                    }
                });

                //Validation
                IValidator<String> validator = getValidator(schemaType);
                if (validator != null) {
                    textField.add(validator);
                }
            }
        } catch (XPathExpressionException e) {
            log.error("Ошибка добавления компонента формы ввода декларации", e);
        }
    }

    @SuppressWarnings({"unchecked"})
    private <T> IValidator<T> getValidator(String id) throws XPathExpressionException {
        IValidator<T> validator = validatorMap.get(id);

        if (validator == null){
            validator = createValidator(id);

            if (validator != null) {
                validatorMap.put(id, validator);
            }
        }

        return validator;
    }

    private <T> IValidator<T> createValidator(String schemaType) throws XPathExpressionException {
        Element typeElement = XmlUtil.getElementByName(schemaType, commonTypes, schemaXPath);

        return typeElement != null ? new Restriction<T>(typeElement) : null;
    }
}

