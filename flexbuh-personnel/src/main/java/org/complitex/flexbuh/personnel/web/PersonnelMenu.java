package org.complitex.flexbuh.personnel.web;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.common.template.ResourceTemplateMenu;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.12.11 17:02
 */
@AuthorizeInstantiation(SecurityRole.PERSONAL_MANAGER)
public class PersonnelMenu extends ResourceTemplateMenu{
    public PersonnelMenu() {
        add("organization", OrganizationList.class);
    }
}
