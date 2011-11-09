package org.complitex.flexbuh.document.web.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 01.09.11 17:55
 */
public abstract class AddRowPanel extends Panel {
    private boolean deleteVisible = true;

    private WebMarkupContainer row;
    private StretchTable stretchTable;

    public AddRowPanel(String id, final WebMarkupContainer row, final StretchTable stretchTable) {
        super(id);

        this.row = row;
        this.stretchTable = stretchTable;

        add(new AjaxLink("add") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                deleteVisible = true;

                onAdd(target);
                afterAction(target);
            }
        });

        add(new AjaxLink("delete") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                stretchTable.deleteRow(row);

                onDelete(target);
                afterAction(target);
            }

            @Override
            public boolean isVisible() {
                return deleteVisible;
            }
        });
    }

    public StretchTable getStretchTable() {
        return stretchTable;
    }

    public WebMarkupContainer getRow() {
        return row;
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
