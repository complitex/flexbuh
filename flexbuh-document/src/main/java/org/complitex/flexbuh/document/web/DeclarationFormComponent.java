package org.complitex.flexbuh.document.web;

import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.Markup;
import org.apache.wicket.markup.MarkupElement;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.complitex.flexbuh.common.entity.dictionary.FieldCode;
import org.complitex.flexbuh.common.util.ScriptUtil;
import org.complitex.flexbuh.common.util.XmlUtil;
import org.complitex.flexbuh.common.web.component.RadioSet;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.entity.DeclarationValue;
import org.complitex.flexbuh.document.entity.Rule;
import org.complitex.flexbuh.document.exception.CreateDocumentException;
import org.complitex.flexbuh.document.service.DeclarationFillService;
import org.complitex.flexbuh.document.service.DeclarationMarkupService;
import org.complitex.flexbuh.document.service.RuleService;
import org.complitex.flexbuh.document.service.TemplateService;
import org.complitex.flexbuh.document.web.behavior.RestrictionBehavior;
import org.complitex.flexbuh.document.web.component.*;
import org.complitex.flexbuh.document.web.model.DeclarationBooleanModel;
import org.complitex.flexbuh.document.web.model.DeclarationChoiceModel;
import org.complitex.flexbuh.document.web.model.DeclarationStringModel;
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

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.08.11 15:58
 */
public class DeclarationFormComponent extends Panel{
    private final static Logger log = LoggerFactory.getLogger(DeclarationFormComponent.class);

    @EJB
    private TemplateService templateService;

    @EJB
    private DeclarationFillService declarationService;

    @EJB
    private DeclarationMarkupService declarationMarkupService;

    @EJB
    private RuleService ruleService;

    private Declaration declaration;

    //Loadable
    private transient Document commonTypes;
    private transient XPath schemaXPath;
    private transient ScriptEngine scriptEngine;

    private Map<String, Rule> rulesMap;
    private Map<String, IDeclarationStringComponent> simpleTextFieldMap = new HashMap<>();
    private Map<String, List<IDeclarationStringComponent>> multiRowTextFieldMap = new HashMap<>();
    private Map<String, IDeclarationStringComponent> maskTextFieldMap = new HashMap<>();

    private final String templateName;

    private int nextId = 1;

    private CounterpartDialog counterpartDialog = null;
    private EmployeeDialog employeeDialog = null;

