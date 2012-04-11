package org.complitex.flexbuh.personnel.web;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.complitex.flexbuh.common.security.SecurityRole;

/**
 * @author Pavel Sknar
 *         Date: 13.03.12 8:41
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class OrganizationCreate extends OrganizationEdit {

    public OrganizationCreate() {
        super();
    }
}
