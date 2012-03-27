package org.complitex.flexbuh.common.template.pages.login;

import org.apache.wicket.Session;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.complitex.flexbuh.resources.WebCommonResourceInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 22.07.2010 16:16:45
 */
public final class Login extends WebPage {

    private static final Logger log = LoggerFactory.getLogger(Login.class);


    public Login() {
        init(false);
    }

    public Login(PageParameters pageParameters) {
        init(true);
    }

    private void init(boolean isError) {
        closePreviousSession();
        add(new Label("login.title", new ResourceModel("login.title")));
        add(new Label("login.header", new ResourceModel(isError ? "login.errorLabel" : "login.enterLabel")));
        WebMarkupContainer errorPanel = new WebMarkupContainer("errorPanel");
        errorPanel.setVisible(isError);
        add(errorPanel);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.renderJavaScriptReference(WebCommonResourceInitializer.JS_COMMON);
        response.renderJavaScriptReference(new PackageResourceReference(getClass(), "Login.js"));
        response.renderCSSReference(WebCommonResourceInitializer.CSS_STYLE);
    }

    private void closePreviousSession() {
        Session.get().invalidateNow();
    }
}

