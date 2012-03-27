package org.complitex.flexbuh.common.template.toolbar;

/**
 *
 * @author Artem
 */
public abstract class AddDocumentButton extends ToolbarButton {

    private static final String IMAGE_SRC = "images/icon-addDocument.gif";
    private static final String TITLE_KEY = "image.title.addDocument";

    public AddDocumentButton(String id) {
        super(id, IMAGE_SRC, TITLE_KEY, "AddDocumentButton");
    }
}
