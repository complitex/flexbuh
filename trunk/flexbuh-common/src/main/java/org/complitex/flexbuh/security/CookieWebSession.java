package org.complitex.flexbuh.security;

import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.util.crypt.Base64;
import org.complitex.flexbuh.entity.user.Session;
import org.complitex.flexbuh.service.user.SessionBean;
import org.complitex.flexbuh.util.EjbUtil;

import javax.servlet.http.Cookie;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 09.09.11 16:33
 */
public class CookieWebSession extends WebSession{
    public static final String SESSION_COOKIE_NAME = "FLEXBUH_SESSION";

    private Session session;

    public CookieWebSession(Request request) {
        super(request);
    }

    public Long getSessionId(boolean create){
        if (session == null){
            session = getSession(create);
        }

        return session != null ? session.getId() : null;
    }

    public SessionBean getSessionBean(){
        return EjbUtil.getBean(SessionBean.class);
    }

    protected Session getSession(boolean create) {
        Session session = null;

        Cookie cookie = ((WebRequest)RequestCycle.get().getRequest()).getCookie(SESSION_COOKIE_NAME);

        if (cookie == null) {
            if (create) {
                String cookieValue = generateEncodeBase64MD5((new Date()).toString().getBytes());

                session = createSession(cookieValue);

                ((WebResponse) RequestCycle.get().getResponse()).addCookie(new Cookie(SESSION_COOKIE_NAME, session.getCookie()));
            }
        } else {
            session = getSessionBean().getSessionByCookie(cookie.getValue());

            if (session == null && create) {
                session = createSession(cookie.getValue());

                ((WebResponse) RequestCycle.get().getResponse()).clearCookie(cookie);
                ((WebResponse) RequestCycle.get().getResponse()).addCookie(new Cookie(SESSION_COOKIE_NAME, session.getCookie()));
            }
        }

        return session;
    }

    private String generateEncodeBase64MD5(byte[] bytes) {
        MessageDigest digest;

        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        digest.update(bytes);

        return new String(Base64.encodeBase64(digest.digest()));
    }

    private Session createSession(String cookieValue) {
        SessionBean sessionBean = getSessionBean();

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
