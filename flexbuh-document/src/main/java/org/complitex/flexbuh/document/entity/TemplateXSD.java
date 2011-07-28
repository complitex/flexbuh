package org.complitex.flexbuh.document.entity;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 27.07.11 16:13
 */
public class TemplateXSD extends AbstractTemplate {
    public static final String TABLE = "template_xsd";

    public TemplateXSD() {
    }

    public TemplateXSD(String name, String data) {
        super(name, data);
    }

    @Override
    public String getTable() {
        return TABLE;
    }
}