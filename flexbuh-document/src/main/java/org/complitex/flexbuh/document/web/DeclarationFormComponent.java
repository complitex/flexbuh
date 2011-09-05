package org.complitex.flexbuh.document.web;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.*;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.AbstractRepeater;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
import org.apache.wicket.validation.IValidator;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.entity.Rule;
import org.complitex.flexbuh.document.service.TemplateService;
import org.complitex.flexbuh.document.web.component.AddRowPanel;
import org.complitex.flexbuh.document.web.model.DeclarationBooleanModel;
import org.complitex.flexbuh.document.web.model.DeclarationChoiceModel;
import org.complitex.flexbuh.document.web.model.DeclarationStringModel;
import org.complitex.flexbuh.document.web.validation.Restriction;
import org.complitex.flexbuh.util.ScriptUtil;
import org.complitex.flexbuh.util.StringUtil;
import org.complitex.flexbuh.util.XmlUtil;
import org.complitex.flexbuh.web.component.CssStyleTextField;
import org.complitex.flexbuh.web.component.RadioSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wiquery.plugin.jquertytools.tooltip.TooltipBehavior;

import javax.ejb.EJB;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.08.11 15:58
 */
public class DeclarationFormComponent extends WebMarkupContainer implements IMarkupResourceStreamProvider{
    private final static Logger log = LoggerFactory.getLogger(DeclarationFormComponent.class);
    private Pattern compile = Pattern.compile("\\^(\\w*\\.?\\w*)");

    @EJB
    private TemplateService templateService;

    private Map<String, RadioSet<String>> radioSetMap = new HashMap<String, RadioSet<String>>();

    private transient Declaration declaration;

    //todo check serialization
    private transient Document schema;
    private transient Document template;
    private transient Document commonTypes;

    private transient XPath schemaXPath = XmlUtil.newSchemaXPath();
    private transient XPath templateXPath = XmlUtil.newXPath();
    private transient ScriptEngine scriptEngine = ScriptUtil.newScriptEngine();

    private StringResourceStream stringResourceStream;

    private static Map<String, IValidator> validatorMap = new ConcurrentHashMap<>();

    private Map<String, Rule> rulesMap;
    private Map<String, TextField<String>> textFieldMap = new HashMap<>();
    private Map<String, List<TextField<String>>> multiRowTextFieldMap = new HashMap<>();

    private WebMarkupContainer container;

    private Integer rowNextMarkupId = 0;

    private transient List<Element> stretchElements = new ArrayList<>();

