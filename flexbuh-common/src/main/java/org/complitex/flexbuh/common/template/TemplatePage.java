package org.complitex.flexbuh.common.template;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.string.Strings;
import org.complitex.flexbuh.common.security.CookieWebSession;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.common.service.ResourceService;
import org.complitex.flexbuh.common.service.UserSessionService;
import org.complitex.flexbuh.common.service.user.SessionBean;
import org.complitex.flexbuh.common.template.pages.login.Login;
import org.complitex.flexbuh.common.template.toolbar.HelpButton;
import org.complitex.flexbuh.common.template.toolbar.ToolbarButton;
import org.complitex.flexbuh.common.util.ResourceUtil;
import org.complitex.flexbuh.common.web.component.PersonProfileChoice;
import org.complitex.flexbuh.resources.WebCommonResourceInitializer;
import org.complitex.flexbuh.resources.theme.ThemeResourceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.text.MessageFormat;
import java.util.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 22.07.2010 16:09:45
 *
 * Суперкласс шаблон для отображения содержания страниц.
 * Для инициализации шаблона наследники должны вызывать метод super().
 */
public abstract class TemplatePage extends WebPage {
    private static final Logger log = LoggerFactory.getLogger(TemplatePage.class);

    private Set<String> resourceBundle = new HashSet<String>();

	@EJB
	private SessionBean sessionBean;
    
    @EJB
    private ResourceService resourceService;

    @EJB
    private UserSessionService userSessionBean;

    protected TemplatePage(PageParameters parameters) {
        this();
    }

    protected TemplatePage() {
        add(new Link("home") {

            @Override
            public void onClick() {
                setResponsePage(getApplication().getHomePage());
            }
        });

        //toolbar
        WebMarkupContainer toolbar = new WebMarkupContainer("toolbar");
        add(toolbar);
        WebMarkupContainer commonPart = new WebMarkupContainer("commonPart");
        toolbar.add(commonPart);

        //add common buttons.
        HelpButton help = new HelpButton("help");
        commonPart.add(help);

        //add page custom buttons.
        List<? extends ToolbarButton> pageToolbarButtonsList = getToolbarButtons("pageToolbarButton");
        if (pageToolbarButtonsList == null) {
            pageToolbarButtonsList = Collections.emptyList();
        }
        Component pagePart = new ListView<ToolbarButton>("pagePart", pageToolbarButtonsList) {

            @Override
            protected void populateItem(ListItem<ToolbarButton> item) {
                item.add(item.getModelObject());
            }
        };
        toolbar.add(pagePart);

        //menu
        add(new ListView<ITemplateMenu>("sidebar", newTemplateMenus()) {

            @Override
            protected void populateItem(ListItem<ITemplateMenu> item) {
                item.add(new TemplateMenu("menu_placeholder", "menu", this, item.getModelObject()));
            }
        });
        
        //Feedback
//        add(new FeedbackCreate("feedback"));

//        todo empty panel
        add(new Label("current_user_fullname", "id: " + getSessionId()));
        add(new EmptyPanel("current_user_department"));
        add(new PersonProfileChoice("profile", getSessionId()));

        add(new BookmarkablePageLink<>("login", Login.class).setVisible(!isUserAuthorized()));
        add(new Form("exit") {

            @Override
            public void onSubmit() {
                getTemplateWebApplication().logout();
            }
        }.setVisible(isUserAuthorized()));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.renderJavaScriptReference(WebCommonResourceInitializer.COMMON_JS);
        response.renderJavaScriptReference(new PackageResourceReference(TemplatePage.class, "TemplatePage.js"));

        response.renderCSSReference(WebCommonResourceInitializer.STYLE_CSS);
        response.renderCSSReference(new ThemeResourceReference());
    }

