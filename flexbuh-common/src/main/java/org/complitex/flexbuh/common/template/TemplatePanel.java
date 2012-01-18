package org.complitex.flexbuh.common.template;

import org.apache.wicket.markup.html.panel.Panel;
import org.complitex.flexbuh.common.security.CookieWebSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 18.01.12 16:12
 */
public abstract class TemplatePanel extends Panel {
    private final static Logger log = LoggerFactory.getLogger(TemplatePanel.class);

    public TemplatePanel(String id) {
        super(id);
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

    protected String getStringFormat(String key, Object... args){
        try {
            return MessageFormat.format(getString(key), args);
        } catch (Exception e) {
            log.error("Ошибка форматирования файла свойств", e);
            return key;
        }
    }
}
