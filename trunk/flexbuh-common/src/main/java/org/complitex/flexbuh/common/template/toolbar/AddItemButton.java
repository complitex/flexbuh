package org.complitex.flexbuh.common.template.toolbar;

import org.apache.wicket.request.resource.PackageResourceReference;

/**
 *
 * @author Artem
 */
public abstract class AddItemButton extends ToolbarButton {

    private static final String IMAGE_SRC = "images/icon-addItem.gif";
    private static final String TITLE_KEY = "image.title.addItem";

    public AddItemButton(String id) {
        super(id, new PackageResourceReference(IMAGE_SRC), TITLE_KEY);
    }

    public AddItemButton(String id, boolean useAjax) {
        super(id, new PackageResourceReference(IMAGE_SRC), TITLE_KEY, useAjax);
    }
}
