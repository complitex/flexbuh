package org.complitex.flexbuh.document.web.model;

import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.entity.DeclarationValue;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 17.08.11 15:31
 */
public class DeclarationStringModel extends AbstractDeclarationModel<String>{
    public DeclarationStringModel(String name, Declaration declaration) {
        super(name, declaration);
    }

    @Override
    public String getObject() {
        DeclarationValue value = declaration.getValue(name);

        return value != null ? value.getValue() : null;
    }

    @Override
    public void setObject(String object) {
        DeclarationValue value = declaration.getValue(name);

        if (value == null){
            value = declaration.addValue(new DeclarationValue(name));
        }

        value.setValue(object);
    }
}
