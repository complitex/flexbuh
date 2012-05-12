package org.complitex.flexbuh.admin.dictionary.web;

import java.io.Serializable;

/**
* @author Anatoly A. Ivanov java@inheaven.ru
*         Date: 12.05.12 14:57
*/
public class DictionaryImportChildListener implements Serializable {
    public static enum Status {
        PROCESSING, PROCESSED, ERROR
    }

    private int total;
    private int updated = 0;
    private int inserted = 0;
    private Status status;
    private String errorMessage;

    public int getTotal() {
        return total;
    }

    public int getUpdated() {
        return updated;
    }

    public int getInserted() {
        return inserted;
    }

    public void processing(){
        status = Status.PROCESSING;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void updated(){
        updated++;
    }

    public void inserted(){
        inserted++;
    }

    public void processed(){
        status = Status.PROCESSED;
    }

    public void error(String message){
        inserted = 0;
        updated = 0;
        errorMessage = message;
        status = Status.ERROR;
    }

    public Status getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
