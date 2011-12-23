package org.complitex.flexbuh.admin.importexport.web;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.common.template.ResourceTemplateMenu;

/**
 * @author Pavel Sknar
 *         Date: 30.08.11 12:54
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class FileMenu extends ResourceTemplateMenu {
    public FileMenu() {
        add("import_file", ImportFile.class);
        add("xsd_list", TemplateXSDList.class);
        add("xsl_list", TemplateXSLList.class);
        add("fo_list", TemplateFOList.class);
        add("control_list", TemplateControlList.class);
    }
}
