package org.complitex.flexbuh.document.web.component;

import org.apache.wicket.markup.html.link.Link;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.entity.DeclarationHead;
import org.complitex.flexbuh.document.web.DeclarationFormPage;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.04.12 17:54
 */
public class DeclarationClarifyLink extends Link{
    private Declaration declaration;

    public DeclarationClarifyLink(String id, Declaration declaration) {
        super(id);

        this.declaration = declaration;
    }

    @Override
    public void onClick() {
        Declaration newDeclaration = new Declaration();

        DeclarationHead head = declaration.getHead();
        DeclarationHead newHead = newDeclaration.getHead();

        newHead.setCDoc(head.getCDoc());
        newHead.setCDocSub(head.getCDocSub());
        newHead.setCDocVer(head.getCDocVer());
        newHead.setPeriodMonth(head.getPeriodMonth());
        newHead.setPeriodType(head.getPeriodType());
        newHead.setPeriodYear(head.getPeriodYear());
        newHead.setTin(head.getTin());
        newHead.setCDocStan(head.getCDocStan());
        newHead.setCDocType(head.getCDocType() + 1);

        newDeclaration.setSessionId(declaration.getSessionId());
        newDeclaration.setPersonProfileId(declaration.getPersonProfileId());
        newDeclaration.setName(declaration.getName());

        setResponsePage(new DeclarationFormPage(newDeclaration));
    }
}
