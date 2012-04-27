package org.complitex.flexbuh.common.logging;

import org.complitex.flexbuh.common.entity.ILongId;
import org.complitex.flexbuh.common.util.XmlUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static org.complitex.flexbuh.common.logging.EventKey.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 25.04.12 17:18
 */
public class Event implements Serializable {
    private Map<EventKey, String> map = new HashMap<>();

    public Event(EventCategory category, ILongId oldObject, ILongId newObject){
        map.put(CATEGORY, category.name());

        ILongId object = newObject != null ? newObject : oldObject != null ? oldObject : null;

        if (object != null) {
            map.put(MODEL_CLASS, object.getClass().getName());
            map.put(OBJECT_ID, String.valueOf(object.getId()));
            map.put(NEW_OBJECT, XmlUtil.getXStream().toXML(object));
        }

        if (oldObject != null) {
            map.put(OLD_OBJECT, XmlUtil.getXStream().toXML(oldObject));
        }

        if (newObject != null){
            map.put(NEW_OBJECT, XmlUtil.getXStream().toXML(newObject));
        }
    }

    public Event(EventCategory category, ILongId newObject){
        this(category, null, newObject);
    }

    public Map<String, String> getMap() {
        Map<String, String> m = new HashMap<>();

        for (EventKey eventKey : map.keySet()){
            m.put(eventKey.name(), map.get(eventKey));
        }

        return m;
    }
}
