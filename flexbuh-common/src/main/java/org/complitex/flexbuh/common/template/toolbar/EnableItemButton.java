package org.complitex.flexbuh.common.template.toolbar;

/**
 *
 * @author Artem
 */
public abstract class EnableItemButton extends ToolbarButton {

    private static final String IMAGE_SRC = "images/icon-hideItem-1.gif";
    private static final String TITLE_KEY = "image.title.enableItem";

    public EnableItemButton(String id) {
        super(id, IMAGE_SRC, TITLE_KEY);
    }

}
