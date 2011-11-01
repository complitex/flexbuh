package org.complitex.flexbuh.document.web;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.Markup;
import org.apache.wicket.markup.MarkupParser;
import org.apache.wicket.markup.MarkupResourceStream;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.AbstractRepeater;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.resource.StringResourceStream;
import org.apache.wicket.util.time.Time;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.entity.DeclarationValue;
import org.complitex.flexbuh.document.entity.Rule;
import org.complitex.flexbuh.document.service.DeclarationService;
import org.complitex.flexbuh.document.service.TemplateService;
import org.complitex.flexbuh.document.web.behavior.RestrictionBehavior;
import org.complitex.flexbuh.document.web.component.AddRowPanel;
import org.complitex.flexbuh.document.web.component.DeclarationTextField;
import org.complitex.flexbuh.document.web.model.DeclarationBooleanModel;
import org.complitex.flexbuh.document.web.model.DeclarationChoiceModel;
import org.complitex.flexbuh.document.web.model.DeclarationStringModel;
import org.complitex.flexbuh.util.ScriptUtil;
import org.complitex.flexbuh.util.StringUtil;
import org.complitex.flexbuh.util.XmlUtil;
import org.complitex.flexbuh.web.component.RadioSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.ejb.EJB;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.08.11 15:58
 */
public class DeclarationFormComponent extends Panel{
    private final static Logger log = LoggerFactory.getLogger(DeclarationFormComponent.class);

    private final static String WICKET_NAMESPACE_URI = "http://wicket.apache.org/dtds.data/wicket-xhtml1.4-strict.dtd";

    private Pattern LINKED_ID_PATTERN = Pattern.compile("(\\w*)\\.(\\w*)");

    private final static String ABC = "АБВГҐДЕЄЖЗИІЇЙКЛМНОПРСТУФХЦЧШЩЮЯ";

    @EJB
    private TemplateService templateService;

    @EJB
    private DeclarationService declarationService;

    private Map<String, RadioSet<String>> radioSetMap = new HashMap<String, RadioSet<String>>();

    private transient Declaration declaration;

    //todo check serialization
    private transient Document schema;
    private transient Document template;
    private transient Document commonTypes;

    private transient XPath schemaXPath = XmlUtil.newSchemaXPath();
    private transient XPath templateXPath = XmlUtil.newXPath();
    private transient ScriptEngine scriptEngine = ScriptUtil.newScriptEngine();

    private Map<String, Rule> rulesMap;
    private Map<String, DeclarationTextField> simpleTextFieldMap = new HashMap<>();
    private Map<String, List<DeclarationTextField>> multiRowTextFieldMap = new HashMap<>();
    private Map<String, DeclarationTextField> maskTextFieldMap = new HashMap<>();

    private transient Map<Integer, NodeList> allStretchTableMap = new HashMap<>();

    private WebMarkupContainer container;

    private transient StringResourceStream stringResourceStream;

