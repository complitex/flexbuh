package org.complitex.flexbuh.common.template.toolbar;

import org.apache.wicket.request.resource.PackageResourceReference;

/**
 *
 * @author Artem
 */
public abstract class AddDocumentButton extends ToolbarButton {

    private static final String IMAGE_SRC = "images/icon-addDocument.gif";
    private static final String TITLE_KEY = "image.title.addDocument";

    public AddDocumentButton(String id) {
        super(id, new PackageResourceReference(IMAGE_SRC), TITLE_KEY, "AddDocumentButton");
    }
}