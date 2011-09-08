package org.complitex.flexbuh.document.web.model;

import org.apache.wicket.model.IModel;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.entity.DeclarationValue;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 17.08.11 15:57
 */
public abstract class AbstractDeclarationModel<T> implements IModel<T>{
    protected Integer rowNum;
    protected String name;
    protected Declaration declaration;
    protected DeclarationValue declarationValue;

    protected AbstractDeclarationModel(String name, Declaration declaration) {
        this.name = name;
        this.declaration = declaration;
    }

    protected AbstractDeclarationModel(Integer rowNum, String name, Declaration declaration) {
        this.rowNum = rowNum;
        this.name = name;
        this.declaration = declaration;
    }

    public Integer getRowNum() {
        return rowNum;
    }

    public String getName() {
        return name;
    }

    public Declaration getDeclaration() {
        return declaration;
    }

    public DeclarationValue getDeclarationValue() {
        return declarationValue;
    }

    @Override
    public void detach() {
        //not yet implemented
    }
}
