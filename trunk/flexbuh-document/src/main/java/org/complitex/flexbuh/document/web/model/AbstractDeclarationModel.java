package org.complitex.flexbuh.document.web.model;

import org.apache.wicket.model.IModel;
import org.complitex.flexbuh.document.entity.Declaration;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 17.08.11 15:57
 */
public abstract class AbstractDeclarationModel<T> implements IModel<T>{
    protected String name;
    protected Declaration declaration;

    protected AbstractDeclarationModel(String name, Declaration declaration) {
        this.name = name;
        this.declaration = declaration;
    }

    public String getName() {
        return name;
    }

    public Declaration getDeclaration() {
        return declaration;
    }

    @Override
    public void detach() {
        //not yet implemented
    }
}
