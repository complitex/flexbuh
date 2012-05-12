package org.complitex.flexbuh.admin.dictionary.web;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.common.template.ResourceTemplateMenu;

/**
 * @author Pavel Sknar
 *         Date: 18.08.11 17:04
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class DictionaryMenu extends ResourceTemplateMenu {
    public DictionaryMenu() {
        add("import_dictionary", ImportDictionary.class);
        add("currency_list", CurrencyList.class);
        add("document_list", DocumentList.class);
        add("document_term_list", DocumentTermList.class);
        add("document_version_list", DocumentVersionList.class);
        add("region_list", RegionList.class);
        add("tax_inspection_list", TaxInspectionList.class);
        add("field_code_list", FieldCodeList.class);
    }
}
