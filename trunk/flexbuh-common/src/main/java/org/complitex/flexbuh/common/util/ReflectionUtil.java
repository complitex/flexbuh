package org.complitex.flexbuh.common.util;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 17.05.12 17:48
 */
public class ReflectionUtil {
    public static void emptyOnNull(Object object) throws IllegalAccessException {
        Field[] fields = object.getClass().getDeclaredFields();

        for (Field field : fields){
            field.setAccessible(true);

            if (field.get(object) == null) {
                if (Integer.class.equals(field.getType())){
                    field.set(object, 0);
                }else if (String.class.equals(field.getType())){
                    field.set(object, "");
                }else if (Date.class.equals(field.getType())){
                    field.set(object, new Date(0));
                }
            }
        }
    }
}
