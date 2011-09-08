package org.complitex.flexbuh.document.web.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 01.09.11 17:55
 */
public abstract class AddRowPanel extends Panel {
    private boolean deleteVisible = true;

    public AddRowPanel(String id) {
        super(id);

        add(new AjaxLink("add") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onAdd(target);
                afterAction(target);
            }
        });

        add(new AjaxLink("delete") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onDelete(target);
                afterAction(target);
            }

            @Override
            public boolean isVisible() {
                return deleteVisible;
            }
        });
    }

    public boolean isDeleteVisible() {
        return deleteVisible;
    }

    public void setDeleteVisible(boolean deleteVisible) {
        this.deleteVisible = deleteVisible;
    }

    protected abstract void onAdd(AjaxRequestTarget target);

    protected abstract void onDelete(AjaxRequestTarget target);

    protected void afterAction(AjaxRequestTarget target){
    }
}
