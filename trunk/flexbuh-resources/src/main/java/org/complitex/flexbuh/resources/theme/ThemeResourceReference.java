package org.complitex.flexbuh.resources.theme;


import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.05.11 16:02
 */
public class ThemeResourceReference extends ResourceReference {
    public ThemeResourceReference() {
        super(ThemeResourceReference.class, "jquery-ui-1.8.13.custom.css");
    }

    @Override
    public IResource getResource() {
        return null;
    }
}
