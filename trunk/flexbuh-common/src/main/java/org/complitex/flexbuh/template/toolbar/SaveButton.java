package org.complitex.flexbuh.template.toolbar;

import org.apache.wicket.request.resource.PackageResourceReference;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 03.06.11 17:45
 */
public abstract class SaveButton extends ToolbarButton {

    private static final String IMAGE_SRC = "images/icon-save.gif";
    private static final String TITLE_KEY = "title";

    public SaveButton(String id, boolean useAjax) {
        super(id, new PackageResourceReference(IMAGE_SRC), TITLE_KEY, useAjax);
    }
}
