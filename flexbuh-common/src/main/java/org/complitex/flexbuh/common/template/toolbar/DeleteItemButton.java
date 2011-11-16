/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.flexbuh.common.template.toolbar;

import org.apache.wicket.request.resource.PackageResourceReference;

/**
 *
 * @author Artem
 */
public abstract class DeleteItemButton extends ToolbarButton {

    private static final String IMAGE_SRC = "images/icon-deleteDocument.gif";

    private static final String TITLE_KEY = "image.title.deleteItem";

    public DeleteItemButton(String id) {
        super(id, new PackageResourceReference(IMAGE_SRC), TITLE_KEY);
    }
}
