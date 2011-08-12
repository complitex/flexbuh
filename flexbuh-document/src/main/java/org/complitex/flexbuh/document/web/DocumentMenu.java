package org.complitex.flexbuh.document.web;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.complitex.flexbuh.template.ITemplateLink;
import org.complitex.flexbuh.template.ResourceTemplateMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.08.11 15:08
 */
public class DocumentMenu extends ResourceTemplateMenu{
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
                        return getString("create_document", locale);
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

        return templateLinks;
    }

    @Override
    public String getTagId() {
        return "DocumentMenu";
    }
}
