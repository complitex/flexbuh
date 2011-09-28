package org.complitex.flexbuh.template;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.Session;
import org.complitex.flexbuh.inject.JavaEE6ModuleNamingStrategy;
import org.complitex.flexbuh.resources.theme.ThemeResourceReference;
import org.complitex.flexbuh.security.ServletAuthWebApplication;
import org.odlabs.wiquery.ui.themes.IThemableApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.javaee.injection.JavaEEComponentInjector;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 22.07.2010 18:36:29
 */
public abstract class TemplateWebApplication extends ServletAuthWebApplication implements IThemableApplication {

    private static final Logger log = LoggerFactory.getLogger(TemplateWebApplication.class);
    private static final String TEMPLATE_CONFIG_FILE_NAME = "template-config.xml";
    private List<Class<ITemplateMenu>> menuClasses;
    private final static ThemeResourceReference theme = new ThemeResourceReference();

    @Override
    protected void init() {
        super.init();

        initializeTemplateConfig();

        addComponentInstantiationListener(new JavaEEComponentInjector(this, new JavaEE6ModuleNamingStrategy()));

//        getApplicationSettings().setPageExpiredErrorPage(SessionExpiredPage.class);
        getApplicationSettings().setPageExpiredErrorPage(getHomePage());
    }

    @Override
    public ResourceReference getTheme(Session session) {
        return theme;
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    private void initializeTemplateConfig() throws RuntimeException {
        try {
            Iterator<URL> resources = getApplicationSettings().getClassResolver().getResources(TEMPLATE_CONFIG_FILE_NAME);
            InputStream inputStream;
            if (resources.hasNext()) {
                inputStream = resources.next().openStream();
                if (resources.hasNext()) {
                    log.warn("There are more than one template config {} files. What file will be picked is unpredictable.",
                            TEMPLATE_CONFIG_FILE_NAME);
                }
            } else {
                throw new RuntimeException("Template config file " + TEMPLATE_CONFIG_FILE_NAME + " was not found.");
            }
            TemplateLoader templateLoader = new TemplateLoader(inputStream);
            List<String> menuClassNames = templateLoader.getMenuClassNames();

            menuClasses = new ArrayList<>();
            for (String menuClassName : menuClassNames) {
                try {
                    Class menuClass = getApplicationSettings().getClassResolver().resolveClass(menuClassName);
                    menuClasses.add(menuClass);
                } catch (Exception e) {
                    log.warn("Меню не найдено : {}", menuClassName);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Class<ITemplateMenu>> getMenuClasses() {
        return menuClasses;
    }
}
