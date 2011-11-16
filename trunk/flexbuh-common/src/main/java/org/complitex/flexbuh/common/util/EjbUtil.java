package org.complitex.flexbuh.common.util;

import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 09.09.11 17:19
 */
public class EjbUtil {
    @SuppressWarnings({"unchecked"})
    public static <T> T getBean(Class<T> beanClass){
        try {
            return (T) new InitialContext().lookup("java:module/" + beanClass.getSimpleName());
        } catch (NamingException e) {
            throw new RuntimeException("Ошибка поиска EJB", e);
        }
    }
}
