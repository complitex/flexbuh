package org.complitex.flexbuh.security;

import org.apache.wicket.*;
import org.apache.wicket.authorization.IUnauthorizedComponentInstantiationListener;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.authorization.strategies.role.IRoleCheckingStrategy;
import org.apache.wicket.authorization.strategies.role.RoleAuthorizationStrategy;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.complitex.flexbuh.template.pages.login.Login;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 22.07.2010 17:53:29
 *
 * Приложение Wicket, которое использует авторизацию сервлет контейнера.
 */
public abstract class ServletAuthWebApplication extends WebApplication
        implements IRoleCheckingStrategy, IUnauthorizedComponentInstantiationListener {
    private static final Logger log = LoggerFactory.getLogger(ServletAuthWebApplication.class);

    @Override
    protected void init() {
        super.init();
        getSecuritySettings().setAuthorizationStrategy(new RoleAuthorizationStrategy(this));
        getSecuritySettings().setUnauthorizedComponentInstantiationListener(this);
    }

    @Override
    public boolean hasAnyRole(Roles roles) {
        HttpServletRequest request = ((WebRequestCycle) RequestCycle.get()).getWebRequest().getHttpServletRequest();
        if (roles != null) {
            for (String role : roles) {
                if (request.isUserInRole(role)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasAnyRole(String... roles) {
        return hasAnyRole(new Roles(roles));
    }

    @Override
    public void onUnauthorizedInstantiation(Component component) {
        WebRequestCycle webRequestCycle = (WebRequestCycle) RequestCycle.get();
        HttpServletRequest servletRequest = webRequestCycle.getWebRequest().getHttpServletRequest();

        if (servletRequest.getUserPrincipal() == null) {
            RequestCycle.get().setRedirect(true);
            Session.get().invalidate();
            throw new RestartResponseException(Login.class);
        } else {
            throw new UnauthorizedInstantiationException(component.getClass());
        }
    }

    /**
     * Helper method in order for logout. Must be used in pages where logout action is required.
     */
    public void logout() {
        Session.get().invalidateNow();
        RequestCycle.get().setResponsePage(getHomePage());
    }

    @Override
    public Session newSession(Request request, Response response) {
        return new CookieWebSession(request);
    }
}
