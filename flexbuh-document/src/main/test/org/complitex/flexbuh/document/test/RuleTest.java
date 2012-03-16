package org.complitex.flexbuh.document.test;

import org.complitex.flexbuh.common.util.ScriptUtil;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.entity.DeclarationValue;
import org.complitex.flexbuh.document.entity.Rule;
import org.complitex.flexbuh.document.service.RuleService;
import sun.org.mozilla.javascript.internal.Context;

import javax.script.ScriptException;
import java.util.regex.Matcher;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 22.02.12 15:00
 */
public class RuleTest {
    public static void main(String... args) throws ScriptException {
        Context context = Context.enter();
        try {
            Object o = context.evaluateString(context.initStandardObjects(), "1+1", "js", 0, null);

            System.out.println(o);
        } finally {
            Context.exit();
        }
    }

    public static void simple() throws ScriptException {
        Object o = ScriptUtil.newScriptEngine().eval("1+1==2");

        System.out.println(o.toString());
    }

    public static void check(){
        RuleService ruleService = new RuleService();

        Rule rule = new Rule();
        rule.setCDocRowC("^R2DG5");
        rule.setSign("=");
        rule.setExpression("SUM(^T2RXXXXG5) + SUM(^T1RXXXXG7)");

        Declaration declaration = new Declaration();
        declaration.addDeclarationValue(new DeclarationValue(1, "T2RXXXXG5", "10"));
        declaration.addDeclarationValue(new DeclarationValue(2, "T2RXXXXG5", "15"));
        declaration.addDeclarationValue(new DeclarationValue(3, "T2RXXXXG5", "3"));
        declaration.addDeclarationValue(new DeclarationValue(1, "T1RXXXXG7", "11"));

        String x = ruleService.getExpression(0, declaration, rule);

        System.out.println(x);
    }

    public static void sum(){
        String sum = "SUM(^T1RXXXXG7)+SUM(^T2RXXXXG7)+SUM(^T3RXXXXG7)+SUM(^T4RXXXXG7)+SUM(^T5RXXXXG7)";
        String sum0 = "HELLO";

        Matcher sumMatcher = RuleService.SUM_PATTERN.matcher(sum0);

        while (sumMatcher.find()){
            System.out.println(sumMatcher.group(1));
        }
    }
}
