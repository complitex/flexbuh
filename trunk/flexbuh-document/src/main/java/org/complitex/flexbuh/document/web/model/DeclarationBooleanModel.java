package org.complitex.flexbuh.document.web.model;

import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.entity.DeclarationValue;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 17.08.11 15:54
 */
public class DeclarationBooleanModel extends AbstractDeclarationModel<Boolean>{
    private final static String TRUE = "1";
    private final static String FALSE = "";

    public DeclarationBooleanModel(String name, Declaration declaration) {
        super(name, declaration);
    }

    @Override
    public Boolean getObject() {
        DeclarationValue value = declaration.getValue(name);

        return value != null ? TRUE.equals(value.getValue()) : Boolean.FALSE;
    }

    @Override
    public void setObject(Boolean object) {
        DeclarationValue value = declaration.getValue(name);

        if (value == null){
            value = new DeclarationValue(name);
            declaration.addValue(value);
        }

        value.setValue(object ? TRUE : FALSE);
    }
}
