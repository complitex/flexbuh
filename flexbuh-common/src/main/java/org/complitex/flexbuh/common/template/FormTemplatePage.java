package org.complitex.flexbuh.common.template;

import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 27.07.2010 16:51:09
 */
public class FormTemplatePage extends TemplatePage {

    private static final String UNFOCUSABLE_CSS_CLASS = "form-template-page-unfocusable";

    public FormTemplatePage() {
    }

    public FormTemplatePage(PageParameters parameters) {
        super(parameters);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderJavaScriptReference(new PackageResourceReference(FormTemplatePage.class, "FormTemplatePage.js"));
    }
}
