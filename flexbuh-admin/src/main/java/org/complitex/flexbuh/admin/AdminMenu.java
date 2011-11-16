package org.complitex.flexbuh.admin;

import org.apache.wicket.Page;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.flexbuh.admin.web.FeedbackList;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.common.template.ITemplateLink;
import org.complitex.flexbuh.common.template.ResourceTemplateMenu;
import org.complitex.flexbuh.common.template.pages.ConfigEdit;
import org.complitex.flexbuh.logging.web.LogList;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Pavel Sknar
 *         Date: 03.09.11 10:15
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class AdminMenu extends ResourceTemplateMenu {

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
                        return getString("feedback", locale);
                    }

                    @Override
                    public Class<? extends Page> getPage() {
                        return FeedbackList.class;
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
                        return getString("log_list", locale);
                    }

                    @Override
                    public Class<? extends Page> getPage() {
                        return LogList.class;
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
