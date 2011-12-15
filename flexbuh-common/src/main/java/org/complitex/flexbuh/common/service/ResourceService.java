package org.complitex.flexbuh.common.service;

import javax.ejb.Singleton;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.12.11 17:38
 */
@Singleton
public class ResourceService {
    private Map<Class, ResourceBundle> resourceBundleMap = new ConcurrentHashMap<>();
    
    public String getString(Class _class, String key){
        ResourceBundle resourceBundle = resourceBundleMap.get(_class);

        if (resourceBundle == null){
            resourceBundle = ResourceBundle.getBundle(_class.getName());

            resourceBundleMap.put(_class, resourceBundle);
        }

        try {
            return resourceBundle.getString(key);
        } catch (MissingResourceException e) {
            if (!_class.getSuperclass().equals(Object.class)){
                return getString(_class.getSuperclass(), key);
            }
        }

        return null;
    }
}
