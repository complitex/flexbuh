package org.complitex.flexbuh.web.component;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.value.IValueMap;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 02.09.11 18:04
 */
public class CssStyleTextField<T> extends TextField<T> {
    private String cssStyle;
    private String title;

    public CssStyleTextField(String id, IModel<T> iModel) {
        super(id, iModel);
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);

        IValueMap attributes = tag.getAttributes();

        attributes.put("style", cssStyle);
        attributes.put("title", title);
    }

    public String getCssStyle() {
        return cssStyle;
    }

    public void setCssStyle(String cssStyle) {
        this.cssStyle = cssStyle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
