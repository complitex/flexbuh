package org.complitex.flexbuh.web;

import org.apache.wicket.Page;
import org.complitex.flexbuh.common.template.TemplateWebApplication;
import org.complitex.flexbuh.common.template.pages.login.Login;
import org.complitex.flexbuh.document.web.DeclarationList;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 26.07.11 15:24
 */
public class FlexbuhWebApplication extends TemplateWebApplication{

    @Override
    protected void init() {
        super.init();

        mountPage("login", Login.class);

        //Development mode
        switch (getConfigurationType()){
            case DEVELOPMENT:
                //getResourceSettings().setResourceWatcher(new Nio2ModificationWatcher(this));
                getDebugSettings().setLinePreciseReportingOnAddComponentEnabled(true);
                getDebugSettings().setLinePreciseReportingOnNewComponentEnabled(true);
                getDebugSettings().setOutputComponentPath(true);
                getDebugSettings().setOutputMarkupContainerClassName(true);
                break;
        }
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return DeclarationList.class;
    }
}
