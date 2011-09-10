package org.complitex.flexbuh.admin.user.web;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.complitex.flexbuh.template.ITemplateLink;
import org.complitex.flexbuh.template.ResourceTemplateMenu;
import org.complitex.flexbuh.template.pages.ConfigEdit;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Pavel Sknar
 *         Date: 03.09.11 10:15
 */
public class UserMenu extends ResourceTemplateMenu {

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
                        return getString("config", locale);
                    }

                    @Override
                    public Class<? extends Page> getPage() {
                        return ConfigEdit.class;
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
                        return getString("user_profile", locale);
                    }

                    @Override
                    public Class<? extends Page> getPage() {
                        return UserProfileView.class;
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
                        return getString("create_company_profile", locale);
                    }

                    @Override
                    public Class<? extends Page> getPage() {
                        return JuridicalPersonProfileCreate.class;
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
        return "UserMenu";
    }
}
