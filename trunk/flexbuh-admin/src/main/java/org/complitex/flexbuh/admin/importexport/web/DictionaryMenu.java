package org.complitex.flexbuh.admin.importexport.web;

import org.apache.wicket.Page;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.common.template.ITemplateLink;
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

        templateLinks.add(
                new ITemplateLink() {
                    @Override
                    public String getLabel(Locale locale) {
                        return getString("import_dictionary", locale);
                    }

                    @Override
                    public Class<? extends Page> getPage() {
                        return ImportDictionary.class;
                    }

                    @Override
                    public PageParameters getParameters() {
                        return null;
                    }

                    @Override
                    public String getTagId() {
                        return null;
                    }
                }
        );

		templateLinks.add(
				new ITemplateLink() {
                    @Override
                    public String getLabel(Locale locale) {
                        return getString("currency_list", locale);
                    }

                    @Override
                    public Class<? extends Page> getPage() {
                        return CurrencyList.class;
                    }

                    @Override
                    public PageParameters getParameters() {
                        return null;
                    }

                    @Override
                    public String getTagId() {
                        return null;
                    }
                }
		);

		templateLinks.add(
				new ITemplateLink() {
                    @Override
                    public String getLabel(Locale locale) {
                        return getString("document_list", locale);
                    }

                    @Override
                    public Class<? extends Page> getPage() {
                        return DocumentList.class;
                    }

                    @Override
                    public PageParameters getParameters() {
                        return null;
                    }

                    @Override
                    public String getTagId() {
                        return null;
                    }
                }
		);

		templateLinks.add(
				new ITemplateLink() {
                    @Override
                    public String getLabel(Locale locale) {
                        return getString("document_term_list", locale);
                    }

                    @Override
                    public Class<? extends Page> getPage() {
                        return DocumentTermList.class;
                    }

                    @Override
                    public PageParameters getParameters() {
                        return null;
                    }

                    @Override
                    public String getTagId() {
                        return null;
                    }
                }
		);

		templateLinks.add(
				new ITemplateLink() {
                    @Override
                    public String getLabel(Locale locale) {
                        return getString("document_version_list", locale);
                    }

                    @Override
                    public Class<? extends Page> getPage() {
                        return DocumentVersionList.class;
                    }

                    @Override
                    public PageParameters getParameters() {
                        return null;
                    }

                    @Override
                    public String getTagId() {
                        return null;
                    }
                }
		);

		templateLinks.add(
				new ITemplateLink() {
                    @Override
                    public String getLabel(Locale locale) {
                        return getString("region_list", locale);
                    }

                    @Override
                    public Class<? extends Page> getPage() {
                        return RegionList.class;
                    }

                    @Override
                    public PageParameters getParameters() {
                        return null;
                    }

                    @Override
                    public String getTagId() {
                        return null;
                    }
                }
		);

		templateLinks.add(
				new ITemplateLink() {
                    @Override
                    public String getLabel(Locale locale) {
                        return getString("tax_inspection_list", locale);
                    }

                    @Override
                    public Class<? extends Page> getPage() {
                        return TaxInspectionList.class;
                    }

                    @Override
                    public PageParameters getParameters() {
                        return null;
                    }

                    @Override
                    public String getTagId() {
                        return null;
                    }
                }
		);

        return templateLinks;
    }

    @Override
    public String getTagId() {
        return "DictionaryMenu";
    }
}
