package org.complitex.flexbuh.common.security;

import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.Session;
import org.apache.wicket.authorization.IUnauthorizedComponentInstantiationListener;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.authroles.authorization.strategies.role.IRoleCheckingStrategy;
import org.apache.wicket.authroles.authorization.strategies.role.RoleAuthorizationStrategy;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.complitex.flexbuh.common.template.pages.login.Login;
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
        HttpServletRequest request = ((ServletWebRequest) RequestCycle.get().getRequest()).getContainerRequest();
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
        WebRequest servletWebRequest = (WebRequest) RequestCycle.get().getRequest();
        //todo test
        HttpServletRequest servletRequest = (HttpServletRequest) servletWebRequest.getContainerRequest();

        if (servletRequest.getUserPrincipal() == null) {
            //todo redirect
//            RequestCycle.get().getResponse().setRedirect(true);
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
        WebRequest servletWebRequest = (WebRequest) RequestCycle.get().getRequest();
        //todo test
        HttpServletRequest servletRequest = (HttpServletRequest) servletWebRequest.getContainerRequest();

        return new CookieWebSession(request, servletRequest.getUserPrincipal());
    }
}
