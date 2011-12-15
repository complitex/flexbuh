package org.complitex.flexbuh.personnel.web;

import org.apache.wicket.Page;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.flexbuh.common.template.ITemplateLink;
import org.complitex.flexbuh.common.template.ResourceTemplateMenu;
import org.complitex.flexbuh.common.web.TemporalEntityListPage;
import org.complitex.flexbuh.personnel.entity.Department;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.12.11 17:02
 */
public class PersonnelMenu extends ResourceTemplateMenu{
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
                        return getString("department", locale);
                    }

                    @Override
                    public Class<? extends Page> getPage() {
                        return TemporalEntityListPage.class;
                    }

                    @Override
                    public PageParameters getParameters() {
                        PageParameters pageParameters = new PageParameters();
                        pageParameters.add("entity", Department.class.getName());

                        return pageParameters;
                    }

                    @Override
                    public String getTagId() {
                        return "department_list";
                    }
                }
        );

        return templateLinks;
    }
}
