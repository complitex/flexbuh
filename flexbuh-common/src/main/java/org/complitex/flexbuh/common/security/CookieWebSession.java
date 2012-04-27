package org.complitex.flexbuh.common.security;

import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.util.crypt.Base64;
import org.complitex.flexbuh.common.entity.user.Session;
import org.complitex.flexbuh.common.entity.user.User;
import org.complitex.flexbuh.common.service.user.SessionBean;
import org.complitex.flexbuh.common.service.user.UserBean;
import org.complitex.flexbuh.common.util.EjbUtil;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 09.09.11 16:33
 */
public class CookieWebSession extends WebSession {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(CookieWebSession.class);

    public static final String SESSION_COOKIE_NAME = "FLEXBUH_SESSION";
    private static final int SESSION_MAX_AGE = 2592000;

    private Session session;
    private String login;

    public CookieWebSession(Request request, String login) {
        super(request);
        this.login = login;
    }

    public Long getSessionId() {
        if (session == null){
            session = getSession();
        }

        return session != null ? session.getId() : null;

    }

    public SessionBean getSessionBean(){
        return EjbUtil.getBean(SessionBean.class);
    }

    public UserBean getUserBean(){
        return EjbUtil.getBean(UserBean.class);
    }

    protected Session getSession() {

        WebResponse webResponse = ((WebResponse) RequestCycle.get().getResponse());
        WebRequest webRequest = (WebRequest) RequestCycle.get().getRequest();

        // Если пользователь не анонимный, то берем сессию из его атрибутов
        // или создаем новую сессию (без куки) и прописываем ее в атрибуты пользователя.
        if (login != null) {
            User user = getUserBean().getUser(login);

            if (user.getSessionId() != null) {
                Session session = getSessionBean().getSessionById(user.getSessionId());

                // Обновить информацио о последнем входе пользователя в систему
                updateLastAccessDateSession(session);

                return session;
            }

            Session session = createSession();

            user.setSessionId(session.getId());
            getUserBean().update(user);

            return session;
        }

        Cookie cookie = webRequest.getCookie(SESSION_COOKIE_NAME);

        // Пользователь анонимный и куки нет: создаем сессию с кукой.
        if (cookie == null) {

            String cookieValue = generateEncodeBase64MD5((new Date()).toString().getBytes());

            Session session = createSession(cookieValue);

            cookie = new Cookie(SESSION_COOKIE_NAME, session.getCookie());
            cookie.setMaxAge(SESSION_MAX_AGE);

            webResponse.addCookie(cookie);

            return session;

        }

        // Пользователь анонимный и кука есть: пытаемся найти на стороне сервера сессию,
        // если сессия не найдена, то создаем новую сессию и куку.
        Session session = getSessionBean().getSessionByCookie(cookie.getValue());

        if (session == null) {
            session = createSession(cookie.getValue());

            webResponse.clearCookie(cookie);
            webResponse.addCookie(new Cookie(SESSION_COOKIE_NAME, session.getCookie()));
        } else {
            // Обновить информацио о последнем входе пользователя в систему
            updateLastAccessDateSession(session);
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

    /**
     * Create session for authorize user
     *
     * @return session
     */
    private Session createSession() {
        Session session = new Session();
        getSessionBean().create(session);

        return session;
    }

    /**
     * Create session for anonymous user
     *
     * @param cookieValue cookie value
     * @return session
     */
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

    private void updateLastAccessDateSession(Session session) {
        session.setLastAccessDate(new Date());
        getSessionBean().updateLastAccessDate(session);
    }

    public String getLogin() {
        return login;
    }
}
