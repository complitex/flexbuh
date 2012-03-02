package org.complitex.flexbuh.document.web;

import org.apache.wicket.markup.html.link.Link;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.service.DeclarationService;

import javax.ejb.EJB;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 28.02.12 17:49
 */
public class DeclarationCheckLink extends Link {
    @EJB
    private DeclarationService declarationService;

    private Declaration declaration;

    public DeclarationCheckLink(String id, Declaration declaration) {
        super(id);

        this.declaration = declaration;
    }

    @Override
    public void onClick() {
        declarationService.validate(declaration);

        getSession().info((declaration.isValidated()
                ? getString("validated")
                : getString("validate_error") + " -  " + declaration.getValidateMessage()));

        declarationService.check(declaration);

        getSession().info((declaration.isChecked()
                ? getString("checked")
                : getString("check_error") +  " -  " + declaration.getCheckMessage()));
    }
}
