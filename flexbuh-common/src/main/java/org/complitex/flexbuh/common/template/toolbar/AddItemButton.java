package org.complitex.flexbuh.common.template.toolbar;

/**
 *
 * @author Artem
 */
public abstract class AddItemButton extends ToolbarButton {

    private static final String IMAGE_SRC = "images/icon-addItem.gif";
    private static final String TITLE_KEY = "image.title.addItem";

    public AddItemButton(String id) {
        super(id, IMAGE_SRC, TITLE_KEY);
    }

    public AddItemButton(String id, boolean useAjax) {
        super(id, IMAGE_SRC, TITLE_KEY, useAjax);
    }
}
