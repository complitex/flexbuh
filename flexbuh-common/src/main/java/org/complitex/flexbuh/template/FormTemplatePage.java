package org.complitex.flexbuh.template;

import org.apache.wicket.markup.html.JavascriptPackageResource;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 27.07.2010 16:51:09
 */
public class FormTemplatePage extends TemplatePage {

    private static final String UNFOCUSABLE_CSS_CLASS = "form-template-page-unfocusable";

    public FormTemplatePage() {
        add(JavascriptPackageResource.getHeaderContribution(FormTemplatePage.class, FormTemplatePage.class.getSimpleName() + ".js"));
    }
}
