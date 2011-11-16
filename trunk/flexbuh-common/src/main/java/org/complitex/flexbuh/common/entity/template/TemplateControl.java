package org.complitex.flexbuh.common.entity.template;

import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 26.08.11 15:37
 */
public class TemplateControl extends AbstractTemplate {
    public TemplateControl() {
    }

    public TemplateControl(String name, String data, Date uploadDate) {
        super(name, data, uploadDate);
    }
}
