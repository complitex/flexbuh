package org.complitex.flexbuh.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 30.08.11 17:19
 */
public class StringUtil {
    public static boolean isDecimal(String s){
        return s != null && s.matches("^-?[0-9]+(\\.[0-9]+)?$");
    }

    public static String getString(Object object){
        return object != null ? object.toString() : "";
    }

    public static String notNull(String s){
        return s != null ? s : "";
    }

    public static String replace(String s, CharSequence target, CharSequence replacement) {
        return Pattern.compile(target.toString(), Pattern.LITERAL).matcher(
                s).replaceFirst(Matcher.quoteReplacement(replacement.toString()));
    }

    public static String underline(String s){
        String underline = "";

        char[] chars = s.toCharArray();

        for (int i = 0; i < chars.length; ++i){
            if (i > 0 && Character.isUpperCase(chars[i])){
                underline += "_";
            }

            underline += Character.toLowerCase(chars[i]);
        }

        return underline;
    }

    public static double getDouble(String s){
        return isDecimal(s) ? Double.parseDouble(s) : 0;
    }
}
