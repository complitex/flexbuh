package org.complitex.flexbuh.common.service;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 25.01.12 17:51
 */
public abstract class AbstractImportListener implements ImportListener{
    @Override
    public void begin() {
    }

    @Override
    public void completed() {
    }

    @Override
    public void completedWithError() {
    }

    @Override
    public void cancel() {
    }

    @Override
    public ImportListener getChildImportListener(Object o) {
        return null;
    }
}
