package org.complitex.flexbuh.entity;

import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 26.08.11 15:37
 */
public class TemplateControl extends AbstractTemplate{
    public static final String TABLE = "template_control";

    public TemplateControl() {
    }

    public TemplateControl(String name, String data, Date uploadDate) {
        super(name, data, uploadDate);
    }

    @Override
    public String getTable() {
        return TABLE;
    }
}
