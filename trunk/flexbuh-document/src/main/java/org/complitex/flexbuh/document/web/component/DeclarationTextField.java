package org.complitex.flexbuh.document.web.component;

import org.complitex.flexbuh.document.web.model.DeclarationStringModel;
import org.complitex.flexbuh.web.component.CssStyleTextField;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 27.10.11 18:06
 */
public class DeclarationTextField extends CssStyleTextField<String> {
    public DeclarationTextField(String id, DeclarationStringModel model) {
        super(id, model);
    }

    public DeclarationStringModel getDeclarationModel(){
        return (DeclarationStringModel) getModel();
    }
}
