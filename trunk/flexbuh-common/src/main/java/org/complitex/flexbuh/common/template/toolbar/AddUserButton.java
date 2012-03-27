package org.complitex.flexbuh.common.template.toolbar;

/**
 *
 * @author Artem
 */
public abstract class AddUserButton extends ToolbarButton {

    private static final String IMAGE_SRC = "images/icon-addUser.gif";
    private static final String TITLE_KEY = "image.title.addUser";

    public AddUserButton(String id) {
        super(id, IMAGE_SRC, TITLE_KEY);
    }
}
