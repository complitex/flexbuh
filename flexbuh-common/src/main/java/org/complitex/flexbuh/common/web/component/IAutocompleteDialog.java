package org.complitex.flexbuh.common.web.component;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 30.11.11 15:01
 */
public interface IAutocompleteDialog<T> {
    void open(AjaxRequestTarget target, AutocompleteDialogComponent<T> component);
}
