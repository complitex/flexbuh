package org.complitex.flexbuh.admin.importexport.web;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.common.template.ITemplateLink;
import org.complitex.flexbuh.common.template.ResourceTemplateLink;
import org.complitex.flexbuh.common.template.ResourceTemplateMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Pavel Sknar
 *         Date: 18.08.11 17:04
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class DictionaryMenu extends ResourceTemplateMenu {

    @Override
    public String getTitle(Locale locale) {
        return getString("title", locale);
    }

    @Override
    public List<ITemplateLink> getTemplateLinks(Locale locale) {
        List<ITemplateLink> templateLinks = new ArrayList<ITemplateLink>();

        templateLinks.add(new ResourceTemplateLink("import_dictionary", this, ImportDictionary.class));
        templateLinks.add(new ResourceTemplateLink("currency_list", this, CurrencyList.class));
        templateLinks.add(new ResourceTemplateLink("document_list", this, DocumentList.class));
        templateLinks.add(new ResourceTemplateLink("document_term_list", this, DocumentTermList.class));
        templateLinks.add(new ResourceTemplateLink("document_version_list", this, DocumentVersionList.class));
        templateLinks.add(new ResourceTemplateLink("region_list", this, RegionList.class));
        templateLinks.add(new ResourceTemplateLink("tax_inspection_list", this, TaxInspectionList.class));
        templateLinks.add(new ResourceTemplateLink("field_code_list", this, FieldCodeList.class));

        return templateLinks;
    }

    @Override
    public String getTagId() {
        return "DictionaryMenu";
    }
}
