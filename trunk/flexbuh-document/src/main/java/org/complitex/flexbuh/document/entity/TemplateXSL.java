package org.complitex.flexbuh.document.entity;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 27.07.11 16:12
 */
public class TemplateXSL extends AbstractTemplate {
    public static final String TABLE = "template_xsl";

    public TemplateXSL() {
    }

    public TemplateXSL(String name, String data) {
        super(name, data);
    }

    @Override
    public String getTable() {
        return TABLE;
    }
}
