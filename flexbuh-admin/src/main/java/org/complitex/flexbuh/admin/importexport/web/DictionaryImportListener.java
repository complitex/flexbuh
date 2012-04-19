package org.complitex.flexbuh.admin.importexport.web;

import org.complitex.flexbuh.common.service.ImportListener;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
* @author Anatoly A. Ivanov java@inheaven.ru
*         Date: 20.03.12 16:27
*/
public class DictionaryImportListener implements ImportListener<String>, Serializable {
    public static enum Status {
        PROCESSING, PROCESSED, ERROR
    }

    private String fileName = "";
    private AtomicInteger countCompleted = new AtomicInteger(0);
    private AtomicInteger countCanceled = new AtomicInteger(0);
    private AtomicInteger countTotal = new AtomicInteger(0);
    private AtomicInteger countProcess = new AtomicInteger(0);

    private Status status = null;

    private String errorMessage;

    @Override
    public void begin() {
        status = Status.PROCESSING;
        countProcess.incrementAndGet();
    }

    @Override
    public void processed(String fileName) {
        this.fileName = fileName;
        countCompleted.incrementAndGet();
    }

    @Override
    public void skip(String fileName) {
        countCanceled.incrementAndGet();
    }

    @Override
    public void completed() {
        if (countCanceled.get() > 0) {
            status = Status.ERROR;
        } else {
            status = Status.PROCESSED;
        }

        countProcess.decrementAndGet();
    }

    @Override
    public void error(String message) {
        errorMessage = message;
        status = Status.ERROR;
        countProcess.decrementAndGet();
    }

    public String currentImportFile() {
        return fileName;
    }

    public long getCountCompleted() {
        return countCompleted.get();
    }

    public long getCountCanceled() {
        return countCanceled.get();
    }

    public void addCountTotal(int count) {
        countTotal.addAndGet(count);
    }

    public long getCountTotal() {
        return countTotal.get();
    }

    public Status getStatus() {
        return status;
    }

    public boolean isEnded() {
        return countProcess.get() == 0
                && (Status.PROCESSED.equals(status)
                || Status.ERROR.equals(status));
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
