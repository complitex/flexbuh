package org.complitex.flexbuh.common.util;

import java.lang.reflect.Field;
import java.util.*;

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
        Set<String> names = new HashSet<>();

        Class c = _class;

        while (!c.equals(Object.class)){
            Field[] fields = c.getDeclaredFields();
            for (int i = fields.length - 1; i >= 0; --i) {
                Field field = fields[i];

                if (!names.contains(field.getName())) {
                    list.add(0, field);
                    names.add(field.getName());
                }
            }

            c = c.getSuperclass();
        }

        return list;
    }
}
