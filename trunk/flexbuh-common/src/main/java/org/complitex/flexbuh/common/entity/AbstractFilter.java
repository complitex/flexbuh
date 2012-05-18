package org.complitex.flexbuh.common.entity;

import java.io.Serializable;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 09.09.11 18:09
 */
public class AbstractFilter implements Serializable{
    protected Long sessionId;

    protected int first;
    protected int count;
    protected String sortProperty;
    protected boolean ascending;

    public AbstractFilter() {
    }

    public AbstractFilter(Long sessionId) {
        this.sessionId = sessionId;
    }

    public AbstractFilter(int first, int count) {
        this.first = first;
        this.count = count;
    }

    public AbstractFilter(Long sessionId, int first, int count) {
        this.sessionId = sessionId;
        this.first = first;
        this.count = count;
    }

    public String getAsc(){
        return ascending ? "asc" : "desc";
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getSortProperty() {
        return sortProperty;
    }

    public void setSortProperty(String sortProperty) {
        this.sortProperty = sortProperty;
    }

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }
}
