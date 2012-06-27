package org.complitex.flexbuh.document.web.component;

import org.apache.wicket.markup.html.link.Link;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.entity.DeclarationValue;
import org.complitex.flexbuh.document.util.DeclarationUtil;
import org.complitex.flexbuh.document.web.DeclarationEditPage;

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
        Declaration newDeclaration = DeclarationUtil.copy(declaration);

        newDeclaration.getHead().setCDocType(declaration.getHead().getCDocType() + 1);
        newDeclaration.getHead().setCDocStan(3);

        newDeclaration.setSessionId(declaration.getSessionId());
        newDeclaration.setPersonProfileId(declaration.getPersonProfileId());
        newDeclaration.setName(declaration.getName());

        //Уточняющий радио баттон
        if (newDeclaration.getDeclarationValue("HZ") != null || newDeclaration.getDeclarationValue("HZN") != null){
            newDeclaration.removeDeclarationValue("HZ");
            newDeclaration.removeDeclarationValue("HZN");

            newDeclaration.addDeclarationValue(new DeclarationValue("HZU", "1"));
        }

        setResponsePage(new DeclarationEditPage(newDeclaration));
    }
}
