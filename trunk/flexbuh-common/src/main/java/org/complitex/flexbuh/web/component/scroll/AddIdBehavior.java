package org.complitex.flexbuh.web.component.scroll;

import org.apache.wicket.behavior.SimpleAttributeModifier;

/**
 * @author Pavel Sknar
 *         Date: 08.09.11 18:09
 */
public class AddIdBehavior extends SimpleAttributeModifier {

    public AddIdBehavior(String markupId) {
        super("id", markupId);
    }
}
