package org.complitex.flexbuh.common.web.component;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 15.12.11 16:35
 */
public class InputPanel<T> extends Panel {
    public InputPanel(String id, IModel<T> model) {
        super(id);
        
        add(new TextField<>("input", model));
    }
}
