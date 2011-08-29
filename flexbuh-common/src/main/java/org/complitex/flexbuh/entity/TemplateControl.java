package org.complitex.flexbuh.entity;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 26.08.11 15:37
 */
public class TemplateControl extends AbstractTemplate{
    public static final String TABLE = "template_control";

    public TemplateControl() {
    }

    public TemplateControl(String name, String data) {
        super(name, data);
    }

    @Override
    public String getTable() {
        return TABLE;
    }
}
