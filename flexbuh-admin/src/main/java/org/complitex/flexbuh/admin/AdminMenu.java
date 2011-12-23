package org.complitex.flexbuh.admin;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.complitex.flexbuh.admin.user.web.UserList;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.common.template.ResourceTemplateMenu;
import org.complitex.flexbuh.common.template.pages.ConfigEdit;
import org.complitex.flexbuh.logging.web.LogList;

/**
 * @author Pavel Sknar
 *         Date: 03.09.11 10:15
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class AdminMenu extends ResourceTemplateMenu {
    public AdminMenu() {
        add("config", ConfigEdit.class);
        add("user_list", UserList.class);
        add("log_list", LogList.class);
    }
}
