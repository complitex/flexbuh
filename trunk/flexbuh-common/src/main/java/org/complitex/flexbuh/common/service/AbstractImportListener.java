package org.complitex.flexbuh.common.service;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 25.01.12 17:51
 */
public abstract class AbstractImportListener<T> implements ImportListener<T>{
    @Override
    public void begin() {
    }

    @Override
    public void processed(T object) {
    }

    @Override
    public void skip(T object) {
    }

    @Override
    public void completed() {
    }

    @Override
    public void error() {
    }
}
