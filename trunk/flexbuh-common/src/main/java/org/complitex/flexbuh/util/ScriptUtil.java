package org.complitex.flexbuh.util;

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
}
