package org.complitex.flexbuh.common.util;

import sun.org.mozilla.javascript.internal.Context;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 30.08.11 15:15
 */
public class ScriptUtil {
    public static ScriptEngine newScriptEngine(){
        ScriptEngineManager factory = new ScriptEngineManager();

        return factory.getEngineByName("JavaScript");
    }

    public static Object evaluate(String s){
        Context context = Context.enter();
        try {
            return context.evaluateString(context.initStandardObjects(), s, "js", 0, null);
        } finally {
            Context.exit();
        }
    }
}
