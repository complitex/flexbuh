package org.complitex.flexbuh.common.template.toolbar;

/**
 *
 * @author Artem
 */
public class HelpButton extends ToolbarButton {

    private static final String IMAGE_SRC = "images/icon-help.gif";
    private static final String TITLE_KEY = "image.title.help";

    public HelpButton(String id) {
        super(id, IMAGE_SRC, TITLE_KEY);
    }

    @Override
    public void onClick() {
        //TODO: add redirect to help page.
    }
}
