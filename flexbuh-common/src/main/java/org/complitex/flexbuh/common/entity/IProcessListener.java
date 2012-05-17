package org.complitex.flexbuh.common.entity;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 17.05.12 16:55
 */
public interface IProcessListener<T> {
    void onSuccess(T object);
    void onError(T object, Exception e);
}