    public DeclarationFormComponent(String id, Declaration declaration){
        super(id);

        //Declaration
        this.declaration = declaration;

        templateName = declaration.getTemplateName();

        try {
            //Rules
            rulesMap = ruleService.getRules(templateName);

            init();

            //Auto fill header
            if (declaration.getId() == null) {
                declarationService.autoFillHeader(declaration);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Document getCommonTypes(){
        if (commonTypes == null){
            try {
                commonTypes = templateService.getTemplateXSDDocument("common_types");
            } catch (CreateDocumentException e) {
                throw new WicketRuntimeException(e);
            }
        }

        return commonTypes;
    }

    public XPath getSchemaXPath(){
        if (schemaXPath == null){
            schemaXPath = XmlUtil.newSchemaXPath();
        }

        return schemaXPath;
    }

    public ScriptEngine getScriptEngine(){
        if (scriptEngine == null){
            scriptEngine = ScriptUtil.newScriptEngine();
        }

        return scriptEngine;
    }

    private void init(){
        WebMarkupContainer container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        add(container);

        RadioSet<String> radioSet = null;
        WebMarkupContainer tbody = null;
        WebMarkupContainer parent = container;
        StretchTable stretchTable = null;

        Markup markup = getAssociatedMarkup();

        for (MarkupElement markupElement : markup){
            if (markupElement instanceof ComponentTag){
                ComponentTag tag = (ComponentTag) markupElement;

                String type = tag.getAttribute("type");
                String schema = tag.getAttribute("schema");
                String wicketId = tag.getAttribute("wicket:id");
                String mask = tag.getAttribute("mask");
                String field = tag.getAttribute("field");

                if (type != null){
                    if ("text".equals(type)){
                        boolean stretchRow = wicketId.contains("XXXX") || (mask != null && !mask.isEmpty());

                        addTextInput(stretchRow ? 1 : null, parent, schema, wicketId, mask, field);
                    }else if ("radio".equals(type) && radioSet != null){
                        Radio<String> radio = new Radio<>(wicketId, new Model<>(wicketId), radioSet);
                        radio.setOutputMarkupId(true);
                        radioSet.addRadio(radio);

                        DeclarationValue declarationValue = declaration.getDeclarationValue(wicketId);

                        if (declarationValue != null && "1".equals(declarationValue.getValue())){
                            radioSet.setModelObject(wicketId);
                        }

                        parent.add(radio);
                    }else if ("checkbox".equals(type)){
                        CheckBox checkBox = new CheckBox(wicketId, new DeclarationBooleanModel(wicketId, declaration));
                        checkBox.setOutputMarkupId(true);

                        parent.add(checkBox);
                    }                    
                }

                if (wicketId != null){
                    if (wicketId.contains("radio_set")){
                        radioSet = new RadioSet<>(wicketId, new DeclarationChoiceModel(null, declaration));
                        parent.add(radioSet);
                    }else if (wicketId.contains("process")){
                        tbody = new WebMarkupContainer(wicketId);
                        tbody.setOutputMarkupId(true);

                        container.add(tbody);

                        parent = tbody;
                    }else if (wicketId.contains("repeater")){
                        stretchTable = new StretchTable(wicketId);
                        parent.add(stretchTable);

                        parent = stretchTable.getFirstStretchRow();

                        addAddRowPanel(parent, stretchTable);
                    }else if (wicketId.contains("spRownum") && stretchTable != null){
                        String numType = tag.getAttribute("numType");
                        boolean letter = numType != null && numType.equals("letter");

                        parent.add(new RowNumLabel(wicketId, letter, parent, stretchTable));
                    }else if (wicketId.contains("dialog")){
                        switch (wicketId.replace("dialog_", "")){
                            case FieldCode.COUNTERPART_SPR_NAME:
                                counterpartDialog = new CounterpartDialog(wicketId, declaration.getSessionId());
                                container.add(counterpartDialog);
                                break;
                            case FieldCode.EMPLOYEE_SPR_NAME:
                                employeeDialog = new EmployeeDialog(wicketId, declaration.getSessionId());
                                container.add(employeeDialog);
                                break;
                            
                            default:
                                container.add(new EmptyPanel(wicketId));
                        }
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

    private void addTextInput(Integer rowNum, WebMarkupContainer container, String schema, String id, String mask, String field) {
        DeclarationStringModel model = new DeclarationStringModel(rowNum, id, schema, mask, field, declaration);

        IDeclarationStringComponent declarationStringComponent = null;
        RestrictionBehavior restrictionBehavior = null;

        //Rule
        Rule rule = rulesMap.get(model.isMask() ? model.getMaskName() : id);

        //Restriction
        try {
            restrictionBehavior = createRestrictionBehavior(schema, rule != null ? rule.getDescription() : "");
        } catch (XPathExpressionException e) {
            log.error("Ошибка создания проверки данных", e);
        }

        if (field == null || field.isEmpty()){
            final DeclarationTextField textField = new DeclarationTextField(id, model);
            textField.setOutputMarkupId(true);
            container.add(textField);

            //auto fill field
            if (rule != null && "=".equals(rule.getSign())){
                textField.setEnabled(false);

                if (restrictionBehavior != null) {
                    restrictionBehavior.setDefaultColor(RestrictionBehavior.COLOR_AUTO_FILL);
                    restrictionBehavior.setValidatedColor(RestrictionBehavior.COLOR_AUTO_FILL);
                }
            }

            //restriction
            textField.add(restrictionBehavior);

            declarationStringComponent = textField;

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

        } else if (field.equals(FieldCode.COUNTERPART_SPR_NAME)) {
            CounterpartAutocompleteDialog component = new CounterpartAutocompleteDialog(id, model, declaration.getSessionId(), counterpartDialog);
            container.add(component);

            //restriction
            component.getAutocompleteField().add(restrictionBehavior);

            declarationStringComponent = component;
        } else if (field.equals(FieldCode.EMPLOYEE_SPR_NAME)) {
            EmployeeAutocompleteDialog component = new EmployeeAutocompleteDialog(id, model, declaration.getSessionId(), employeeDialog);
            container.add(component);

            //restriction
            component.getAutocompleteField().add(restrictionBehavior);

            declarationStringComponent = component;
        }

        if (declarationStringComponent != null) {
            //add maps
            if (id.contains("XXXX")) {
                List<IDeclarationStringComponent> textFields = multiRowTextFieldMap.get(id);

                if (textFields == null) {
                    textFields = new ArrayList<>();
                    multiRowTextFieldMap.put(id, textFields);
                }

                textFields.add(declarationStringComponent);
            } else if (!mask.isEmpty()) {
                maskTextFieldMap.put(model.getMaskName(), declarationStringComponent);
            } else {
                simpleTextFieldMap.put(id, declarationStringComponent);
            }
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
                }else if (object instanceof IDeclarationStringComponent){
                    DeclarationStringModel model = ((IDeclarationStringComponent) object).getDeclarationModel();

                    addTextInput(rowNum, newRow, model.getType(), model.getName(), model.getMask(), model.getField());

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
                getRow().visitChildren(IDeclarationStringComponent.class, new IVisitor<Component, Void>() {
                    @Override
                    public void component(Component object, IVisit<Void> visit) {
                        IDeclarationStringComponent declarationStringComponent = (IDeclarationStringComponent) object;

                        for (List list : multiRowTextFieldMap.values()) {
                            list.remove(object);
                        }

                        maskTextFieldMap.remove(declarationStringComponent.getDeclarationModel().getMaskName());

                        declarationStringComponent.getDeclarationModel().removeValue();

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
        Element typeElement = XmlUtil.getElementByName(schemaType, getCommonTypes(), getSchemaXPath());

        if (typeElement != null && "xs:complexType".equals(typeElement.getTagName())){
            Element extension = (Element) typeElement.getElementsByTagName("xs:extension").item(0);
            typeElement = XmlUtil.getElementByName(extension.getAttribute("base"), getCommonTypes(), getSchemaXPath());
        }

        return new RestrictionBehavior(typeElement, getLocale(), title);
    }

    private void autoFill(AjaxRequestTarget target){
        for (Rule rule : rulesMap.values()){
            if (!"=".equals(rule.getSign()) || "SUMF".contains(rule.getExpression())){
                continue;
            }

            if ("Y".equals(rule.getRowNum())){
                List list =  multiRowTextFieldMap.get(rule.getCDocRowC().replace("^",""));

                if (list != null){
                    for (int i = 0; i < list.size(); ++i){
                        applyRule(i, rule, target);
                    }
                }
            }else {
                applyRule(null, rule, target);
            }
        }
    }

    private void applyRule(Integer rowNum, Rule rule, AjaxRequestTarget target){
        try {
            //Evaluate script
            String value = getScriptEngine().eval(ruleService.getExpression(rowNum, declaration, rule)).toString();

            String name = rule.getCDocRowC().replace("^","");

            IDeclarationStringComponent autoFill;

            if (rowNum != null){
                autoFill = multiRowTextFieldMap.get(name).get(rowNum);
            }else{
                autoFill = simpleTextFieldMap.get(name);

                if (autoFill == null) {
                    autoFill = maskTextFieldMap.get(name);
                }
            }

            if (autoFill != null && !value.equals(autoFill.getValue())) {
                autoFill.getDeclarationModel().setObject(value);
                target.add((Component)autoFill);
            }
        } catch (ScriptException e) {
            log.error("Ошибка автозаполнения", e);
        }
    }

    private int getRowCount(WebMarkupContainer firstStretchRow){
        int count = 0;

        for (Component component : firstStretchRow){
            if (component instanceof IDeclarationStringComponent){
                DeclarationStringModel model = ((IDeclarationStringComponent) component).getDeclarationModel();

                String mask = model.getMask();
                String id = model.getName();

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
                if (c instanceof IDeclarationStringComponent) {
                    IDeclarationStringComponent component = ((IDeclarationStringComponent) c);
                    DeclarationStringModel model = component.getDeclarationModel();

                    if (model.isMask()) {
                        maskTextFieldMap.remove(model.getMaskName());
                    }

                    model.updateRowNum(i + 1);

                    if (model.isMask()) {
                        maskTextFieldMap.put(model.getMaskName(), component);
                    }
                }
            }
        }
    }
}

