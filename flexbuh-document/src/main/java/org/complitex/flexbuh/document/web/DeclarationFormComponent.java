package org.complitex.flexbuh.document.web;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.Markup;
import org.apache.wicket.markup.MarkupElement;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.entity.DeclarationValue;
import org.complitex.flexbuh.document.entity.Rule;
import org.complitex.flexbuh.document.service.DeclarationFillService;
import org.complitex.flexbuh.document.service.DeclarationMarkupService;
import org.complitex.flexbuh.document.service.TemplateService;
import org.complitex.flexbuh.document.web.behavior.RestrictionBehavior;
import org.complitex.flexbuh.document.web.component.AddRowPanel;
import org.complitex.flexbuh.document.web.component.DeclarationTextField;
import org.complitex.flexbuh.document.web.component.RowNumLabel;
import org.complitex.flexbuh.document.web.component.StretchTable;
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

import javax.ejb.EJB;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.08.11 15:58
 */
public class DeclarationFormComponent extends Panel{
    private final static Logger log = LoggerFactory.getLogger(DeclarationFormComponent.class);

    private Pattern LINKED_ID_PATTERN = Pattern.compile("(\\w*)\\.(\\w*)");

    @EJB
    private TemplateService templateService;

    @EJB
    private DeclarationFillService declarationService;

    @EJB
    private DeclarationMarkupService declarationMarkupService;

    private transient Declaration declaration;

    //todo check serialization    
    private transient Document commonTypes;

    private transient XPath schemaXPath = XmlUtil.newSchemaXPath();
    private transient ScriptEngine scriptEngine = ScriptUtil.newScriptEngine();

    private Map<String, Rule> rulesMap;
    private Map<String, DeclarationTextField> simpleTextFieldMap = new HashMap<>();
    private Map<String, List<DeclarationTextField>> multiRowTextFieldMap = new HashMap<>();
    private Map<String, DeclarationTextField> maskTextFieldMap = new HashMap<>();

    private final String templateName;

    private int nextId = 1;

