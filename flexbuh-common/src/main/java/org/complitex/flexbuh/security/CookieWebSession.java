package org.complitex.flexbuh.security;

import org.apache.wicket.Request;
import org.apache.wicket.protocol.http.WebSession;
import org.complitex.flexbuh.entity.user.Session;
import org.complitex.flexbuh.service.user.SessionBean;
import org.complitex.flexbuh.util.EjbUtil;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 09.09.11 16:33
 */
public class CookieWebSession extends WebSession{
    private Session session;

    public CookieWebSession(Request request) {
        super(request);
    }

    public Long getSessionId(boolean create){
        //todo implement session load

        return -1L;
    }

    public SessionBean getSessionBean(){
        return EjbUtil.getBean(SessionBean.class);
    }
}
