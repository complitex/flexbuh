package org.complitex.flexbuh.entity;

import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 27.07.11 16:12
 */
public class TemplateXSL extends AbstractTemplate {
    public static final String TABLE = "template_xsl";

    public TemplateXSL() {
    }

    public TemplateXSL(String name, String data, Date uploadDate) {
        super(name, data, uploadDate);
    }

    @Override
    public String getTable() {
        return TABLE;
    }
}
