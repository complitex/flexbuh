package org.complitex.flexbuh.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 15.12.11 16:40
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Display {
    public boolean visible() default true;
    public boolean enable() default true;
    public int order() default 1000;
}
