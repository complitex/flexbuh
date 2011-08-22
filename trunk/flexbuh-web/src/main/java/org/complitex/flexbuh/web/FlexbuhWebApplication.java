package org.complitex.flexbuh.web;

import org.apache.wicket.Page;
import org.complitex.flexbuh.template.TemplateWebApplication;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 26.07.11 15:24
 */
public class FlexbuhWebApplication extends TemplateWebApplication{

    @Override
    protected void init() {
        super.init();

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
