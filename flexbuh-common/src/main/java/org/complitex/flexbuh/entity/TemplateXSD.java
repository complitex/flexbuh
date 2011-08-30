package org.complitex.flexbuh.entity;

import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 27.07.11 16:13
 */
public class TemplateXSD extends AbstractTemplate {
    public static final String TABLE = "template_xsd";

    public TemplateXSD() {
    }

    public TemplateXSD(String name, String data, Date uploadDate) {
        super(name, data, uploadDate);
    }

    @Override
    public String getTable() {
        return TABLE;
    }
}