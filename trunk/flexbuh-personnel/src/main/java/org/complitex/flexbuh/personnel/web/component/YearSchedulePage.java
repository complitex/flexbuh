package org.complitex.flexbuh.personnel.web.component;

import org.apache.wicket.PageReference;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.complitex.flexbuh.common.template.TemplatePage;
import org.complitex.flexbuh.resources.WebCommonResourceInitializer;
import org.complitex.flexbuh.resources.theme.ThemeResourceReference;
import org.odlabs.wiquery.core.resources.CoreJavaScriptResourceReference;

/**
 * @author Pavel Sknar
 *         Date: 11.10.12 15:19
 */
public class YearSchedulePage extends WebPage {

    public YearSchedulePage(final PageReference modalWindowPage, final ModalWindow window) {

    }

    public YearSchedulePage(PageParameters parameters) {
        super(parameters);

    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.renderJavaScriptReference(WebCommonResourceInitializer.JS_COMMON);
        response.renderJavaScriptReference(CoreJavaScriptResourceReference.get());
        response.renderJavaScriptReference(new PackageResourceReference(TemplatePage.class, "TemplatePage.js"));

        response.renderCSSReference(WebCommonResourceInitializer.CSS_STYLE);
        response.renderCSSReference(WebCommonResourceInitializer.CSS_FLEXBUH);
        response.renderCSSReference(new ThemeResourceReference());
    }
}
