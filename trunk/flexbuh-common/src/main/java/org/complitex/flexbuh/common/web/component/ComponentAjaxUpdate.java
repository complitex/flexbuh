package org.complitex.flexbuh.common.web.component;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 06.02.12 17:45
 */
public class ComponentAjaxUpdate implements IAjaxUpdate{
    private Component component;

    public ComponentAjaxUpdate(Component component) {
        this.component = component;
    }

    @Override
    public void onUpdate(AjaxRequestTarget target) {
        target.add(component);
    }
}