    public DeclarationFormComponent(String id, Declaration declaration){
        super(id);

        try {
            String templateName = declaration.getTemplateName();

            //Template
            template = templateService.getDocumentXSL(templateName, new Declaration());

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

        //Auto fill header
        if (declaration.getId() == null) {
            declarationService.autoFillHeader(declaration);
        }
    }

    private void init(){
        //Body
        NodeList bodyNodeList = template.getElementsByTagName("body");
        if (bodyNodeList.getLength() < 1){
            bodyNodeList = template.getElementsByTagName("BODY");
        }

        Element panel = template.createElement("wicket:panel");
        panel.setAttribute("xmlns:wicket", WICKET_NAMESPACE_URI);

        Element div = (Element) template.renameNode(bodyNodeList.item(0), null, "div");
        div.setAttribute("wicket:id", "container");
        panel.appendChild(div);

        container = new WebMarkupContainer("container");
        add(container);

        //Input
        NodeList inputList = div.getElementsByTagName("input");
        for (int i=0; i < inputList.getLength(); ++i){
            Element input = (Element) inputList.item(i);

            if (XmlUtil.getParentById("StretchTable", input) == null){
                addInput(input);
            }
        }

        //MultiRows
        List<Element> tbodyElements = new ArrayList<>();

        NodeList stretchList = XmlUtil.getElementsById("StretchTable", div, templateXPath);

        int index = 0;

        for (int i=0; i < stretchList.getLength(); ++i){
            Element tbody = (Element) XmlUtil.getParent("tbody", stretchList.item(i));

            if (!tbodyElements.contains(tbody)) {
                tbodyElements.add(tbody);

                addRows(index++, tbody);
            }
        }

        //Markup
        stringResourceStream = new StringResourceStream(XmlUtil.getString(panel), "text/html");
        stringResourceStream.setCharset(Charset.forName("UTF-8"));
        stringResourceStream.setLastModified(Time.now());
    }

    @Override
    public Markup getAssociatedMarkup() {
        try {
            return new MarkupParser( new MarkupResourceStream(stringResourceStream)).parse();
        } catch (Exception e) {
            throw new WicketRuntimeException(e);
        }
    }

    private void addInput(Element inputElement){
        addInput(null, inputElement, container, true);
    }

    private void addInput(Integer rowNum, Element inputElement, MarkupContainer parent, boolean updateMarkup){
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
                    radio.setMarkupId(id + "_" + nextRandomId());
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

                //mask
                Element maskElement = (Element) XmlUtil.getParent("td", inputElement);
                String mask = maskElement != null ? maskElement.getAttribute("mask") : null;

                DeclarationStringModel model = new DeclarationStringModel(rowNum, id, schemaType, mask, declaration);
                final DeclarationTextField textField = new DeclarationTextField(id, model, schemaType);
                textField.setMarkupId(id + "_" + nextRandomId());
                textField.setOutputMarkupId(true);
                parent.add(textField);

                //multirow input
                String maxOccurs = schemaElement.getAttribute("maxOccurs");
                boolean multiRow = maxOccurs != null && !maxOccurs.isEmpty() && Integer.parseInt(maxOccurs) > 1;

                //Add to map for rule auto fill
                if (multiRow) {
                    addMultiRowTextField(id, textField);
                }else if (model.isMask()){
                    maskTextFieldMap.put(model.getMaskName(), textField);
                }else{
                    simpleTextFieldMap.put(id, textField);
                }

                //Ajax update
                textField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
                    @Override
                    protected void onError(AjaxRequestTarget target, RuntimeException e) {
                        //wtf
                    }

                    @Override
                    protected void onUpdate(final AjaxRequestTarget target) {
                        String value = textField.getValue();

                        if (value != null && !value.isEmpty()) {
                            //Auto fill
                            autoFill(target);

                            //Auto fill linked
                            getPage().visitChildren(DeclarationFormComponent.class, new IVisitor<DeclarationFormComponent, Void>() {

                                @Override
                                public void component(DeclarationFormComponent component, IVisit visit) {
                                    component.autoFill(target);

                                    visit.dontGoDeeper();
                                }
                            });
                        }

                        target.add(textField);
                    }
                });

