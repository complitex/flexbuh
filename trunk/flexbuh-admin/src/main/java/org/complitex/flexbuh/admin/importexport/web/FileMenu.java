package org.complitex.flexbuh.admin.importexport.web;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.complitex.flexbuh.template.ITemplateLink;
import org.complitex.flexbuh.template.ResourceTemplateMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Pavel Sknar
 *         Date: 30.08.11 12:54
 */
public class FileMenu extends ResourceTemplateMenu {
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
                        return getString("import_file", locale);
                    }

                    @Override
                    public Class<? extends Page> getPage() {
                        return ImportFile.class;
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
        return "FileMenu";
    }
}
