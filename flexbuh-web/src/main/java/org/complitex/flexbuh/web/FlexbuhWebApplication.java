package org.complitex.flexbuh.web;

import org.apache.wicket.Page;
import org.complitex.flexbuh.document.test.MarkupTestPage;
import org.complitex.flexbuh.document.test.TransformationTestPage;
import org.complitex.flexbuh.document.test.ValidationTestPage;
import org.complitex.flexbuh.template.TemplateWebApplication;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 26.07.11 15:24
 */
public class FlexbuhWebApplication extends TemplateWebApplication{

    @Override
    protected void init() {
        super.init();

        mountBookmarkablePage("test2", TransformationTestPage.class);
        mountBookmarkablePage("test3", MarkupTestPage.class);
        mountBookmarkablePage("test4", ValidationTestPage.class);

        getDebugSettings().setComponentUseCheck(true);
        getDebugSettings().setLinePreciseReportingOnAddComponentEnabled(true);
        getDebugSettings().setLinePreciseReportingOnNewComponentEnabled(true);
        getDebugSettings().setOutputComponentPath(true);
        getDebugSettings().setOutputMarkupContainerClassName(true);
        getDebugSettings().setDevelopmentUtilitiesEnabled(true);

    }

    @Override
    public Class<? extends Page> getHomePage() {
        return WelcomePage.class;
    }
}
