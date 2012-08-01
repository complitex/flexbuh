package org.complitex.flexbuh.personnel.web.component;

import org.apache.wicket.request.resource.CssResourceReference;

/**
 * @author Pavel Sknar
 *         Date: 10.04.12 11:40
 */
public class TreeTheme extends CssResourceReference {
    public TreeTheme() {
        super(TreeTheme.class, "theme/tree.css");
    }
}
