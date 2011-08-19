package org.complitex.flexbuh.document.web;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.template.TemplatePage;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.08.11 15:25
 */
public class DeclarationFormPage extends TemplatePage{
    public DeclarationFormPage(PageParameters pageParameters) {
        add(CSSPackageResource.getHeaderContribution(DeclarationFormPage.class, "declaration.css"));

        String name = pageParameters.getString("name");

        add(new Label("title", name));

        Form form = new Form("form");
        add(form);

        DeclarationFormComponent declaration = new DeclarationFormComponent("declaration", name, new Declaration());
        form.add(declaration);
    }
}
