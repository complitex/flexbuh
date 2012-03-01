package org.complitex.flexbuh.document.service;

import org.apache.commons.lang.StringEscapeUtils;
import org.complitex.flexbuh.common.util.ScriptUtil;
import org.complitex.flexbuh.common.util.StringUtil;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.entity.DeclarationValue;
import org.complitex.flexbuh.document.entity.LinkedDeclaration;
import org.complitex.flexbuh.document.entity.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 21.02.12 16:00
 */
@Stateless
public class RuleService {
    private final static Logger log = LoggerFactory.getLogger(RuleService.class);

    public final static Pattern VALUE_PATTERN = Pattern.compile("\\^(\\w*\\.?\\w*)");

    public final static Pattern LINKED_PATTERN = Pattern.compile("\\^(\\w*)\\.(\\w*)");

    public final static Pattern SUM_PATTERN = Pattern.compile("SUM\\(\\^(\\w*)\\)");

    @EJB
    private TemplateService templateService;

    public Map<String, Rule> getRules(String templateName) {
        Map<String, Rule> rules = new LinkedHashMap<>();

        try {
            JAXBContext context = JAXBContext.newInstance(Rule.class);

            Unmarshaller unmarshaller = context.createUnmarshaller();

            Document controls = templateService.getTemplateControlDocument(templateName);

            NodeList ruleNodeList = controls.getElementsByTagName("rule");

            for (int i = 0; i < ruleNodeList.getLength(); ++i){
                Rule rule = (Rule) unmarshaller.unmarshal(ruleNodeList.item(i));

                rules.put(rule.getCDocRowC().replace("^", ""), rule);
            }
        } catch (Exception e) {
            log.error("Ошибка получения правила", e);
        }

        return rules;
    }

    public void check(Declaration declaration){
        Map<String, Rule> rules = getRules(declaration.getTemplateName());

        ScriptEngine scriptEngine = ScriptUtil.newScriptEngine();

        declaration.setChecked(true);

        for (Rule rule : rules.values()){
            //todo implement
            if (rule.getExpression().contains("GetPdvType")){
                continue;
            }

            String name = rule.getCDocRowC().replace("^","");

            if ("Y".equals(rule.getRowNum())){
                for (DeclarationValue declarationValue : declaration.getDeclarationValues()){
                    if (name.equals(declarationValue.getName())){
                        if (!check(declarationValue.getRowNum(), declaration, rule, scriptEngine)){
                            declaration.setChecked(false);
                            declaration.setCheckMessage(rule.getDescription());

                            return;
                        }
                    }
                }
            }else {
                if (check(null, declaration, rule, scriptEngine)){
                    declaration.setChecked(false);
                    declaration.setCheckMessage(rule.getDescription());

                    return;
                }
            }
        }
    }

    public boolean check(Integer rowNum, Declaration declaration, Rule rule, ScriptEngine scriptEngine){
        String expr = declaration.getDeclarationValue(rowNum, rule.getCDocRowC().replace("^", "")).getValue()
                + rule.getSign().replace("=", "==")
                + getExpression(rowNum, declaration, rule);

        try {
            return !(Boolean)scriptEngine.eval(expr);
        } catch (ScriptException e) {
            log.error("Ошибка выполнения скрипта", e);
        }

        return false;
    }

    public String getExpression(Integer rowNum, Declaration declaration, Rule rule){
        String expression = rule.getExpression();

        //SUM
        Matcher sumMatcher = SUM_PATTERN.matcher(expression);

        while (sumMatcher.find()){
            String expr = sumMatcher.group(0);
            String name = sumMatcher.group(1);

            double value = 0;

            for (DeclarationValue declarationValue : declaration.getDeclarationValues()){
                if (name.equals(declarationValue.getName())){
                    value += StringUtil.getDouble(declarationValue.getValue());
                }
            }

            expression = expression.replace(expr, value + "");
        }

        //Linked
        Matcher linkedMather = LINKED_PATTERN.matcher(expression);

        while (linkedMather.find()){
            String expr = linkedMather.group(0);
            String id = linkedMather.group(1);
            String name = linkedMather.group(2);

            LinkedDeclaration linkedDeclaration = declaration.getParent() != null
                    ? declaration.getParent().getLinkedDeclaration(id)
                    : declaration.getLinkedDeclaration(id);

            String value = "0";

            if (linkedDeclaration != null && linkedDeclaration.getDeclaration().getDeclarationValue(name) != null){
                value = linkedDeclaration.getDeclaration().getDeclarationValue(name).getValue();
            }

            expression = expression.replace(expr, StringUtil.isDecimal(value) ? value : "0");
        }

        //Value
        Matcher valueMatcher = VALUE_PATTERN.matcher(expression);

        while (valueMatcher.find()){
            String expr = valueMatcher.group(0);
            String name = valueMatcher.group(1);

            DeclarationValue declarationValue = declaration.getDeclarationValue(rowNum, name);

            String value = declarationValue != null ? declarationValue.getValue() : "0";

            expression = expression.replace(expr, StringUtil.isDecimal(value) ? value : "0");
        }

        //ABS
        expression = expression.replace("ABS", "Math.abs");

        //Unescape
        expression = StringEscapeUtils.unescapeXml(expression);

        return expression;
    }
}

