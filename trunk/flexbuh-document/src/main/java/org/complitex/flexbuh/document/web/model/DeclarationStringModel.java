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

    public DeclarationStringModel(Integer rowRum, String name, String type, Declaration declaration) {
        super(rowRum, name, declaration);

        setObject("");
        declarationValue.setType(type);
    }

    @Override
    public String getObject() {
        DeclarationValue value = declaration.getDeclarationValue(rowNum, name);

        return value != null ? value.getValue() : null;
    }

    @Override
    public void setObject(String object) {
        declarationValue = declaration.getDeclarationValue(rowNum, name);

        if (declarationValue == null){
            declarationValue = new DeclarationValue(rowNum, name, null);
            declaration.addDeclarationValue(declarationValue);
        }

        declarationValue.setValue(object);
    }

    public void updateRowNum(Integer rowNum){
        this.rowNum = rowNum;

        if (declarationValue != null){
            declarationValue.setRowNum(rowNum);
        }
    }

    public void removeValue(){
        declaration.removeDeclarationValue(rowNum, name);
    }
}
