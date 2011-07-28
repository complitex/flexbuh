package org.complitex.flexbuh.document.entity;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 27.07.11 16:11
 */
public class TemplateFO extends AbstractTemplate {
    public static final String TABLE = "template_fo";

    public TemplateFO() {
    }

    public TemplateFO(String name, String data) {
        super(name, data);
    }

    @Override
    public String getTable() {
        return TABLE;
    }
}
