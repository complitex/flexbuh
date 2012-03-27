package org.complitex.flexbuh.common.template.pages.expired;

import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.ResourceModel;
import org.complitex.flexbuh.common.template.TemplateWebApplication;
import org.complitex.flexbuh.resources.WebCommonResourceInitializer;

/**
 *
 * @author Artem
 */
public final class SessionExpiredPage extends WebPage {

    public SessionExpiredPage() {
        init();
    }

    private void init() {
        add(new Label("title", new ResourceModel("session_expired.title")));
        add(new Link("homePageLink") {

            @Override
            public void onClick() {
                ((TemplateWebApplication) getApplication()).logout();
            }
        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.renderJavaScriptReference(WebCommonResourceInitializer.JS_COMMON);
        response.renderCSSReference(WebCommonResourceInitializer.CSS_STYLE);
    }

    /**
     * @see org.apache.wicket.Component#isVersioned()
     */
    @Override
    public boolean isVersioned() {
        return false;
    }

    /**
     * @see org.apache.wicket.Page#isErrorPage()
     */
    @Override
    public boolean isErrorPage() {
        return true;
    }
}

