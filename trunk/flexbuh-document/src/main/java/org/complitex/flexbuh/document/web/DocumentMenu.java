package org.complitex.flexbuh.document.web;

import org.apache.wicket.Page;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.flexbuh.common.template.ITemplateLink;
import org.complitex.flexbuh.common.template.ResourceTemplateMenu;

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
                        return getString("person_profile_list", locale);
                    }

                    @Override
                    public Class<? extends Page> getPage() {
                        return PersonProfileList.class;
                    }

                    @Override
                    public PageParameters getParameters() {
                        return null;
                    }

                    @Override
                    public String getTagId() {
                        return "person_profile_list";
                    }
                }
        );

        templateLinks.add(
                new ITemplateLink() {
                    @Override
                    public String getLabel(Locale locale) {
                        return getString("declaration_list", locale);
                    }

                    @Override
                    public Class<? extends Page> getPage() {
                        return DeclarationList.class;
                    }

                    @Override
                    public PageParameters getParameters() {
                        return null;
                    }

                    @Override
                    public String getTagId() {
                        return "declaration_list";
                    }
                }
        );

        templateLinks.add(
                new ITemplateLink() {
                    @Override
                    public String getLabel(Locale locale) {
                        return getString("counterpart_list", locale);
                    }

                    @Override
                    public Class<? extends Page> getPage() {
                        return CounterpartList.class;
                    }

                    @Override
                    public PageParameters getParameters() {
                        return null;
                    }

                    @Override
                    public String getTagId() {
                        return "counterpart_list";
                    }
                }
        );

        templateLinks.add(
                new ITemplateLink() {
                    @Override
                    public String getLabel(Locale locale) {
                        return getString("employee_list", locale);
                    }

                    @Override
                    public Class<? extends Page> getPage() {
                        return EmployeeList.class;
                    }

                    @Override
                    public PageParameters getParameters() {
                        return null;
                    }

                    @Override
                    public String getTagId() {
                        return "employee_list";
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