    public DeclarationFormComponent(String id, Declaration declaration){
        super(id);

        templateName = declaration.getTemplateName();

        try {
            //Common Types
            commonTypes = templateService.getSchema("common_types");

            //Rules
            rulesMap = templateService.getRules(templateName);

            //Declaration
            this.declaration = declaration;

            init();

            //Auto fill header
            if (declaration.getId() == null) {
                declarationService.autoFillHeader(declaration);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void init(){
        WebMarkupContainer container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        add(container);

        RadioSet<String> radioSet = null;
        WebMarkupContainer tbody = null;
        WebMarkupContainer parent = container;
        StretchTable stretchTable = null;

        for (MarkupElement markupElement : getAssociatedMarkup()){
            if (markupElement instanceof ComponentTag){
                ComponentTag tag = (ComponentTag) markupElement;

                String type = tag.getAttribute("type");
                String schema = tag.getAttribute("schema");
                String wicketId = tag.getAttribute("wicket:id");
                String mask = tag.getAttribute("mask");

                if (type != null){
                    if ("radio".equals(type) && radioSet != null){
                        Radio<String> radio = new Radio<>(wicketId, new Model<>(wicketId), radioSet);
                        radio.setOutputMarkupId(true);
                        radioSet.addRadio(radio);

                        parent.add(radio);
                    }

                    if ("checkbox".equals(type)){
                        CheckBox checkBox = new CheckBox(wicketId, new DeclarationBooleanModel(wicketId, declaration));
                        checkBox.setOutputMarkupId(true);

                        parent.add(checkBox);
                    }

                    if ("text".equals(type)){
                        boolean stretchRow = wicketId.contains("XXXX") || (mask != null && !mask.isEmpty());

                        addTextInput(stretchRow ? 1 : null, parent, schema, wicketId, mask);
                    }
                }

                if (wicketId != null){
                    if (wicketId.contains("radio_set")){
                        radioSet = new RadioSet<>(wicketId, new DeclarationChoiceModel(wicketId, declaration));
                        parent.add(radioSet);
                    }

                    if (wicketId.contains("process")){
                        tbody = new WebMarkupContainer(wicketId);
                        tbody.setOutputMarkupId(true);

                        container.add(tbody);

                        parent = tbody;
                    }

                    if (wicketId.contains("repeater")){
                        stretchTable = new StretchTable(wicketId);
                        parent.add(stretchTable);

                        parent = stretchTable.getFirstStretchRow();

                        addAddRowPanel(parent, stretchTable);
                    }

                    if (wicketId.contains("spRownum") && stretchTable != null){
                        String numType = tag.getAttribute("numType");
                        boolean letter = numType != null && numType.equals("letter");

                        parent.add(new RowNumLabel(wicketId, letter, parent, stretchTable));
                    }
                }

                if (tag.isClose()){
                    String openWicketId = tag.getOpenTag().getAttribute("wicket:id");

                    if (openWicketId != null) {
                        if (openWicketId.contains("process")){
                            parent = container;
                        }

                        if (openWicketId.contains("repeater") && tbody != null){
                            parent = tbody;

                            if (declaration.getId() != null) {
                                for (int i = 0, count = getRowCount(stretchTable.getFirstStretchRow()) - 1; i < count; ++i){
                                    addRow(i + 2, stretchTable, null);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void addTextInput(Integer rowNum, WebMarkupContainer container, String schema, String id, String mask) {
        DeclarationStringModel model = new DeclarationStringModel(rowNum, id, schema, mask, declaration);

        final DeclarationTextField textField = new DeclarationTextField(id, model, schema);
        textField.setOutputMarkupId(true);
        container.add(textField);

        //add maps
        if (id.contains("XXXX")){
            List<DeclarationTextField> textFields = multiRowTextFieldMap.get(id);

            if (textFields == null){
                textFields = new ArrayList<>();
                multiRowTextFieldMap.put(id, textFields);
            }

            textFields.add(textField);
        }else if (!mask.isEmpty()){
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
        try {
            Rule rule = rulesMap.get(model.isMask() ? model.getMaskName() : id);
            textField.add(createRestrictionBehavior(schema, rule != null ? rule.getDescription() : ""));
        } catch (XPathExpressionException e) {
            log.error("Ошибка создания проверки данных",e);
        }
    }

    private void addRow(final int rowNum, final StretchTable stretchTable, WebMarkupContainer afterRow){
        final WebMarkupContainer newRow = stretchTable.insertAfter(afterRow);
        
        stretchTable.getFirstStretchRow().visitChildren(new IVisitor<Component, Object>() {
            @Override
            public void component(Component object, IVisit<Object> visit) {
                if (object instanceof RowNumLabel){
                    RowNumLabel rowNumLabel = (RowNumLabel) object;

                    newRow.add(new RowNumLabel(rowNumLabel.getId(), rowNumLabel.isLetter(), newRow, stretchTable));

                    visit.dontGoDeeper();
                }else if (object instanceof AddRowPanel){
                    addAddRowPanel(newRow, stretchTable);

                    visit.dontGoDeeper();
                }else if (object instanceof DeclarationTextField){
                    DeclarationTextField textField = (DeclarationTextField) object;

                    addTextInput(rowNum, newRow, textField.getSchema(), textField.getId(), textField.getMask());

                    visit.dontGoDeeper();
                }
            }
        });
    }

    private void addAddRowPanel(WebMarkupContainer parent, StretchTable stretchTable){
        parent.add(new AddRowPanel("add_row_panel", parent, stretchTable) {
            @Override
            protected void onAdd(AjaxRequestTarget target) {
                addRow(++nextId, getStretchTable(), getRow());

                reorder(getStretchTable().getStretchRows());
            }

            @Override
            protected void onDelete(AjaxRequestTarget target) {
                getRow().visitChildren(DeclarationTextField.class, new IVisitor<DeclarationTextField, Void>() {
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

                reorder(getStretchTable().getStretchRows());

                autoFill(target);
            }

            @Override
            protected void afterAction(final AjaxRequestTarget target) {
                target.add(getStretchTable().getParent());

                //update row num
                for (WebMarkupContainer row : getStretchTable().getStretchRows()){
                    row.visitChildren(RowNumLabel.class, new IVisitor<Label, Object>() {
                        @Override
                        public void component(Label object, IVisit<Object> visit) {
                            target.add(object);

                            visit.dontGoDeeper();
                        }
                    });
                }
            }
        });
    }

    @Override
    public Markup getAssociatedMarkup() {
        return declarationMarkupService.getMarkup(templateName);
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

    private int getRowCount(WebMarkupContainer firstStretchRow){
        int count = 0;

        for (Component component : firstStretchRow){
            if (component instanceof DeclarationTextField){
                DeclarationTextField textField = (DeclarationTextField) component;

                String mask = textField.getMask();
                String id = textField.getId();

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
        }

        return count;
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
}

