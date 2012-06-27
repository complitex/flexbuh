package org.complitex.flexbuh.document.util;

import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.entity.DeclarationHead;
import org.complitex.flexbuh.document.entity.DeclarationValue;
import org.complitex.flexbuh.document.entity.LinkedDeclaration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 27.06.12 19:00
 */
public class DeclarationUtil {
    public static Declaration copy(Declaration declaration){
        Declaration copy = new Declaration();

        //Head
        copy.setHead(copy(declaration.getHead()));

        //Values
        for (DeclarationValue dv : declaration.getDeclarationValues()){
            copy.addDeclarationValue(new DeclarationValue(dv.getRowNum(), dv.getName(), dv.getValue()));
        }

        //Linked
        List<LinkedDeclaration> linkedDeclarations = declaration.getHead().getLinkedDeclarations();

        if (linkedDeclarations != null) {
            List<LinkedDeclaration> list = new ArrayList<>();
            copy.getHead().setLinkedDeclarations(list);

            for (LinkedDeclaration linkedDeclaration : linkedDeclarations){
                LinkedDeclaration ld = new LinkedDeclaration();

                ld.setDeclaration(linkedDeclaration.getDeclaration());
                ld.setNum(linkedDeclaration.getNum());
                ld.setType(linkedDeclaration.getType());

                list.add(ld);
            }
        }

        return copy;
    }

    public static DeclarationHead copy(DeclarationHead declarationHead){
        DeclarationHead copy = new DeclarationHead();

        copy.setTin(declarationHead.getTin());
        copy.setCDoc(declarationHead.getCDoc());
        copy.setCDocSub(declarationHead.getCDocSub());
        copy.setCDocVer(declarationHead.getCDocVer());
        copy.setCDocType(declarationHead.getCDocType());
        copy.setCDocCnt(declarationHead.getCDocCnt());
        copy.setCReg(declarationHead.getCReg());
        copy.setCRaj(declarationHead.getCRaj());
        copy.setPeriodMonth(declarationHead.getPeriodMonth());
        copy.setPeriodType(declarationHead.getPeriodType());
        copy.setPeriodYear(declarationHead.getPeriodYear());
        copy.setCStiOrig(declarationHead.getCStiOrig());
        copy.setCDocStan(declarationHead.getCDocStan());
        copy.setDFill(declarationHead.getDFill());
        copy.setSoftware(declarationHead.getSoftware());

        return copy;
    }
}
