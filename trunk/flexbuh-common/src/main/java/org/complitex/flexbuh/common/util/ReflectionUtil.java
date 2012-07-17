package org.complitex.flexbuh.common.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

    public static List<Field> getAllFields(Class _class){
        List<Field> list = new ArrayList<>();

        Class c = _class;

        while (!c.equals(Object.class)){
            list.addAll(0, Arrays.asList(c.getDeclaredFields()));

            c = c.getSuperclass();
        }

        return list;
    }
}
