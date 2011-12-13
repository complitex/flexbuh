package org.complitex.flexbuh.common.entity;

import org.complitex.flexbuh.common.util.StringUtil;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 08.12.11 17:41
 */
public abstract class AbstractEntity implements Serializable{
    private Long id;

    public String getTableName(){
        return StringUtil.underline(getClass().getSimpleName());
    }

    public Map<String, Object> getFieldMap(){
        Map<String, Object> map = new HashMap<>();

        List<Field> fields = new ArrayList<>();

        for (Class _class = getClass(); !_class.equals(Object.class); _class = _class.getSuperclass()){
            Collections.addAll(fields, _class.getDeclaredFields());
        }

        try {
            for (Field field : fields){
                field.setAccessible(true);
                map.put(StringUtil.underline(field.getName()), field.get(this));
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return map;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
