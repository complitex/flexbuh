package org.complitex.flexbuh.template;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.JavascriptPackageResource;
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
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.util.crypt.Base64;
import org.apache.wicket.util.string.Strings;
import org.complitex.flexbuh.entity.user.Session;
import org.complitex.flexbuh.resources.WebCommonResourceInitializer;
import org.complitex.flexbuh.security.CookieWebSession;
import org.complitex.flexbuh.security.SecurityRole;
import org.complitex.flexbuh.service.user.SessionBean;
import org.complitex.flexbuh.template.toolbar.HelpButton;
import org.complitex.flexbuh.template.toolbar.ToolbarButton;
import org.complitex.flexbuh.util.ResourceUtil;
import org.odlabs.wiquery.core.commons.CoreJavaScriptResourceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.servlet.http.Cookie;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    private String page = getClass().getName();

    private Set<String> resourceBundle = new HashSet<String>();

	@EJB
	private SessionBean sessionBean;

    protected TemplatePage() {
        add(JavascriptPackageResource.getHeaderContribution(CoreJavaScriptResourceReference.get()));
        add(JavascriptPackageResource.getHeaderContribution(WebCommonResourceInitializer.COMMON_JS));
        add(JavascriptPackageResource.getHeaderContribution(TemplatePage.class, TemplatePage.class.getSimpleName() + ".js"));
        add(CSSPackageResource.getHeaderContribution(WebCommonResourceInitializer.STYLE_CSS));

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


//        todo empty panel
        add(new EmptyPanel("current_user_fullname"));
        add(new EmptyPanel("current_user_department"));
        add(new EmptyPanel("profile"));


        add(new Form("exit") {

            @Override
            public void onSubmit() {
                getTemplateWebApplication().logout();
            }
        }.setVisible(isUserAuthorized()));
    }

    /**
     * Боковая панель с меню, которое устанавливается в конфигурационном файле.
     */
    private class TemplateMenu extends Fragment {

        private String tagId;

        public TemplateMenu(String id, String markupId, MarkupContainer markupProvider, ITemplateMenu menu) {
            super(id, markupId, markupProvider);
            this.tagId = menu.getTagId();

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
                                tag.put("id", templateLink.getTagId());
                            }
                        }
                    };
                    link.add(new Label("label", templateLink.getLabel(getLocale())));
                    item.add(link);
                }
            });
        }

        @Override
        protected void onComponentTag(ComponentTag tag) {
            super.onComponentTag(tag);
            if (!Strings.isEmpty(tagId)) {
                tag.put("id", tagId);
            }
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

    protected void addResourceBundle(String bundle){
        resourceBundle.add(bundle);
    }

    protected void addAllResourceBundle(Collection<String> bundle){
        resourceBundle.addAll(bundle);
    }

    public boolean isUserAuthorized(){
        return getTemplateWebApplication().hasAnyRole(SecurityRole.AUTHORIZED);
    }

    public CookieWebSession getCookieWebSession(){
        return (CookieWebSession) getSession();
    }

    public Long getSessionId(boolean create){
        return getCookieWebSession().getSessionId(create);
    }

	protected Session getUserSession() {
		Session session;
		Cookie cookie = ((WebRequest)getRequestCycle().getRequest()).getCookie(UserConstants.SESSION_COOKIE_NAME);
		if (cookie == null) {
			String cookieValue = generateEncodeBase64MD5((new Date()).toString().getBytes());
			if (cookieValue == null) return null;

			session = createSession(cookieValue);

			((WebResponse) RequestCycle.get().getResponse()).addCookie(new Cookie(UserConstants.SESSION_COOKIE_NAME, session.getCookie()));

			//String s = personProfile.get
		} else {
			session = sessionBean.getSessionByCookie(cookie.getValue());
			if (session == null) {
				session = createSession(cookie.getValue());
				((WebResponse) RequestCycle.get().getResponse()).clearCookie(cookie);
				((WebResponse) RequestCycle.get().getResponse()).addCookie(new Cookie(UserConstants.SESSION_COOKIE_NAME, session.getCookie()));
			}
			//Validate.isTrue(session != null, "Can not find session with cookie: '" + cookie.getValue() + "'");
		}
		return session;
	}

	private String generateEncodeBase64MD5(byte[] bytes) {
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
		digest.update(bytes);
		return new String(Base64.encodeBase64(digest.digest()));
	}

	private Session createSession(String cookieValue) {
		if (sessionBean.getSessionByCookie(cookieValue) != null) {
			do {
				cookieValue = generateEncodeBase64MD5((cookieValue + new Date().toString()).getBytes());
			} while (sessionBean.getSessionByCookie(cookieValue) != null);
		}
		Session session = new Session();
		session.setCookie(cookieValue);

		sessionBean.create(session);
		return session;
	}
}