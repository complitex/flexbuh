package org.complitex.flexbuh.common.template.toolbar;

/**
 * @author Pavel Sknar
 *         Date: 13.03.12 8:47
 */
public abstract class AddOrganizationButton extends ToolbarButton {

    private static final String IMAGE_SRC = "images/icon-addDocument.gif";
    private static final String TITLE_KEY = "image.title.addOrganization";

    public AddOrganizationButton(String id) {
        super(id, IMAGE_SRC, TITLE_KEY, "AddOrganizationButton");
    }
}
