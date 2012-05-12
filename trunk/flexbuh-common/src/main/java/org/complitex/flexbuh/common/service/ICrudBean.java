package org.complitex.flexbuh.common.service;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 17.04.12 15:01
 */
public interface ICrudBean<T> {
    Long getId(T o);

    void insert(T o);

    void update(T o);

    T load(Long id);

    void delete(Long id);
}
