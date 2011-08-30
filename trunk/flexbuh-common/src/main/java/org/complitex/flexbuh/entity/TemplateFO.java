package org.complitex.flexbuh.entity;

import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 27.07.11 16:11
 */
public class TemplateFO extends AbstractTemplate {
    public static final String TABLE = "template_fo";

    public TemplateFO() {
    }

    public TemplateFO(String name, String data, Date uploadDate) {
        super(name, data, uploadDate);
    }

    @Override
    public String getTable() {
        return TABLE;
    }
}
