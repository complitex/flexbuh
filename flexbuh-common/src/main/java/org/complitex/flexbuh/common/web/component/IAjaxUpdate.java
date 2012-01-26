package org.complitex.flexbuh.common.web.component;

import org.apache.wicket.ajax.AjaxRequestTarget;

import java.io.Serializable;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 19.12.11 15:50
 */
public interface IAjaxUpdate extends Serializable{
    void onUpdate(AjaxRequestTarget target);
}
