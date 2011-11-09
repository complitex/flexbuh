package org.complitex.flexbuh.document.web.model;

import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.entity.DeclarationValue;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 17.08.11 15:31
 */
public class DeclarationStringModel extends AbstractDeclarationModel<String>{
    private String mask;
    private DeclarationValue declarationValue;

    public DeclarationStringModel(Integer rowRum, String name, String type, String mask, Declaration declaration) {
        super(rowRum, name, declaration);
        
        this.mask = mask;

        declarationValue = isMask()
                ? declaration.getDeclarationValue(getMaskName())
                : declaration.getDeclarationValue(rowNum, name);

        if (declarationValue == null){
            declarationValue = isMask()
                    ? new DeclarationValue(null, getMaskName(), null)
                    : new DeclarationValue(rowNum, name, null);

            declarationValue.setType(type);

            declaration.addDeclarationValue(declarationValue);
        }
    }

    @Override
    public String getObject() {
        return declarationValue.getValue();
    }

    @Override
    public void setObject(String object) {         
        declarationValue.setValue(object);
    }

    public void updateRowNum(Integer rowNum){
        this.rowNum = rowNum;
        
        if (isMask()){
            declarationValue.setName(getMaskName());
        }else{
            declarationValue.setRowNum(rowNum);
        }
    }

    public void removeValue(){
        declaration.removeDeclarationValue(declarationValue);
    }

    public String getMaskName(){
        if (isMask()){
            return mask.replace("XXXX", rowNum + "");                        
        }
        
        return null;
    }

    public String getMask() {
        return mask;
    }

    public boolean isMask(){
        return mask != null && !mask.isEmpty();
    }
}