    public DeclarationFormComponent(String id, String templateName, Declaration declaration){
        super(id);

        try {
            //Template
            template = templateService.getDocument(templateName, declaration);

            //Schema
            schema = templateService.getSchema(templateName);

            //Common Types
            commonTypes = templateService.getSchema("common_types");

            //Rules
            rulesMap = templateService.getRules(templateName);

            //Declaration
            this.declaration = declaration;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        init();
    }

    private void init(){
        //Form
        NodeList bodyNodeList = template.getElementsByTagName("body");

        if (bodyNodeList.getLength() < 1){
            bodyNodeList = template.getElementsByTagName("BODY");
        }

        Element div = (Element) template.renameNode(bodyNodeList.item(0), null, "div");
        div.setAttribute("xmlns:wicket", "http://wicket.apache.org/dtds.data/wicket-xhtml1.4-strict.dtd");
        div.setAttribute("wicket:id", "container");

        container = new WebMarkupContainer("container");
        add(container);

        //Input
        NodeList inputList = div.getElementsByTagName("input");
        for (int i=0; i < inputList.getLength(); ++i){
            addInput((Element) inputList.item(i));
        }

        //Stretch table
        NodeList stretchList = XmlUtil.getElementsById("StretchTable", div, templateXPath);
        for (int i=0; i < stretchList.getLength(); ++i){
            stretchElements.add((Element) stretchList.item(i));

            addStretchTable(i);
        }

        //Markup
        stringResourceStream = new StringResourceStream(XmlUtil.getString(div), "text/html");
        stringResourceStream.setCharset(Charset.forName("UTF-8"));
    }

    @Override
    protected void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
        //todo cache
        try {
            MarkupResourceStream markupResourceStream = new MarkupResourceStream(stringResourceStream);

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
        return stringResourceStream;
    }

    private void addInput(Element inputElement){
        addInput(null, inputElement, container, true, true);
    }

    private void addInput(Integer rowNum, Element inputElement, MarkupContainer parent, boolean updateMarkup, boolean skipMultiRow){
        try {
            //lookup schema rules
            Element schemaElement = XmlUtil.getElementByName(inputElement.getAttribute("id"), schema, schemaXPath);
            String schemaType = schemaElement.getAttribute("type");

            //id
            final String id = inputElement.getAttribute("id");

            //set wicket id markup
            if (updateMarkup) {
                inputElement.setAttribute("wicket:id", id);
            }

            //multirow input
            String maxOccurs = schemaElement.getAttribute("maxOccurs");

            final boolean multiRow = maxOccurs != null && !maxOccurs.isEmpty() && Integer.parseInt(maxOccurs) > 1;

            //skip
            if (multiRow && skipMultiRow){
                return;
            }

            //create form component
            if ("DGchk".equals(schemaType)){
                Element schemaChoice = (Element) schemaElement.getParentNode();

                if ("xs:choice".equals(schemaChoice.getTagName())){
                    //find parent for component
                    RadioSet<String> radioSet = radioSetMap.get(id);

                    //create choice
                    if (radioSet == null){
                        String rId = "radio_"+id;

                        //create radio set markup
                        if (updateMarkup) {
                            Element radioSetElement = template.createElement("span");
                            radioSetElement.setAttribute("wicket:id", rId);
                            inputElement.getParentNode().appendChild(radioSetElement);
                        }

                        //add radio set component
                        radioSet = new RadioSet<>(rId, new DeclarationChoiceModel(id, declaration));
                        parent.add(radioSet);

                        //child nodes to radio set
                        NodeList children = schemaChoice.getElementsByTagName("xs:element");
                        for (int i=0; i < children.getLength() ; ++i){
                            Element el = (Element) children.item(i);
                            radioSetMap.put(el.getAttribute("name"), radioSet);
                        }
                    }

                    inputElement.setAttribute("type", "radio");

                    Radio<String> radio = new Radio<>(id, new Model<>(id), radioSet);
                    radioSet.addRadio(radio);

                    parent.add(radio);
                }else{
                    if (updateMarkup) {
                        inputElement.setAttribute("type", "checkbox");
                    }

                    CheckBox checkBox = new CheckBox(id, new DeclarationBooleanModel(id, declaration));

                    parent.add(checkBox);
                }
            }else{
                //TextField
                inputElement.setAttribute("type", "text");

                final CssStyleTextField<String> textField = new CssStyleTextField<>(id, new DeclarationStringModel(rowNum, id, declaration));
                textField.setOutputMarkupId(true);
                parent.add(textField);

                //Add to map for rule auto fill
                if (multiRow) {
                    addMultiRowTextField(id, textField);
                }else {
                    textFieldMap.put(id, textField);
                }

                String feedbackContainerId = "feedback_container_" + id;

                //Markup
                if (updateMarkup) {
                    Element feedbackContainerElement = template.createElement("div");
                    feedbackContainerElement.setAttribute("wicket:id", feedbackContainerId);
                    feedbackContainerElement.setAttribute("style", "display: none; background-color: #f0e68c;");
                    inputElement.getParentNode().appendChild(feedbackContainerElement);

                    Element feedbackElement = template.createElement("span");
                    feedbackElement.setAttribute("wicket:id", "feedback_" + id);
                    feedbackContainerElement.appendChild(feedbackElement);
                }

                //Feedback container
                WebMarkupContainer feedbackContainer = new WebMarkupContainer(feedbackContainerId);
                feedbackContainer.setOutputMarkupId(true);
                parent.add(feedbackContainer);

                //Feedback label
                final Label feedbackLabel = new Label("feedback_" + id, new Model<String>());
                feedbackLabel.setOutputMarkupId(true);
                feedbackContainer.add(feedbackLabel);

                //Rule
                final Rule rule = rulesMap.get(id);
                if (rule != null) {
                    feedbackLabel.setDefaultModelObject(rule.getDescription());
                }

                //Tooltip
                textField.add(new TooltipBehavior().setTip(feedbackContainer));

                //Ajax update
                textField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
                    @Override
                    protected void onError(AjaxRequestTarget target, RuntimeException e) {
                        FeedbackMessage feedbackMessage = textField.getFeedbackMessage();

                        if (feedbackMessage != null) {
                            feedbackLabel.setDefaultModelObject(feedbackMessage.getMessage());
                        }

                        target.addComponent(feedbackLabel);

                        target.appendJavascript("$('#" + textField.getMarkupId() +"').css('background-color', '#ff9999')");
                        textField.setCssStyle("background-color: #ff9999");
                    }

                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        String value = textField.getValue();

                        if (value != null && !value.isEmpty()) {
                            //Auto fill
                            autoFill(target);

                            target.appendJavascript("$('#" + textField.getMarkupId() +"').css('background-color', '#99ff99')");
                            textField.setCssStyle("background-color: #99ff99");
                        }else{
                            target.appendJavascript("$('#" + textField.getMarkupId() +"').css('background-color', '#cccccc')");
                            textField.setCssStyle("background-color: #cccccc");
                        }

                        feedbackLabel.setDefaultModelObject(rule != null ? rule.getDescription() : "");
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

        if (typeElement != null) {
            Restriction<T> restriction = new Restriction<>(typeElement);

            if (restriction.isComplex()){
                return createValidator(restriction.getBase());
            }

            return restriction;
        }

        return null;
    }

    private void autoFill(AjaxRequestTarget target){
        try {
            for (Rule rule : rulesMap.values()){
                String expression = rule.getExpression();

                //todo implement many row
                if (expression.contains("SUMF")){
                    continue;
                }

                List<String> ids = extractIds(expression);

                String value = "";

                if (expression.contains("SUM") && ids.size() > 0){
                    double sum = 0;

                    for (TextField<String> textField : multiRowTextFieldMap.get(ids.get(0))){
                        sum += (StringUtil.isDecimal(textField.getValue())) ? Double.parseDouble(textField.getValue()) : 0;
                    }

                    value = sum + "";
                }else{
                    for (String id : ids){
                        TextField<String> textField = textFieldMap.get(id);

                        //todo linked docs?
                        if (textField == null){
                            return;
                        }

                        //todo add validation checking
                        String v = (StringUtil.isDecimal(textField.getValue())) ? textField.getValue() : "0";

                        expression = expression.replace("^" + id, v);
                    }

                    expression = expression.replace("ABS", "Math.abs");
                    expression = StringEscapeUtils.unescapeXml(expression);

                    value = scriptEngine.eval(expression).toString();
                }

                String autoFillId = rule.getCDocRowC().replace("^", "");
                TextField<String> autoFill = textFieldMap.get(autoFillId);

                autoFill.getModel().setObject(value);

                target.addComponent(autoFill);

                target.appendJavascript("$('#" + autoFill.getMarkupId() +"').css('background-color', '#add8e6')");
            }
        } catch (ScriptException e) {
            log.error("Ошибка автозаполнения", e);
        }
    }

    private List<String> extractIds(String expression){
        List<String> ids = new ArrayList<>();

        Matcher matcher = compile.matcher(expression);

        while (matcher.find()){
            ids.add(matcher.group(1));
        }

        return ids;
    }

    private void addStretchTable(final int index){
        //Stretch table
        Element stretchElement = stretchElements.get(index);

        Node stretchTableElement = stretchElement.getParentNode();
        while (!"tbody".equalsIgnoreCase(stretchTableElement.getNodeName())){
            stretchTableElement = stretchTableElement.getParentNode();
        }

        String stretchTableId = "stretch_table_" + index;

        ((Element)stretchTableElement).setAttribute("wicket:id", stretchTableId);

        final WebMarkupContainer stretchTable = new WebMarkupContainer(stretchTableId);
        stretchTable.setOutputMarkupId(true);
        container.add(stretchTable);

        //Stretch row
        final String id = "stretch_row_" + index;

        stretchElement.setAttribute("wicket:id", id);

        final List<Component> rows = new ArrayList<>();

        final NodeList inputList = stretchElement.getElementsByTagName("input");

        Element inputElement = (Element) inputList.item(0);

        //wrap table
        Element inputParentElement = (Element) inputElement.getParentNode();

        Element wrapTableElement = template.createElement("table");
        Element trElement = template.createElement("tr");
        Element tdLeftElement = template.createElement("td");
        tdLeftElement.setAttribute("wicket:id", "add_row_panel");
        Element tdRightElement = template.createElement("td");

        inputParentElement.replaceChild(wrapTableElement, inputElement);
        wrapTableElement.appendChild(trElement);
        trElement.appendChild(tdLeftElement);
        trElement.appendChild(tdRightElement);
        tdRightElement.appendChild(inputElement);

        //style
        wrapTableElement.setAttribute("style", "width: 100%;");
        wrapTableElement.setAttribute("border", "0");
        wrapTableElement.setAttribute("cellpadding", "0");
        wrapTableElement.setAttribute("cellspacing", "0");
        tdLeftElement.setAttribute("style", "width: 40px;");

        //Add rows
        addRow(index, stretchTable, rows, 0);

        //Repeater
        stretchTable.add(new AbstractRepeater(id) {
            @Override
            protected Iterator<? extends Component> renderIterator() {
                return rows.iterator();
            }

            @Override
            protected void onPopulate() {
                //remove
                for (Iterator<? extends Component> it = iterator(); it.hasNext();){
                    Component c = it.next();

                    if (!rows.contains(c)){
                        remove(c);
                    }
                }

                //add
                for (Component row : rows) {
                    boolean found = false;

                    for (Iterator<? extends Component> it = iterator(); it.hasNext();){
                        if (row.equals(it.next())){
                            found = true;
                            break;
                        }
                    }

                    if (!found){
                        add(row);
                    }
                }

                //delete visible
                if (rows.size() == 1){
                    ((AddRowPanel) ((WebMarkupContainer) rows.get(0)).get("add_row_panel")).setDeleteVisible(false);
                }
            }
        });
    }

    private void addRow(final int index, final WebMarkupContainer stretchTable, final List<Component> rows, int addRowIndex) {
        final WebMarkupContainer row = new WebMarkupContainer(++rowNextMarkupId + "");
        rows.add(addRowIndex, row);

        NodeList inputList = stretchElements.get(index).getElementsByTagName("input");

        for (int i = 0; i < inputList.getLength(); ++i){
            addInput(rowNextMarkupId, (Element) inputList.item(i), row, addRowIndex == 0, false);
        }

        //add row panel
        row.add(new AddRowPanel("add_row_panel") {
            @Override
            protected void onAdd(AjaxRequestTarget target) {
                setDeleteVisible(true);

                addRow(index, stretchTable, rows, rows.indexOf(row) + 1);

                target.addComponent(stretchTable);
            }

            @Override
            protected void onDelete(AjaxRequestTarget target) {
                rows.remove(row);

                removeMultiRowTextField(row);

                autoFill(target);

                target.addComponent(stretchTable);
            }
        });
    }

    private void addMultiRowTextField(String id, TextField<String> textField){
        List<TextField<String>> textFields = multiRowTextFieldMap.get(id);

        if (textFields == null){
            textFields = new ArrayList<>();
            multiRowTextFieldMap.put(id, textFields);
        }

        textFields.add(textField);
    }

    private void removeMultiRowTextField(WebMarkupContainer row){
        for (Iterator<? extends Component> it = row.iterator();it.hasNext();){
            Component component = it.next();

            if (component instanceof TextField){
                TextField textField = (TextField) component;

                for (List<TextField<String>> textFields : multiRowTextFieldMap.values()){
                    for (int i = 0, textFieldsSize = textFields.size(); i < textFieldsSize; i++) {
                        textFields.remove(textField);
                    }
                }
            }
        }
    }
}

