package org.complitex.flexbuh.web;

import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.cycle.RequestCycle;
import org.complitex.flexbuh.common.entity.user.User;
import org.complitex.flexbuh.common.security.CookieWebSession;
import org.complitex.flexbuh.common.service.user.UserBean;
import org.complitex.flexbuh.common.template.TemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 26.07.11 18:23
 */
public class WelcomePage extends TemplatePage {

    private static final Logger log = LoggerFactory.getLogger(WelcomePage.class);

    @EJB
    private UserBean userBean;


	@Override
	protected void onAfterRender() {

        if (((ServletWebRequest) RequestCycle.get().getRequest()).getContainerRequest().getUserPrincipal() != null) {
            String login = ((ServletWebRequest) RequestCycle.get().getRequest()).getContainerRequest().getUserPrincipal().getName();
            log.debug("Login: {}", login);
            User user = userBean.getUser(login);
            if (user != null) {
                CookieWebSession cookieWebSession = getCookieWebSession();
                user.setSessionId(cookieWebSession.getSessionId(true));
                userBean.update(user);
            }
        }

		super.onAfterRender();
	}
}
