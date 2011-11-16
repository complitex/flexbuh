package org.complitex.flexbuh.common.template.toolbar;

import org.apache.wicket.request.resource.PackageResourceReference;

/**
 *
 * @author Artem
 */
public abstract class AddUserButton extends ToolbarButton {

    private static final String IMAGE_SRC = "images/icon-addUser.gif";
    private static final String TITLE_KEY = "image.title.addUser";

    public AddUserButton(String id) {
        super(id, new PackageResourceReference(IMAGE_SRC), TITLE_KEY);
    }
}
