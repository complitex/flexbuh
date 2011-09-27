package org.complitex.flexbuh.util;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 30.08.11 17:19
 */
public class StringUtil {
    public static boolean isDecimal(String s){
        return s != null && s.matches("^-?[0-9]+(\\.[0-9]+)?$");
    }

    public static String getString(Integer num){
        return num != null ? num.toString() : "";
    }
}
