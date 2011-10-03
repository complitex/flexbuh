package org.complitex.flexbuh.template.pages.expired;

import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.ResourceModel;
import org.complitex.flexbuh.resources.WebCommonResourceInitializer;
import org.complitex.flexbuh.template.TemplateWebApplication;

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
        response.renderJavaScriptReference(WebCommonResourceInitializer.COMMON_JS);
        response.renderCSSReference(WebCommonResourceInitializer.STYLE_CSS);
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

