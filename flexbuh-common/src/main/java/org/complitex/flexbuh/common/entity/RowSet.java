package org.complitex.flexbuh.common.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 13.04.12 16:58
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
public class RowSet<T>{
    @XmlAnyElement(lax=true)
    private List<T> rows;

    public RowSet() {
    }

    public RowSet(List<T> rows) {
        this.rows = rows;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }
}
