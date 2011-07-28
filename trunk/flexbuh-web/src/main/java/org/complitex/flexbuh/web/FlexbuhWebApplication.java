package org.complitex.flexbuh.web;

import org.apache.wicket.Page;
import org.complitex.flexbuh.document.web.ImportTemplatePage;
import org.complitex.template.web.ComplitexWebApplication;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 26.07.11 15:24
 */
public class FlexbuhWebApplication extends ComplitexWebApplication{

    @Override
    protected void init() {
        super.init();

        mountBookmarkablePage("test", ImportTemplatePage.class);
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return WelcomePage.class;
    }
}