                //Restriction
                Rule rule = rulesMap.get(model.isMask() ? model.getMaskName() : id);
                textField.add(createRestrictionBehavior(schemaType, rule != null ? rule.getDescription() : ""));
            }
        } catch (Exception e) {
            log.error("Ошибка добавления компонента формы ввода декларации", e);
        }
    }

    private void addMultiRowTextField(String id, DeclarationTextField textField){
        List<DeclarationTextField> textFields = multiRowTextFieldMap.get(id);

        if (textFields == null){
            textFields = new ArrayList<>();
            multiRowTextFieldMap.put(id, textFields);
        }

        textFields.add(textField);
    }

    private RestrictionBehavior createRestrictionBehavior(String schemaType, String title) throws XPathExpressionException {
        Element typeElement = XmlUtil.getElementByName(schemaType, commonTypes, schemaXPath);

        if (typeElement != null && "xs:complexType".equals(typeElement.getTagName())){
            Element extension = (Element) typeElement.getElementsByTagName("xs:extension").item(0);
            typeElement = XmlUtil.getElementByName(extension.getAttribute("base"), commonTypes, schemaXPath);
        }

        return new RestrictionBehavior(typeElement, getLocale(), title);
    }

    private void autoFill(AjaxRequestTarget target){
        for (Rule rule : rulesMap.values()){
            if ("SUMF".contains(rule.getExpression())){
                continue;
            }

            if ("Y".equals(rule.getRowNum())){
                List list =  multiRowTextFieldMap.get(rule.getCDocRowCId());

                if (list != null){
                    for (int i = 0; i < list.size(); ++i){
                        applyRule(i, rule, target);
                    }
                }
            }else {
                applyRule(-1, rule, target);
            }
        }
    }

    private void applyRule(int index, Rule rule, AjaxRequestTarget target){
        try {
            String value = rule.getExpression();

            if (value.contains("SUM")){
                double sum = 0;

                for (TextField<String> textField : multiRowTextFieldMap.get(rule.getExpressionIds().get(index > 0 ? index : 0))){
                    sum += (StringUtil.isDecimal(textField.getValue())) ? Double.parseDouble(textField.getValue()) : 0;
                }

                value = sum + "";
            }else{
                for (String id : rule.getExpressionIds()){
                    String input = "";

                    //linked document
                    if (id.contains(".")){
                        Matcher m = LINKED_ID_PATTERN.matcher(id);
                        if (m.matches()){
                            Declaration d = declaration.getParent() != null ? declaration.getParent() : declaration;
                            DeclarationValue dv = d.getLinkedDeclaration(m.group(1)).getDeclaration().getDeclarationValue(m.group(2));

                            if (dv != null){
                                input = dv.getValue();
                            }
                        }
                    }else{
                        if (index >= 0 && multiRowTextFieldMap.get(id) != null){
                            input = multiRowTextFieldMap.get(id).get(index).getValue();
                        } else if (maskTextFieldMap.get(id) != null){
                            input = maskTextFieldMap.get(id).getValue();
                        } else if (simpleTextFieldMap.get(id) != null){
                            input = simpleTextFieldMap.get(id).getValue();
                        }
                    }

                    //todo add validation checking
                    String v = (StringUtil.isDecimal(input)) ? input : "0";

                    value = StringUtil.replace(value, "^" + id, v);
                }

                value = value.replace("ABS", "Math.abs");
                value = StringEscapeUtils.unescapeXml(value);

                value = scriptEngine.eval(value).toString();
            }

            TextField<String> autoFill;

            if (index >= 0){
                autoFill = multiRowTextFieldMap.get(rule.getCDocRowCId()).get(index);
            }else{
                autoFill = simpleTextFieldMap.get(rule.getCDocRowCId());

                if (autoFill == null) {
                    autoFill = maskTextFieldMap.get(rule.getCDocRowCId());
                }
            }

            if (autoFill != null && !value.equals(autoFill.getValue())) {
                autoFill.setModelObject(value);
                target.add(autoFill);
                target.appendJavaScript("$('#" + autoFill.getMarkupId() + "').css('background-color', '#add8e6')");
            }
        } catch (ScriptException e) {
            log.error("Ошибка автозаполнения", e);
        }
    }

    private void addRows(final int index, Element tbody){
        //ajax container
        String tbodyId = "process_" + index;
        tbody.setAttribute("wicket:id", tbodyId);
        tbody.setAttribute("id", tbodyId + "_" + nextRandomId());

        WebMarkupContainer tbodyContainer = new WebMarkupContainer(tbodyId);
        tbodyContainer.setOutputMarkupId(true);
        container.add(tbodyContainer);

        //change parent for internal not dynamic
        NodeList componentList = XmlUtil.getElementsByAttribute("id", tbody, templateXPath);

        for (int i = 0, size = componentList.getLength(); i < size; ++i){
            Element element = (Element) componentList.item(i);
            String wicketId = element.getAttribute("wicket:id");

            if (wicketId != null && !wicketId.isEmpty()) {
                Component component = container.get(wicketId);

                if (component != null && !component.equals(tbodyContainer)){
                    tbodyContainer.add(component);
                }
            }
        }

        int addRow = !tbody.getAttribute("addRow").isEmpty() ? Integer.parseInt(tbody.getAttribute("addRow")) : 1;
        NodeList stretchTables = XmlUtil.getElementsById("StretchTable", tbody, templateXPath);

        allStretchTableMap.put(index, stretchTables);

        //repeater
        String repeaterId = "repeater_" + index;

        Element wrapContainer = template.createElement("wicket:container");
        wrapContainer.setAttribute("wicket:id", repeaterId);

        //multiRow
        int rows = stretchTables.getLength();
        for (int i =  rows - addRow; i < rows; ++i){
            wrapContainer.appendChild(stretchTables.item(i));
        }
        tbody.appendChild(wrapContainer);

        //add delete link
        Element firstRow = (Element) stretchTables.item(rows - addRow);
        NodeList tdList = firstRow.getElementsByTagName("td");
        if (tdList.getLength() == 0){
            tdList = firstRow.getElementsByTagName("TD");
        }
        Element firstColumn = (Element) tdList.item(0);

        Element wrapTableElement = template.createElement("table");
        Element trElement = template.createElement("tr");
        Element tdLeftElement = template.createElement("td");
        tdLeftElement.setAttribute("wicket:id", "add_row_panel");
        Element tdRightElement = template.createElement("td");

        trElement.appendChild(tdLeftElement);
        trElement.appendChild(tdRightElement);
        wrapTableElement.appendChild(trElement);

        NodeList childNodes = firstColumn.getChildNodes();
        while (childNodes.getLength() > 0){
            tdRightElement.appendChild(childNodes.item(0));
        }

        firstColumn.appendChild(wrapTableElement);

        //style
        wrapTableElement.setAttribute("style", "width: 100%;");
        wrapTableElement.setAttribute("border", "0");
        wrapTableElement.setAttribute("cellpadding", "0");
        wrapTableElement.setAttribute("cellspacing", "0");
        tdLeftElement.setAttribute("style", "width: 40px;");

        //Stretch rows
        final List<WebMarkupContainer> stretchRows = new ArrayList<>();

        //Add rows
        for (int i = 0, size = getRowCount(tbody.getElementsByTagName("input")); i <  size; ++i) {
            addRow(index, addRow, tbodyContainer, i, stretchRows);
        }

        //Repeater
        tbodyContainer.add(new AbstractRepeater(repeaterId) {
            @Override
            protected Iterator<? extends Component> renderIterator() {
                return stretchRows.iterator();
            }

            @Override
            protected void onPopulate() {
                //remove
                for (Component component : this) {
                    WebMarkupContainer c = (WebMarkupContainer) component;

                    if (!stretchRows.contains(c)) {
                        remove(c);
                    }
                }

                //add
                for (Component row : stretchRows) {
                    boolean found = false;

                    for (Component component : this) {
                        if (row.equals(component)) {
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        add(row);
                    }
                }

                //delete visible
                if (stretchRows.size() == 1) {
                    ((AddRowPanel) (stretchRows.get(0)).get("add_row_panel")).setDeleteVisible(false);
                }
            }
        });
    }

    private int getRowCount(NodeList inputList){
        int count = 1;

        for (int i = 0; i < inputList.getLength(); ++i){
            Element inputElement = (Element)inputList.item(i); 

            //mask
            Element maskElement = (Element) XmlUtil.getParent("td", inputElement);
            String mask = maskElement != null ? maskElement.getAttribute("mask") : null;

            //id
            String id = inputElement.getAttribute("id");
            
            int c = 0;

            if (mask != null && !mask.isEmpty()){
                c = declaration.getDeclarationValuesCountByMask(mask);
            } else if (id != null && !id.isEmpty()){
                c = declaration.getDeclarationValuesCount(id);
            }

            if (c > count){
                count = c;
            }
        }

        return count;
    }
    
    private Map<Integer, Integer> nextRowIdMap = new HashMap<>();
    
    private int nextRowId(int index){
        Integer nextId = nextRowIdMap.get(index);
        
        if (nextId == null){
            nextId = 1;            
        }
        
        nextRowIdMap.put(index, nextId + 1);
        
        return nextId;
    }

    private void addRow(final int index, final int addRow, final WebMarkupContainer tbodyContainer,
                        final int addRowIndex, final List<WebMarkupContainer> stretchRows) {
        NodeList allStretchRow = allStretchTableMap.get(index);  
        
        int rowNextId = nextRowId(index);

        final WebMarkupContainer stretchRow = new WebMarkupContainer(rowNextId + "");
        stretchRows.add(addRowIndex, stretchRow);

        final List<String> rowNumIds = new ArrayList<>();
        
        int rows = allStretchRow.getLength();        
        for (int j = rows - addRow; j < rows; ++j) {
            Element stretchTableElement = ((Element)allStretchRow.item(j));

            NodeList inputList = stretchTableElement.getElementsByTagName("input");

            for (int i = 0; i < inputList.getLength(); ++i){
                addInput(rowNextId, (Element) inputList.item(i), stretchRow, addRowIndex == 0);
            }

            //Row number
            NodeList spRownumList = XmlUtil.getElementsById("spRownum", stretchTableElement, templateXPath);

            for (int i = 0, size = spRownumList.getLength(); i < size; ++i) {
                Element spRownumElement = (Element) spRownumList.item(i);
                String rowNumId = "spRownum_" + index + "_" + j + "_" + i;
                rowNumIds.add(rowNumId);

                if (spRownumElement != null){
                    final boolean letter = spRownumElement.getAttribute("numType").equals("letter");

                    spRownumElement.setAttribute("wicket:id", rowNumId);

                    stretchRow.add(new Label(rowNumId, new LoadableDetachableModel<String>() {
                        @Override
                        protected String load() {
                            int index = stretchRows.indexOf(stretchRow);
                            
                            return letter ? ABC.substring(index, index + 1) : String.valueOf(index + 1);
                        }
                    }).setOutputMarkupId(true));
                }
            }
        }

        //reorder model
        reorder(stretchRows);

        //add row panel
        stretchRow.add(new AddRowPanel("add_row_panel") {
            @Override
            protected void onAdd(AjaxRequestTarget target) {
                setDeleteVisible(true);

                addRow(index, addRow, tbodyContainer, stretchRows.indexOf(stretchRow) + 1, stretchRows);
            }

            @Override
            protected void onDelete(AjaxRequestTarget target) {
                stretchRows.remove(stretchRow);
                DeclarationFormComponent.this.remove(stretchRow);
                reorder(stretchRows);

                autoFill(target);
            }

            @Override
            protected void afterAction(AjaxRequestTarget target) {
                target.add(tbodyContainer);

                //update row num
                for (WebMarkupContainer row : stretchRows){
                    for (String rowNumId : rowNumIds) {
                        Component c = row.get(rowNumId);

                        if (c != null){
                            target.add(c);
                        }
                    }
                }
            }
        });
    }

    private void reorder(List<WebMarkupContainer> rows){
        for (int i = 0, size = rows.size(); i < size; i++) {
            for (Component c : rows.get(i)) {
                if (c instanceof DeclarationTextField) {
                    DeclarationTextField textField = ((DeclarationTextField) c);
                    DeclarationStringModel model = textField.getDeclarationModel();

                    if (model.isMask()) {
                        maskTextFieldMap.remove(model.getMaskName());
                    }

                    model.updateRowNum(i + 1);

                    if (model.isMask()) {
                        maskTextFieldMap.put(model.getMaskName(), textField);
                    }
                }
            }
        }
    }

    private void remove(WebMarkupContainer row){
        row.visitChildren(DeclarationTextField.class, new IVisitor<DeclarationTextField, Void>() {
            @Override
            public void component(DeclarationTextField object, IVisit<Void> visit) {
                for (List list : multiRowTextFieldMap.values()) {
                    list.remove(object);
                }

                maskTextFieldMap.remove(object.getDeclarationModel().getMaskName());

                object.getDeclarationModel().removeValue();

                visit.dontGoDeeper();
            }
        });
    }
    
    private SecureRandom random = new SecureRandom();
    
    private String nextRandomId(){
        return random.nextInt() + "";
    }

}

