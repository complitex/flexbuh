package org.complitex.flexbuh.common.service;

import org.apache.ibatis.reflection.factory.DefaultObjectFactory;

import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.12.11 17:28
 */
public class TemporalEntityObjectFactory extends DefaultObjectFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> T create(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
        if (constructorArgs != null && !constructorArgs.isEmpty()){
            String className = constructorArgs.get(0).toString();

            try {
                ClassLoader classLoader = getClass().getClassLoader();

                Class<?> _class = classLoader.loadClass(className);

                return (T) _class.newInstance();
            } catch (Exception e) {
                return super.create(type, constructorArgTypes, constructorArgs);
            }
        }

        return super.create(type, constructorArgTypes, constructorArgs);
    }
}
