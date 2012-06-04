package org.complitex.flexbuh.document.web.model;

import org.complitex.flexbuh.document.entity.Declaration;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 04.06.12 17:37
 */
public class DeclarationRownumModel extends DeclarationStringModel {
    public DeclarationRownumModel(Integer rowRum, String name, String type, String mask, String field, Declaration declaration) {
        super(rowRum, name, type, mask, field, declaration);
    }

    @Override
    public void updateRowNum(Integer rowNum) {
        super.updateRowNum(rowNum);

        setObject(rowNum.toString());
    }
}
