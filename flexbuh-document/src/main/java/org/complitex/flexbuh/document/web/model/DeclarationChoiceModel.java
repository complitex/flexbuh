package org.complitex.flexbuh.document.web.model;

import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.entity.DeclarationValue;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 17.08.11 16:07
 */
public class DeclarationChoiceModel extends AbstractDeclarationModel<String>{
    private final static String CHECKED = "1";

    public DeclarationChoiceModel(String name, Declaration declaration) {
        super(name, declaration);
    }

    @Override
    public String getObject() {
        return name;
    }

    @Override
    public void setObject(String object) {
        if (object != null && !object.equals(name)){
            if (name != null) {
                declaration.removeDeclarationValue(name);
            }

            name = object;

            DeclarationValue declarationValue = declaration.getDeclarationValue(name);

            if (declarationValue != null){
                declarationValue.setValue(CHECKED);
            }else {
                declaration.addDeclarationValue(new DeclarationValue(name, CHECKED));
            }
        }
    }
}
