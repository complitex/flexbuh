package org.complitex.flexbuh.common.service;

import org.apache.ibatis.reflection.factory.DefaultObjectFactory;

import java.util.List;
import java.util.Properties;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.12.11 17:28
 */
public class TemporalEntityObjectFactory extends DefaultObjectFactory {
    @Override
    public Object create(Class type) {
        System.out.println(type);

        return super.create(type);
    }

    @Override
    public Object create(Class type, List<Class> constructorArgTypes, List<Object> constructorArgs) {
        if (!constructorArgs.isEmpty()){
            String className = constructorArgs.get(0).toString();

            try {
                return getClass().getClassLoader().loadClass(className).newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        
        return super.create(type, constructorArgTypes, constructorArgs);
    }

    @Override
    public void setProperties(Properties properties) {
        //no property used from mybatis-config.xml
    }
}
