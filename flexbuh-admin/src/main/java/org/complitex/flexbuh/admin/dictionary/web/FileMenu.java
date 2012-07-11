package org.complitex.flexbuh.admin.dictionary.web;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.common.template.ResourceTemplateMenu;

/**
 * @author Pavel Sknar
 *         Date: 30.08.11 12:54
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class FileMenu extends ResourceTemplateMenu {
    public FileMenu() {
        add("import_file", ImportTemplate.class);
        add("xsd_list", TemplateXMLList.class, new PageParameters(){{set("type", "XSD");}});
        add("xsl_list", TemplateXMLList.class, new PageParameters(){{set("type", "XSL");}});
        add("fo_list", TemplateXMLList.class, new PageParameters(){{set("type", "FO");}});
        add("control_list", TemplateXMLList.class, new PageParameters(){{set("type", "CONTROL");}});
    }
}
