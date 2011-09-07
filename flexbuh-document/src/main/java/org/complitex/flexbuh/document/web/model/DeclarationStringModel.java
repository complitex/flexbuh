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

     public DeclarationStringModel(Integer rowRum, String name, Declaration declaration) {
        super(rowRum, name, declaration);
    }

    @Override
    public String getObject() {
        DeclarationValue value = declaration.getValue(rowNum, name);

        return value != null ? value.getValue() : null;
    }

    @Override
    public void setObject(String object) {
        DeclarationValue value = declaration.getValue(rowNum, name);

        if (value == null){
            value = new DeclarationValue(rowNum, name, null);
            declaration.addValue(value);
        }

        value.setValue(object);
    }

    public void setRowNum(Integer rowNum){
        DeclarationValue value = declaration.getValue(this.rowNum, name);

        if (value != null){
            value.setRowNum(rowNum);
        }

        this.rowNum = rowNum;
    }
}
