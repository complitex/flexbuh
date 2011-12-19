package org.complitex.flexbuh.common.entity;

import org.complitex.flexbuh.common.annotation.Display;
import org.complitex.flexbuh.common.util.StringUtil;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 08.12.11 17:41
 */
public abstract class AbstractEntity implements Serializable{
    @Display(visible = false)
    private Long id;

    public String getTableName(){
        return StringUtil.underline(getClass().getSimpleName());
    }

    protected List<Field> getFields(){
        List<Field> fields = new ArrayList<>();

        for (Class _class = getClass(); !_class.equals(Object.class); _class = _class.getSuperclass()){
            Collections.addAll(fields, _class.getDeclaredFields());
        }

        return fields;
    }

    public Map<String, Object> getFieldMap(){
        Map<String, Object> map = new HashMap<>();

        try {
            for (Field field : getFields()){
                field.setAccessible(true);
                map.put(StringUtil.underline(field.getName()), field.get(this));
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return map;
    }

    public Map<String, Class> getFieldTypeMap(){
        Map<String, Class> map = new HashMap<>();

        for (Field field : getFields()){
            map.put(StringUtil.underline(field.getName()), field.getType());
        }

        return map;
    }
    
    public List<String> getFieldKeyList(boolean onlyVisible){
        List<OrderString> orderStrings = new ArrayList<>();

        for (Field field : getFields()){
            Display display = field.getAnnotation(Display.class);

            if (display == null || !onlyVisible || display.visible()) {
                int order = display != null ? display.order() : 1000;
                orderStrings.add(new OrderString(order, StringUtil.underline(field.getName())));
            }
        }

        Collections.sort(orderStrings);

        List<String> list = new ArrayList<>();

        for (OrderString os : orderStrings){
            list.add(os.getString());
        }

        return list;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
