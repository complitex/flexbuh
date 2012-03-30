package org.complitex.flexbuh.document.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 30.03.12 14:24
 */
@XmlType
@XmlAccessorType(value = XmlAccessType.FIELD)
public class RowSetHeaderColumn {
    @XmlAttribute(name = "id")
    private String id;

    @XmlAttribute(name = "name")
    private String name;

    @XmlAttribute(name = "align")
    private String align;

    @XmlAttribute(name = "filter")
    private String filter;

    public RowSetHeaderColumn() {
    }

    public RowSetHeaderColumn(String id, String name, String align, String filter) {
        this.id = id;
        this.name = name;
        this.align = align;
        this.filter = filter;
    }

    public RowSetHeaderColumn(String id, String name, String align) {
        this.id = id;
        this.name = name;
        this.align = align;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlign() {
        return align;
    }

    public void setAlign(String align) {
        this.align = align;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }
}