    /**
     * Боковая панель с меню, которое устанавливается в конфигурационном файле.
     */
    private class TemplateMenu extends Fragment {
        public TemplateMenu(String id, String markupId, MarkupContainer markupProvider, ITemplateMenu menu) {
            super(id, markupId, markupProvider);

            setMarkupId(menu.getTagId());
            setOutputMarkupId(true);

            add(new Label("menu_title", menu.getTitle(getLocale())));
            add(new ListView<ITemplateLink>("menu_items", menu.getTemplateLinks(getLocale())) {

                @Override
                protected void populateItem(ListItem<ITemplateLink> item) {
                    final ITemplateLink templateLink = item.getModelObject();
                    BookmarkablePageLink link = new BookmarkablePageLink<Class<? extends Page>>("link", templateLink.getPage(),
                            templateLink.getParameters()) {

                        @Override
                        protected void onComponentTag(ComponentTag tag) {
                            super.onComponentTag(tag);
                            if (!Strings.isEmpty(templateLink.getTagId())) {
                                setMarkupId(templateLink.getTagId());
                            }
                        }
                    };
                    link.add(new Label("label", templateLink.getLabel(getLocale())));
                    item.add(link);
                }
            });
        }
    }

    private List<ITemplateMenu> newTemplateMenus() {
        List<ITemplateMenu> templateMenus = new ArrayList<ITemplateMenu>();
        for (Class<ITemplateMenu> menuClass : getTemplateWebApplication().getMenuClasses()) {
            if (isTemplateMenuAuthorized(menuClass)) {
                try {
                    ITemplateMenu templateMenu = menuClass.newInstance();
                    templateMenus.add(templateMenu);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return templateMenus;
    }

    /**
     * Проверка роли пользователя для отображения меню модуля.
     * @param menuClass Класс меню.
     * @return Отображать ли меню пользователю в зависимости от его роли.
     */
    private boolean isTemplateMenuAuthorized(Class<?> menuClass) {
        boolean authorized = true;

        final AuthorizeInstantiation classAnnotation = menuClass.getAnnotation(AuthorizeInstantiation.class);
        if (classAnnotation != null) {
            authorized = getTemplateWebApplication().hasAnyRole(classAnnotation.value());
        }

        return authorized;
    }

    /**
     * Subclass can override method in order to specify custom page toolbar buttons.
     * @param id Component id
     * @return List of ToolbarButton to add to Template
     */
    protected List<? extends ToolbarButton> getToolbarButtons(String id) {
        return null;
    }

    protected boolean hasAnyRole(String... roles) {
        return getTemplateWebApplication().hasAnyRole(roles);
    }

    protected TemplateWebApplication getTemplateWebApplication() {
        return (TemplateWebApplication) getApplication();
    }

    protected String getStringOrKey(String key) {
        if (key == null){
            return "";
        }

        try {
            return getString(key);
        } catch (MissingResourceException e) {
            //resource is not found
        }

        for (String bundle : resourceBundle){
            try {
                return ResourceUtil.getString(bundle, key, getLocale());
            } catch (MissingResourceException e) {
                //resource is not found
            }
        }

        return key;
    }

    protected String getStringOrKey(Enum key) {
        return key != null ? getStringOrKey(key.name()) : "";
    }

    protected String getStringFormat(String key, Object... args){
        try {
            return MessageFormat.format(getString(key), args);
        } catch (Exception e) {
            log.error("Ошибка форматирования файла свойств", e);
            return key;
        }
    }
    
    protected String getString(Class _class, String key){
        try {
            return resourceService.getString(_class, key);
        } catch (Exception e) {
            return _class.getSimpleName() + "[" + key + "]";
        }
    }

    protected void addResourceBundle(String bundle){
        resourceBundle.add(bundle);
    }

    //todo migrate to resource service
    protected void addAllResourceBundle(Collection<String> bundle){
        resourceBundle.addAll(bundle);
    }

    public boolean isUserAuthorized(){
        return getTemplateWebApplication().hasAnyRole(SecurityRole.AUTHORIZED);
    }

    protected CookieWebSession getCookieWebSession(){
        return (CookieWebSession) getSession();
    }

    protected Long getSessionId(boolean create){
        return getCookieWebSession().getSessionId(create);
    }

    protected Long getSessionId(){
        return getCookieWebSession().getSessionId(false);
    }
}