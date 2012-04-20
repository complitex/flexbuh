package org.complitex.flexbuh.common.entity.template;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 27.07.11 16:02
 */
public class TemplateXML implements Serializable {
    private Long id;
    private TemplateXMLType type;
    private String name;
    private String data;
    private Date uploadDate;

    public TemplateXML(TemplateXMLType type, String name, String data, Date uploadDate) {
        this.type = type;
        this.name = name;
        this.data = data;
        this.uploadDate = uploadDate;
    }

    public TemplateXML() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TemplateXMLType getType() {
        return type;
    }

    public void setType(TemplateXMLType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }
}
