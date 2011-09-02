package org.complitex.flexbuh.web.component;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 02.09.11 18:04
 */
public class CssStyleTextField<T> extends TextField<T> {
    private String cssStyle;

    public CssStyleTextField(String id, IModel<T> iModel) {
        super(id, iModel);
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);

        tag.getAttributes().put("style", cssStyle);
    }

    public String getCssStyle() {
        return cssStyle;
    }

    public void setCssStyle(String cssStyle) {
        this.cssStyle = cssStyle;
    }
}
