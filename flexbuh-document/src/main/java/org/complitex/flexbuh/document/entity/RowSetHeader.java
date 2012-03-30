package org.complitex.flexbuh.document.entity;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 30.03.12 14:21
 */
@XmlType
@XmlAccessorType(value = XmlAccessType.FIELD)
public class RowSetHeader {
    @XmlAttribute(name = "sprId")
    private String sprId;

    @XmlAttribute(name = "sprName")
    private String sprName;

    @XmlElement(name = "column")
    private List<RowSetHeaderColumn> columns = new ArrayList<>();

    @XmlElement(name = "editAble")
    private String editAble = "true";

    public RowSetHeader() {
    }

    public RowSetHeader(String sprId, String sprName) {
        this.sprId = sprId;
        this.sprName = sprName;
    }

    public void addColumn(String id, String name, String align, String filter){
        columns.add(new RowSetHeaderColumn(id, name, align, filter));
    }

    public void addColumn(String id, String name, String align){
        columns.add(new RowSetHeaderColumn(id, name, align));
    }

    public String getSprId() {
        return sprId;
    }

    public void setSprId(String sprId) {
        this.sprId = sprId;
    }

    public String getSprName() {
        return sprName;
    }

    public void setSprName(String sprName) {
        this.sprName = sprName;
    }

    public List<RowSetHeaderColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<RowSetHeaderColumn> columns) {
        this.columns = columns;
    }

    public String getEditAble() {
        return editAble;
    }

    public void setEditAble(String editAble) {
        this.editAble = editAble;
    }
}
